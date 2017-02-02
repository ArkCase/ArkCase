'use strict';

angular.module('admin').controller('Admin.OrganizationalHierarchyController', ['$scope'
    , 'Admin.OrganizationalHierarchyService', '$q', '$modal', 'MessageService', '$translate', 'Admin.ModalDialogService'
    , 'UtilService',
    function ($scope, organizationalHierarchyService, $q, $modal, messageService, $translate, modalDialogService, Util) {

        var UUIDRegExString = ".*-UUID-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

        var UUIDRegEx = new RegExp(UUIDRegExString);

        $scope.data = [];
        var groupsMap = {};

        $scope.enableEditingLdapUsers = false;

        organizationalHierarchyService.isEnabledEditingLdapUsers().then(function (isEnabledEditingLdapUsers) {
            $scope.enableEditingLdapUsers = isEnabledEditingLdapUsers;
        });

        $scope.config.$promise.then(function (config) {
            $scope.cfg = _.find(config.components, {id: 'usersPicker'});
            $scope.cfgOrgHierarchy = _.find(config.components, {id: 'orgHierarchy'});
        });

        function createTreeData(groups) {
            var source = [];

            //group details
            for (var i = 0; i < groups.length; i++) {
                var group = groups[i];
                addToTree(group);
            }
            return source;
        }

        function addToTree(group, top) {
            group.expanded = false;
            group.lazy = true;
            group.folder = true;
            group.title = group.name;
            if (group.supervisor_id_s) {
                if (group.supervisor_name_s)
                    group.supervisor = group.supervisor_name_s;
                else
                    group.supervisor = group.supervisor_id_s;
            }
            //init children array
            if (!group.children)
                group.children = [];
            if (!group.parent_id_s) {
                //add group to root
                if (top)
                    $scope.data.unshift(group);
                else
                    $scope.data.push(group);
            }

        }

        $scope.onLoadMore = function (currentPage, pageSize) {
            var groupsPromise;
            if ($scope.cfgOrgHierarchy && Util.isArray($scope.cfgOrgHierarchy.topLevelGroupTypes)) {
                groupsPromise = organizationalHierarchyService.getGroupsTopLevel(currentPage, pageSize, $scope.cfgOrgHierarchy.topLevelGroupTypes);
            }
            else {
                groupsPromise = organizationalHierarchyService.getGroupsTopLevel(currentPage, pageSize, []);
            }


            groupsPromise.then(function (payload) {
                var tempGroups = [];
                if (!Util.isArrayEmpty(_.get(payload, 'data.response.docs'))) {
                    tempGroups = _.get(payload, 'data.response.docs');
                }
                $scope.totalGroups = _.get(payload, 'data.response.numFound');
                //create map from groups
                for (var i = 0; i < tempGroups.length; i++) {
                    var tempGroup = tempGroups[i];
                    groupsMap[tempGroup.object_id_s] = tempGroup;
                }
                $scope.data = [];
                createTreeData(tempGroups);
            });

        };

        $scope.onAddSubGroup = function (parent) {
            var deffered = $q.defer();
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                controller: function ($scope, $modalInstance) {
                    $scope.inputValid = true;
                    $scope.adHocGroup = {};

                    //watch the input to enable/disable ok button
                    $scope.$watch('adHocGroup.name', function (newValue) {
                        if (newValue) {
                            $scope.inputValid = false;
                        } else {
                            $scope.inputValid = true;
                        }
                    }, true);
                    $scope.ok = function () {
                        $modalInstance.close($scope.adHocGroup);
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                },
                size: 'md'
            });

            //handle the result
            modalInstance.result.then(function (adHocGroup) {
                //button ok
                organizationalHierarchyService.addAdHocGroup(adHocGroup, parent).then(function (payload) {
                    //added successfully
                    var newGroup = payload.data;
                    newGroup.object_sub_type_s = 'ADHOC_GROUP';
                    newGroup.object_id_s = newGroup.name;
                    newGroup.parent_id_s = parent.object_id_s;
                    if (newGroup.supervisor) {
                        newGroup.supervisor = newGroup.supervisor.fullName;
                    }

                    //name that should be displayed in UI should not be unique across different tree levels,
                    // so the UUID part is removed!
                    newGroup.name = UUIDRegEx.test(newGroup.name) ? newGroup.name.substring(0, newGroup.name.lastIndexOf("-UUID-")) : newGroup.name;

                    groupsMap[newGroup.object_id_s] = newGroup;
                    if (!groupsMap[newGroup.parent_id_s].child_id_ss) {
                        groupsMap[newGroup.parent_id_s].child_id_ss = [];
                    }
                    groupsMap[newGroup.parent_id_s].child_id_ss.push(newGroup.object_id_s);

                    addToTree(newGroup, true);
                    deffered.resolve(newGroup);
                }, function () {
                    //error adding group
                    deffered.reject();
                });
            }, function (result) {
                //button cancel, nothing to do.
                deffered.reject();
            });
            return deffered.promise;
        };

        $scope.onDeleteGroup = function (group) {
            var deffered = $q.defer();
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.headerText') + group.name,
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.bodyText')
            };
            modalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.removeGroup(group).then(function (payload) {
                    deffered.resolve(payload);
                }, function (payload) {
                    deffered.reject(payload);
                });
            }, function () {
                //cancel btn
                deffered.reject();
            });
            return deffered.promise;
        };

        $scope.onDeleteMembers = function (group, members) {
            var deffered = $q.defer();
            members = [].concat(members);
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.headerText') + formatMembers(members),
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.bodyText')
            };

            modalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.removeMembers(group, members).then(function (payload) {
                    deffered.resolve(payload);
                }, function (payload) {
                    deffered.reject(payload);
                });
            }, function () {
                //cancel btn
                deffered.reject();
            });
            return deffered.promise;
        };

        $scope.onAddMembers = function (group) {
            var deffered = $q.defer();
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.org-hierarchy.users-picker.client.view.html',
                controller: 'Admin.UsersPicker',
                size: 'lg',
                resolve: {
                    $config: function () {
                        return $scope.cfg;
                    },
                    $filter: function () {
                        return "\"Object Type\": USER";
                    }
                }
            });

            modalInstance.result.then(function (membersSelected) {
                //ok button clicked
                var mappedMembers = [];
                if (Util.isArray(membersSelected)) {
                    for (var i = 0; i < membersSelected.length; i++) {
                        mappedMembers.push(mapMember(membersSelected[i]));
                    }
                }
                else {
                    mappedMembers.push(mapMember(membersSelected));
                }


                organizationalHierarchyService.saveMembers(group, mappedMembers).then(function (payload) {
                    //saving success
                    //map members
                    var mappedMembersMap = {};
                    angular.forEach(mappedMembers, function (memb) {
                        mappedMembersMap[memb.userId] = memb;
                    });
                    var members = payload.data.members;
                    var unmappedMembers = [];
                    for (var i = 0; i < members.length; i++) {
                        var unmappedMember = unMapMember(members[i]);
                        if (mappedMembersMap[unmappedMember.object_id_s])
                            unmappedMembers.push(unmappedMember);
                    }
                    deffered.resolve(unmappedMembers);
                }, function (payload) {
                    //saving error
                    deffered.reject();
                });
            }, function () {
                // Cancel button was clicked
                deffered.reject();
                return [];
            });
            return deffered.promise;
        };

        $scope.onAddLdapMember = function (group) {
            var deffered = $q.defer();
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.user = {};
                    $scope.data = {
                        "acmUser": $scope.user,
                        "groupName": group.object_id_s
                    };
                    $scope.ok = function () {
                        $modalInstance.close($scope.data);
                    };
                }],
                size: 'md'
            });

            modalInstance.result.then(function (data) {
                organizationalHierarchyService.addMemberToLdapGroup(data).then(function (member) {
                    //saving success
                    //map members
                    var unmappedMembers = [];
                    var unmappedMember = unMapMember(member);
                    unmappedMembers.push(unmappedMember);
                    deffered.resolve(unmappedMembers);
                }, function () {
                    //saving error
                    deffered.reject();
                });
            }, function () {
                // Cancel button was clicked
                deffered.reject("cancel");
                return [];
            });
            return deffered.promise;
        };

        $scope.onLazyLoad = function (event, groupNode) {

            var parentId = groupNode.object_id_s;
            var dfd = $q.defer();
            var group = groupsMap[parentId];
            var children = [];

            //find child groups
            var subGroupsPromise = organizationalHierarchyService.getSubGroupsForGroup(parentId);
            subGroupsPromise.then(function (payload) {
                var tempGroups = [];
                if (!Util.isArrayEmpty(_.get(payload, 'data.response.docs'))) {
                    tempGroups = _.get(payload, 'data.response.docs');
                }
                //create map from groups
                for (var i = 0; i < tempGroups.length; i++) {
                    var tempGroup = tempGroups[i];
                    groupsMap[tempGroup.object_id_s] = tempGroup;
                }
                createTreeData(tempGroups);

                if (group && group.child_id_ss) {
                    for (var i = 0; i < group.child_id_ss.length; i++) {
                        var groupId = group.child_id_ss[i];
                        children.push(getGroup(groupId));
                    }
                }

                //find child users
                if (group && group.member_id_ss) {
                    organizationalHierarchyService.getUsersForGroup(group.object_id_s.replace(/\./g, '_002E_')).then(function (payload) {
                        //successfully users received, insert with groups in same array
                        var data = _.get(payload, 'data.response.docs');
                        if (data) {
                            for (var i = 0; i < data.length; i++) {
                                data[i].title = data[i].name;
                                data[i].isMember = true;
                                children.push(data[i]);
                            }
                            group.children = children;
                        }
                        dfd.resolve(children);
                    }, function (payload) {
                        //error getting users
                        console.log("Error getting users: " + payload);

                        //be we still need to return sub groups which are included in children, that's why we are not using reject
                        group.children = children;
                        dfd.resolve(children);
                    });
                } else {
                    dfd.resolve(children);
                }
            });
            return dfd.promise;
        };

        function getGroup(id) {
            return groupsMap[id];
        }

        function mapMember(member) {
            var mapped = {};
            mapped["userId"] = member.object_id_s;
            mapped["fullName"] = member.name;
            mapped["firstName"] = member.first_name_lcs;
            mapped["lastName"] = member.last_name_lcs;
            mapped["created"] = member.create_date_tdt;
            mapped["modified"] = member.modified_date_tdt;
            mapped["userState"] = member.status_lcs;
            mapped["mail"] = member.email_lcs;
            return mapped;
        }

        function unMapMember(mapped) {
            var member = {};
            member.object_id_s = mapped["userId"];
            member.name = mapped["fullName"];
            member.first_name_lcs = mapped["firstName"];
            member.last_name_lcs = mapped["lastName"];
            member.create_date_tdt = mapped["created"];
            member.modified_date_tdt = mapped["modified"];
            member.status_lcs = mapped["userState"];
            member.email_lcs = mapped["mail"];
            member.title = member.name;
            member.isMember = true;
            return member;
        }

        $scope.createGroup = function () {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                controller: function ($scope, $modalInstance) {
                    $scope.inputInvalid = true;
                    $scope.adHocGroup = {};

                    //watch the input to enable/disable ok button
                    $scope.$watch('adHocGroup.name', function (newValue) {
                        if (newValue) {
                            $scope.inputInvalid = false;
                        } else {
                            $scope.inputInvalid = true;
                        }
                    }, true);
                    $scope.ok = function () {
                        $modalInstance.close($scope.adHocGroup);
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                },
                size: 'md'
            });

            //handle the result
            modalInstance.result.then(function (adHocGroup) {
                //button ok
                organizationalHierarchyService.addAdHocGroup(adHocGroup).then(function (payload) {
                    //added successfully
                    var newGroup = payload.data;
                    newGroup.object_sub_type_s = 'ADHOC_GROUP';
                    newGroup.object_id_s = payload.data.name;
                    if (payload.data.supervisor)
                        newGroup.supervisor = payload.data.supervisor.fullName;

                    //name that should be displayed in UI should not be unique across different tree levels,
                    // so the UUID part is removed!


                    newGroup.name = UUIDRegEx.test(payload.data.name) ? payload.data.name.substring(0, payload.data.name.lastIndexOf("-UUID-")) : payload.data.name;


                    groupsMap[payload.data.name] = newGroup;
                    addToTree(newGroup, true);
                    messageService.succsessAction();
                }, function () {
                    //error adding group
                    messageService.errorAction();
                });
            }, function (result) {
                //button cancel, nothing to do.
            });
        };

        function formatMembers(members) {
            var formattedMembers = [];
            for (var i = 0; i < members.length; i++) {
                formattedMembers.push(members[i].name);
            }
            return formattedMembers.join(', ');
        }

        $scope.onSetSupervisor = function (group) {
            var deffered = $q.defer();
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.org-hierarchy.user-picker.client.view.html',
                controller: 'Admin.UsersPicker',
                size: 'lg',
                resolve: {
                    $config: function () {
                        return $scope.cfg;
                    },
                    $filter: function () {
                        return "\"Object Type\": USER";
                    }
                }
            });

            modalInstance.result.then(function (memberSelected) {
                //ok button clicked
                organizationalHierarchyService.setSupervisor(group, mapMember(memberSelected)).then(function (payload) {
                    //saving success
                    deffered.resolve(payload.data);
                }, function (payload) {
                    //saving error
                    deffered.reject();
                });
            }, function () {
                // Cancel button was clicked
                deffered.reject();
                return [];
            });
            return deffered.promise;
        };
    }
]);