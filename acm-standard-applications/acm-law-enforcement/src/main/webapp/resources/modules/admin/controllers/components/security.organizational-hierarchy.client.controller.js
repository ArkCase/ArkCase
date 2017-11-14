'use strict';

angular.module('admin').controller('Admin.OrganizationalHierarchyController', ['$scope'
    , 'Admin.OrganizationalHierarchyService', '$q', '$modal', 'MessageService', '$translate', 'Admin.ModalDialogService'
    , 'UtilService', 'Admin.LdapConfigService',
    function ($scope, organizationalHierarchyService, $q, $modal, messageService, $translate, ModalDialogService, Util
        , LdapConfigService) {

        var UUIDRegExString = ".*-UUID-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

        var UUIDRegEx = new RegExp(UUIDRegExString);

        $scope.data = [];
        var groupsMap = {};

        $scope.addAdHocGroupBtn = "admin.security.organizationalHierarchy.createGroupDialog.adHocGroup.title";
        $scope.addLdapGroupBtn = "admin.security.organizationalHierarchy.createGroupDialog.ldapGroup.title";

        $scope.ldapEditingEnabledPerDirectoryServer = {};

        LdapConfigService.retrieveDirectories().then(function (directories) {
            $scope.directoryServers = _.map(directories.data, function (ds) {
                var dirId = ds["ldapConfig.id"];
                var dirEnabled = ds["ldapConfig.enableEditingLdapUsers"] === "true";
                $scope.ldapEditingEnabledPerDirectoryServer[dirId] = dirEnabled;
                return {
                    id: dirId,
                    enabled: dirEnabled
                }
            });
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
            if (!group.ascendants_id_ss) {
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
            var deferred = $q.defer();
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                controller: function ($scope, $modalInstance) {
                    $scope.inputValid = true;
                    $scope.group = {};
                    $scope.header = "admin.security.organizationalHierarchy.createGroupDialog.adHocGroup.title";
                    $scope.ok = function () {
                        $modalInstance.close($scope.group);
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                },
                size: 'sm'
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
                    newGroup.name = UUIDRegEx.test(newGroup.name) ?
                        newGroup.name.substring(0, newGroup.name.lastIndexOf("-UUID-")) : newGroup.name;

                    groupsMap[newGroup.object_id_s] = newGroup;

                    if (!groupsMap[newGroup.parent_id_s].child_id_ss) {
                        groupsMap[newGroup.parent_id_s].child_id_ss = [];
                    }
                    groupsMap[newGroup.parent_id_s].child_id_ss.push(newGroup.object_id_s);

                    newGroup.ascendants_id_ss = [newGroup.parent_id_s];
                    addToTree(newGroup, true);
                    deferred.resolve(newGroup);
                }, function () {
                    //error adding group
                    messageService.error("Group name already exists.");
                    deferred.reject();
                });
            }, function (result) {
                //button cancel, nothing to do.
            });
            return deferred.promise;
        };

        $scope.onDeleteGroup = function (group) {
            var deferred = $q.defer();
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.headerText') + group.name,
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.bodyText')
            };
            ModalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.removeGroup(group).then(function (payload) {
                    delete groupsMap[group.object_id_s];
                    var index = $scope.data.findIndex(function (el) {
                        return el.object_id_s === group.object_id_s;
                    });
                    $scope.data.splice(index, 1);
                    deferred.resolve(payload);
                }, function (payload) {
                    deferred.reject(payload);
                });
            }, function () {
                //cancel btn
            });
            return deferred.promise;
        };

        $scope.onDeleteMembers = function (group, members) {
            var deferred = $q.defer();
            members = [].concat(members);
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.headerText') + formatMembers(members),
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.bodyText')
            };

            ModalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.removeMembers(group, members).then(function (payload) {
                    deferred.resolve(payload);
                }, function (payload) {
                    deferred.reject(payload);
                });
            }, function () {
                //cancel btn
                deferred.reject("cancel");
            });
            return deferred.promise;
        };

        $scope.onAddMembers = function (group) {
            var deferred = $q.defer();
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
                        return "\"Object Type\": USER %26status_lcs:VALID";
                    }
                }
            });

            modalInstance.result.then(function (membersSelected) {
                //ok button clicked
                var selectedUserIds = [];
                if (Util.isArray((membersSelected))) {
                    selectedUserIds = _.map(membersSelected, function (member) {
                        return member.object_id_s;
                    })
                } else {
                    selectedUserIds = [membersSelected.object_id_s];
                }

                organizationalHierarchyService.saveMembers(group, selectedUserIds).then(function (payload) {
                    //saving success
                    var members = payload.data.members;
                    var selectedMembers = _.filter(members, function (member) {
                       return _.includes(selectedUserIds, member.userId);
                    });
                    var unmappedMembers = _.map(selectedMembers, function (member) {
                        return unMapMember(member);
                    });
                    deferred.resolve(unmappedMembers);
                }, function (payload) {
                    //saving error
                    deferred.reject();
                });
            }, function () {
                // Cancel button was clicked
            });
            return deferred.promise;
        };

        function openCreateUserModal(user, group, error) {
            return $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.cloneUser = false;
                    $scope.addUser = true;
                    $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                    $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                    $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                    $scope.error = error;
                    $scope.user = user;
                    $scope.user.groupNames = [group.object_id_s];
                    $scope.ok = function () {
                        $modalInstance.close($scope.user);
                    };
                    $scope.clearUsernameError = function(){
                        if($scope.error){
                            $scope.error = '';
                        }
                    };
                }],
                size: 'sm'
            });
        }

        function onLdapUserAdd(data, deferred, group) {
            organizationalHierarchyService.addMemberToLdapGroup(data, group.directory_name_s).then(function (member) {
                //saving success
                //map members
                var unmappedMember = unMapMember(member);
                deferred.resolve(unmappedMember);
            }, function (error) {
                // showing error
                if (error.data.extra) {
                    var onAdd = function (data) {
                        return onLdapUserAdd(data, deferred, group);
                    };
                    openCreateUserModal(error.data.extra.user, group, error.data.message)
                        .result.then(onAdd, function () {
                        deferred.reject("cancel");
                        return [];
                    });
                } else {
                    deferred.reject();
                }
            });
        }

        $scope.onAddLdapMember = function (group) {
            var deferred = $q.defer();
            var modalInstance = openCreateUserModal({}, group);
            var onAdd = function (data) {
                return onLdapUserAdd(data, deferred, group)
            };

            modalInstance.result.then(onAdd, function () {
                // Cancel button was clicked
                deferred.reject("cancel");
                return [];
            });
            return deferred.promise;
        };

        $scope.addExistingMembersToLdapGroup = function (group) {
            var deferred = $q.defer();
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
                        return "\"Object Type\": USER%26directory_name_s:" + group.directory_name_s + "%26status_lcs:VALID";
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

                organizationalHierarchyService
                    .addExistingMembersToLdapGroup(mappedMembers, group.object_id_s, group.directory_name_s)
                    .then(function (members) {
                        //saving success
                        //map members

                        var unmappedMembers = [];
                        for (var i = 0; i < members.length; i++) {
                            var unmappedMember = unMapMember(members[i]);
                            unmappedMembers.push(unmappedMember);
                        }
                        deferred.resolve(unmappedMembers);
                    }, function () {
                        //saving error
                        deferred.reject();
                    });
            }, function () {
                // Cancel button was clicked
                deferred.reject("cancel");
                return [];
            });
            return deferred.promise;
        };

        $scope.onEditLdapMember = function (member) {
            var deferred = $q.defer();
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.addUser = false;
                    $scope.cloneUser = false;
                    $scope.header = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.title";
                    $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.btn.ok";
                    $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.btn.cancel";
                    $scope.user = mapMember(member);
                    $scope.ok = function () {
                        $modalInstance.close($scope.user);
                    };
                }],
                size: 'sm'
            });

            modalInstance.result.then(function (user) {
                organizationalHierarchyService.editGroupMember(user)
                    .then(function (member) {
                        var unmappedMember = unMapMember(member);
                        deferred.resolve(unmappedMember);
                    }, function () {
                        //saving error
                        deferred.reject();
                    });
            }, function () {
                // Cancel button was clicked
                deferred.reject("cancel");
                return [];
            });
            return deferred.promise;
        };

        $scope.onDeleteLdapMember = function (data) {
            var deferred = $q.defer();
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.headerText'),
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.bodyText')
            };
            ModalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.deleteLdapUserMember(data).then(function (payload) {
                    deferred.resolve(payload);
                }, function (payload) {
                    deferred.reject(payload);
                });
            }, function () {
                //cancel btn
                deferred.reject("cancel");
            });
            return deferred.promise;
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
                    organizationalHierarchyService.getUsersForGroup(group.object_id_s.replace(/\./g, '_002E_'), 'VALID').then(function (payload) {
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
            mapped["userDirectoryName"] = member.directory_name_s;
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
            member.directory_name_s = mapped["userDirectoryName"];
            member.title = member.name;
            member.isMember = true;
            return member;
        }

        $scope.createGroup = function () {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                controller: function ($scope, $modalInstance) {
                    $scope.header = "admin.security.organizationalHierarchy.createGroupDialog.adHocGroup.title";
                    $scope.group = {};
                    $scope.addLdapGroupModal = false;
                    $scope.ok = function () {
                        $modalInstance.close($scope.group);
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                },
                size: 'sm'
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

                    newGroup.name = UUIDRegEx.test(payload.data.name) ?
                        payload.data.name.substring(0, payload.data.name.lastIndexOf("-UUID-")) : payload.data.name;

                    groupsMap[payload.data.object_id_s] = newGroup;
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

        $scope.addLdapSubgroup = function (parentGroup) {
            var deferred = $q.defer();
            var modalInstance = openLdapSubGroupModal({}, parentGroup);

            var onAdd = function (data) {
                onAddLdapSubGroup(deferred, data);
            };
            //handle the result
            modalInstance.result.then(onAdd, function () {
                //button cancel, nothing to do.
                deferred.reject("cancel");
                return [];
            });
            return deferred.promise;
        };

        function onAddLdapSubGroup(deferred, data) {
            //button ok
            organizationalHierarchyService.createLdapSubgroup(data.subgroup, data.parentGroupName, data.parentGroupDirectoryName)
                .then(function (group) {
                    //added successfully
                    var newGroup = {};
                    newGroup.object_sub_type_s = group.type;
                    newGroup.object_id_s = group.name;
                    newGroup.name = group.name;
                    newGroup.parent_id_s = data.parentGroupName;
                    newGroup.directory_name_s = data.parentGroupDirectoryName;
                    groupsMap[group.name] = newGroup;
                    if (!groupsMap[newGroup.parent_id_s].child_id_ss) {
                        groupsMap[newGroup.parent_id_s].child_id_ss = [];
                    }
                    groupsMap[newGroup.parent_id_s].child_id_ss.push(newGroup.object_id_s);
                    newGroup.ascendants_id_ss = [newGroup.parent_id_s];

                    addToTree(newGroup);
                    deferred.resolve(newGroup);
                    messageService.succsessAction();
                }, function (error) {
                    //error adding group
                    if (error.data.extra) {
                        var onAdd = function (data) {
                            onAddLdapSubGroup(deferred, data);
                        };
                        var parentGroup = {
                            "object_id_s": data.parentGroupName,
                            "directory_name_s": data.parentGroupDirectoryName
                        };
                        openLdapSubGroupModal(error.data.extra.subgroup, parentGroup, error.data.message)
                            .result.then(onAdd, function () {
                            //cancel button clicked
                            deferred.reject('cancel');
                        });
                    }
                    messageService.errorAction();
                });
        }

        $scope.onDeleteLdapGroup = function (group) {
            var deferred = $q.defer();
            var modalOptions = {
                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.cancelBtn'),
                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.deleteBtn'),
                headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.headerText') + group.name,
                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.bodyText')
            };
            ModalDialogService.showModal({}, modalOptions).then(function () {
                //ok btn
                organizationalHierarchyService.deleteLdapGroup(group).then(function (payload) {
                    delete groupsMap[group.object_id_s];
                    var index = $scope.data.findIndex(function (el) {
                        return el.object_id_s === group.object_id_s;
                    });
                    $scope.data.splice(index, 1);
                    deferred.resolve(payload);
                }, function (payload) {
                    deferred.reject(payload);
                });
            }, function () {
                //cancel btn
                deferred.reject("cancel");
            });
            return deferred.promise;
        };

        var groupController = function (seeDirectorySelect, group, errorMessage, parentGroup, onOK, directoryServers) {
            return function ($scope, $modalInstance) {
                $scope.header = "admin.security.organizationalHierarchy.createGroupDialog.ldapGroup.title";
                $scope.addLdapGroupModal = seeDirectorySelect;
                $scope.group = group;
                $scope.error = errorMessage;
                $scope.directoryServers = directoryServers;
                $scope.selectedConfig = {};
                $scope.ok = onOK($scope, $modalInstance, $scope.selectedConfig);
                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        };

        function groupModal(controllerFunction) {
            return $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                controller: ['$scope', '$modalInstance', controllerFunction],
                size: 'sm'
            });
        }

        function openLdapSubGroupModal(group, parentGroup, errorMessage) {
            return groupModal(groupController(false, group, errorMessage, parentGroup, function (scope, modal) {
                return function () {
                    scope.data = {
                        "subgroup": scope.group,
                        "parentGroupName": parentGroup.object_id_s,
                        "parentGroupDirectoryName": parentGroup.directory_name_s
                    };
                    modal.close(scope.data);
                };
            }))
        }

        function openCreateGroupModal(group, errorMessage) {
            return groupModal(groupController(true, group, errorMessage, {}, function (scope, modal) {
                return function () {
                    scope.data = {
                        "group": scope.group,
                        "selectedDirectory": scope.selectedConfig.directory
                    };
                    modal.close(scope.data);
                };
            }, $scope.directoryServers))
        }

        function onLdapGroupAdd(data, deferred) {
            //button ok
            organizationalHierarchyService.createLdapGroup(data.group, data.selectedDirectory.id)
                .then(function (group) {
                    //added successfully
                    var newGroup = {};
                    newGroup.object_sub_type_s = group.type;
                    newGroup.object_id_s = group.name;
                    newGroup.name = group.name;
                    newGroup.directory_name_s = group.directoryName;
                    groupsMap[group.name] = newGroup;
                    addToTree(newGroup, true);
                    messageService.succsessAction();
                }, function (error) {
                    if (error.data.extra) {
                        var onAdd = function (data) {
                            onLdapGroupAdd(data, deferred);
                        };
                        openCreateGroupModal(error.data.extra.group, error.data.message)
                            .result.then(onAdd, function () {
                            deferred.reject("cancel");
                            return [];
                        });
                    }
                    messageService.errorAction();
                });
        }

        $scope.createLdapGroup = function () {
            var deferred = $q.defer();
            var modalInstance = openCreateGroupModal({});

            var onAdd = function (data) {
                onLdapGroupAdd(data, deferred);
            };

            modalInstance.result.then(onAdd, function () {
                // Cancel button was clicked
                deferred.reject("cancel");
                return [];
            });
            return deferred.promise;
        };

        function formatMembers(members) {
            var formattedMembers = [];
            for (var i = 0; i < members.length; i++) {
                formattedMembers.push(members[i].name);
            }
            return formattedMembers.join(', ');
        }

        $scope.onSetSupervisor = function (group) {
            var deferred = $q.defer();
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
                        return "\"Object Type\": USER %26status_lcs:VALID";
                    }
                }
            });

            modalInstance.result.then(function (memberSelected) {
                //ok button clicked
                organizationalHierarchyService.setSupervisor(group, mapMember(memberSelected)).then(function (payload) {
                    //saving success
                    deferred.resolve(payload.data);
                }, function (payload) {
                    //saving error
                    deferred.reject();
                });
            }, function () {
                // Cancel button was clicked
            });
            return deferred.promise;
        };
    }
]);