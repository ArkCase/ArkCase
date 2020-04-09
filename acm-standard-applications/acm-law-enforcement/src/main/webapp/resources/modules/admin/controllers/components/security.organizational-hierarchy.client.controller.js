'use strict';

angular.module('admin').controller(
        'Admin.OrganizationalHierarchyController',
        [ '$scope', '$timeout', 'Admin.OrganizationalHierarchyService', '$q', '$modal', 'MessageService', '$translate', 'Admin.ModalDialogService', 'UtilService', 'Admin.LdapConfigService',
                function($scope, $timeout, organizationalHierarchyService, $q, $modal, messageService, $translate, ModalDialogService, Util, LdapConfigService) {

                    $scope.data = [];
                    var groupsMap = {};
                    var gridCurrentPage;
                    var gridPageSize;
                    var controlGroups = [];

                    var enableEditingLdapUsers = false;
                    function getEnableEditingLdapUsers() {
                        var tempLdapPromise = LdapConfigService.retrieveDirectories();
                        tempLdapPromise.then(function(directories) {
                            removePrefixInKey(directories.data);
                            _.forEach(directories.data , function (data) {
                                enableEditingLdapUsers = data.enableEditingLdapUsers;
                            });
                            $scope.showButton = enableEditingLdapUsers;
                        });
                    }

                    //we need this because key name contains '.'
                    function removePrefixInKey(data) {
                        angular.forEach(data, function(row, index) {
                            angular.forEach(row, function(element, key) {
                                if (key.match('.') !== -1) {
                                    delete row[key];
                                    var newKey = key.replace(/[a-zA-Z]*?\./, '');
                                    row[newKey] = element;
                                }
                            });
                        });
                    }
                    getEnableEditingLdapUsers();
                    LdapConfigService.retrieveDirectories().then(function(directories) {
                        $scope.ldapEditingEnabledPerDirectoryServer = {};
                        $scope.directoryServers = _.map(directories.data, function(ds) {
                            var dirId = ds["ldapConfig.id"];
                            var dirEnabled = ds["ldapConfig.enableEditingLdapUsers"] === true;
                            var groupControlGroup = ds["ldapConfig.groupControlGroup"];
                            var userControlGroup = ds["ldapConfig.userControlGroup"];
                            var userIdAttributeName = ds["ldapConfig.userIdAttributeName"];

                            if (groupControlGroup.trim() !== '') {
                                controlGroups.push(groupControlGroup);
                            }
                            if (userControlGroup.trim() !== '') {
                                controlGroups.push(userControlGroup);
                            }

                            $scope.ldapEditingEnabledPerDirectoryServer[dirId] = dirEnabled;
                            return {
                                id: dirId,
                                enabled: dirEnabled,
                                checkUsernameLength: userIdAttributeName === 'sAMAccountName'
                            }
                        });
                    });

                    $scope.config.$promise.then(function(config) {
                        $scope.cfg = _.find(config.components, {
                            id: 'usersPicker'
                        });
                        $scope.cfgOrgHierarchy = _.find(config.components, {
                            id: 'orgHierarchy'
                        });
                    });

                    function createTreeData(groups) {
                        _.forEach(groups, function(group) {
                            addToTree(group);
                        });
                    }

                    function refreshPageData() {
                        // set delay for solr to finish indexing
                        $timeout(function() {
                            $scope.onLoadMore(gridCurrentPage, gridPageSize);
                        }, 2000);
                    }

                    function addToGroupsMap(groups) {
                        _.forEach(groups, function(group) {
                            groupsMap[group.object_id_s] = group;
                        });
                    }

                    function addToTree(group, top) {
                        group.lazy = true;
                        group.folder = true;
                        group.title = group.name;
                        var hasAscendantControlGroup = false;
                        if(controlGroups.length > 0 && group.ascendants_id_ss && group.ascendants_id_ss.length === 1){
                                if(controlGroups.includes(group.ascendants_id_ss[0])){
                                    hasAscendantControlGroup = true;
                                }
                        }
                        if (group.supervisor_id_s) {
                            if (group.supervisor_name_s)
                                group.supervisor = group.supervisor_name_s;
                            else
                                group.supervisor = group.supervisor_id_s;
                        }
                        //init children array
                        if (!group.children)
                            group.children = [];
                        if (!group.ascendants_id_ss || group.ascendants_id_ss.length === 0 ||
                            hasAscendantControlGroup) {
                            //add group to root
                            if (top)
                                $scope.data.unshift(group);
                            else
                                $scope.data.push(group);
                        }
                    }

                    $scope.onLoadMore = function(currentPage, pageSize) {
                        gridPageSize = pageSize;
                        gridCurrentPage = currentPage;

                        var successCallback = function(payload) {
                            var groups = _.get(payload, 'data.response.docs');
                            $scope.data = [];
                            groupsMap = {};
                            $scope.totalGroups = _.get(payload, 'data.response.numFound');

                            groups.forEach(function (group) {
                                group.directory = _.find($scope.directoryServers, function (directoryServer) {
                                    return directoryServer.id === group.directory_name_s
                                });
                            });

                            addToGroupsMap(groups);
                            createTreeData(groups);
                        };
                        if (!_.isEmpty($scope.searchParams)) {
                            $scope.searchParams.n = pageSize;
                            $scope.searchParams.start = (currentPage - 1) * pageSize;
                            organizationalHierarchyService.getGroupsByName($scope.searchParams).then(successCallback);
                        } else {
                            organizationalHierarchyService.getGroupsTopLevel(currentPage, pageSize, []).then(successCallback);
                        }
                    };

                    $scope.searchParams = {};
                    $scope.onSearch = function() {
                        $scope.onLoadMore(gridCurrentPage, gridPageSize);
                    };

                    $scope.onFilterChange = function(searchFilter) {
                        if (searchFilter === '') {
                            $scope.searchParams = {};
                            $scope.onLoadMore(gridCurrentPage, gridPageSize);
                        }
                    };

                    var adHocGroupController = function(group, errorMessage, parentGroup, onOK) {
                        return function($scope, $modalInstance) {
                            $scope.header = "admin.security.organizationalHierarchy.createGroupDialog.adHocGroup.title";
                            $scope.group = group;
                            $scope.error = errorMessage;
                            $scope.ok = onOK($scope, $modalInstance);
                            $scope.cancel = function() {
                                $modalInstance.dismiss('cancel');
                            };
                        };
                    };

                    function openAdHocGroupModal(group, parentGroup, errorMessage) {
                        return groupModal(adHocGroupController(group, errorMessage, parentGroup, function(scope, modal) {
                            return function() {
                                var group = scope.group;
                                group.type = "ADHOC_GROUP";
                                scope.data = {
                                    "group": group,
                                    "parent": parentGroup
                                };
                                modal.close(scope.data);
                            };
                        }))
                    }

                    $scope.onAddSubGroup = function(parent) {
                        var deferred = $q.defer();
                        var modalInstance = openAdHocGroupModal({}, parent);
                        //handle the result

                        var onAdd = function(data) {
                            onAddAdHocSubGroup(deferred, data.group, data.parent);
                        };
                        //handle the result
                        modalInstance.result.then(onAdd, function() {
                            //button cancel, nothing to do.
                        });
                        return deferred.promise;
                    };

                    function onAddAdHocSubGroup(deferred, adHocGroup, parent) {
                        organizationalHierarchyService.addAdHocGroup(adHocGroup, parent).then(function(payload) {
                            //added successfully
                            var newGroup = unmapGroupMember(payload.data);
                            groupsMap[newGroup.object_id_s] = newGroup;
                            addToTree(newGroup);
                            deferred.resolve(newGroup);
                            messageService.succsessAction();
                        }, function(error) {
                            //error adding group
                            if (error.data.extra && error.data.field === 'name') {
                                var onAdd = function(data) {
                                    onAddAdHocSubGroup(deferred, data.group, data.parent);
                                };
                                openAdHocGroupModal(error.data.extra.subGroup, parent, error.data.message).result.then(onAdd, function() {
                                    //cancel button clicked
                                    deferred.reject('cancel');
                                });
                            } else {
                                messageService.error(error.data.message);
                            }
                        });
                    }

                    $scope.onAddExistingSubGroup = function(parent) {
                        var deferred = $q.defer();
                        var excludeAncestorGroups = '';
                        if (parent.ascendants_id_ss && parent.ascendants_id_ss.length > 0) {
                            var ancestors = _.map(parent.ascendants_id_ss, function(ancestor) {
                                return ancestor.replace(/ /g, "*")
                            });
                            excludeAncestorGroups = ' OR ' + ancestors.join(' OR ');
                        }
                        var parentId = parent.object_id_s.replace(/ /g, "*");
                        var params = {
                            filter: '\"Object Type\":GROUP %26 object_sub_type_s:ADHOC_GROUP %26 status_lcs:ACTIVE %26 -object_id_s:(' + parentId + excludeAncestorGroups + ")",
                            header: $translate.instant('admin.security.organizationalHierarchy.addMembers.group.title')
                        };
                        var modalInstance = openMembersPicker(params);
                        modalInstance.result.then(function(groups) {
                            var memberIds = _.map(groups, function(group) {
                                return group.object_id_s;
                            });
                            organizationalHierarchyService.addExistingAdHocSubGroups(parent.object_id_s, memberIds).then(function(payload) {
                                //added successfully
                                var subgroups = _.map(payload.data, function(group) {
                                    return unmapGroupMember(group, parent.object_id_s);
                                });
                                createTreeData(subgroups);
                                deferred.resolve(subgroups);
                            }, function() {
                                //error adding group
                                deferred.reject();
                            }).then(function() {
                                refreshPageData();
                            });
                        }, function() {
                            //button cancel, nothing to do.
                        });
                        return deferred.promise;
                    };

                    $scope.onDeleteGroup = function(groupNode, parentNode) {
                        var deferred = $q.defer();
                        var modalOptions = {
                            closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.cancelBtn'),
                            actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.deleteBtn'),
                            headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.headerText') + groupNode.name,
                            bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.bodyText')
                        };
                        ModalDialogService.showModal({}, modalOptions).then(function() {
                            //ok btn
                            if (!_.isEmpty(parentNode)) {
                                organizationalHierarchyService.removeGroupMembership(groupNode.object_id_s, parentNode.object_id_s).then(function(group) {
                                    refreshPageData();
                                    deferred.resolve(group);
                                }, function(errorData) {
                                    deferred.reject(errorData);
                                });
                            } else {
                                organizationalHierarchyService.removeGroup(groupNode).then(function(payload) {
                                    refreshPageData();
                                    deferred.resolve(payload);
                                }, function(payload) {
                                    deferred.reject(payload);
                                });
                            }
                        }, function() {
                            //cancel btn
                        });
                        return deferred.promise;
                    };

                    $scope.onDeleteMembers = function(group, members) {
                        var deferred = $q.defer();
                        members = [].concat(members);
                        var modalOptions = {
                            closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.cancelBtn'),
                            actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.deleteBtn'),
                            headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.headerText') + formatMembers(members),
                            bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.bodyText')
                        };

                        ModalDialogService.showModal({}, modalOptions).then(function() {
                            //ok btn
                            organizationalHierarchyService.removeMembers(group, members).then(function(payload) {
                                deferred.resolve(payload);
                            }, function(payload) {
                                deferred.reject(payload);
                            });
                        }, function() {
                            //cancel btn
                            deferred.reject("cancel");
                        });
                        return deferred.promise;
                    };

                    $scope.onAddMembers = function(group) {
                        var deferred = $q.defer();
                        var params = {
                            filter: "\"Object Type\": USER %26status_lcs:VALID%26-groups_id_ss:" + group.object_id_s
                        };
                        var modalInstance = openMembersPicker(params);

                        modalInstance.result.then(function(membersSelected) {
                            //ok button clicked
                            var selectedUserIds = [];
                            if (Util.isArray((membersSelected))) {
                                selectedUserIds = _.map(membersSelected, function(member) {
                                    return member.object_id_s;
                                })
                            } else {
                                selectedUserIds = [ membersSelected.object_id_s ];
                            }

                            organizationalHierarchyService.saveMembers(group, selectedUserIds).then(function(payload) {
                                //saving success
                                var members = payload.data.members;
                                var selectedMembers = _.filter(members, function(member) {
                                    return _.includes(selectedUserIds, member.userId);
                                });
                                var unmappedMembers = _.map(selectedMembers, function(member) {
                                    return unMapMember(member);
                                });
                                deferred.resolve(unmappedMembers);
                            }, function(payload) {
                                //saving error
                                deferred.reject();
                            });
                        }, function() {
                            // Cancel button was clicked
                        });
                        return deferred.promise;
                    };

                    function openCreateUserModal(user, group, error) {
                        return $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                            controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                                $scope.enableUsernameEdit = true;
                                $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                                $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                                $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                                $scope.error = error;
                                $scope.user = user;
                                $scope.user.groupNames = [group.object_id_s];

                                $scope.ok = function () {
                                    $modalInstance.close($scope.user);
                                };
                                $scope.clearUsernameError = function () {
                                    if (group.directory.checkUsernameLength && $scope.user.userId.length > 20) {
                                        $scope.error = $translate.instant('admin.security.organizationalHierarchy.createUserDialog.addLdapMember.samlMaxUsernameLengthError');
                                    } else if ($scope.error) {
                                        $scope.error = '';
                                    }
                                };
                            } ],
                            size: 'md',
                            backdrop: 'static'
                        });
                    }

                    function onLdapUserAdd(data, deferred, group) {
                        organizationalHierarchyService.addMemberToLdapGroup(data, group.directory_name_s).then(function(member) {
                            //saving success
                            //map members
                            var unmappedMember = unMapMember(member);
                            deferred.resolve(unmappedMember);
                        }, function(error) {
                            // showing error
                            if (error.data.extra) {
                                var onAdd = function(data) {
                                    return onLdapUserAdd(data, deferred, group);
                                };
                                openCreateUserModal(error.data.extra.user, group, error.data.message).result.then(onAdd, function() {
                                    deferred.reject("cancel");
                                    return [];
                                });
                            } else {
                                deferred.reject();
                            }
                        });
                    }

                    $scope.onAddLdapMember = function(group) {
                        var deferred = $q.defer();
                        var modalInstance = openCreateUserModal({}, group);
                        var onAdd = function(data) {
                            return onLdapUserAdd(data, deferred, group)
                        };

                        modalInstance.result.then(onAdd, function() {
                            // Cancel button was clicked
                            deferred.reject("cancel");
                            return [];
                        });
                        return deferred.promise;
                    };

                    function openMembersPicker(params, showSelectedItemsGrid) {
                        params.config = $scope.cfg;
                        params.showSelectedItemsGrid = Util.isEmpty(showSelectedItemsGrid);
                        return $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/admin/views/components/security.org-hierarchy.users-groups-picker.client.view.html',
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.filter = params.filter;
                                $scope.showSelectedItemsGrid = params.showSelectedItemsGrid;
                                $scope.config = params.config;
                                $scope.header = params.header ? params.header : $translate.instant('admin.security.organizationalHierarchy.addMembers.title');
                            } ],
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: params
                            }
                        });
                    }

                    $scope.addExistingMembersToLdapGroup = function(group) {
                        var deferred = $q.defer();
                        var params = {
                            filter: "\"Object Type\": USER%26directory_name_s:" + group.directory_name_s + "%26status_lcs:VALID%26-groups_id_ss:" + group.object_id_s
                        };
                        var modalInstance = openMembersPicker(params);

                        modalInstance.result.then(function(selectedMembers) {
                            //ok button clicked
                            var memberIds = [];
                            if (Util.isArray(selectedMembers)) {
                                memberIds = _.map(selectedMembers, function(member) {
                                    return member.object_id_s;
                                });
                            } else {
                                memberIds.push(selectedMembers.object_id_s);
                            }

                            organizationalHierarchyService.addExistingMembersToLdapGroup(memberIds, group.object_id_s, group.directory_name_s).then(function(members) {
                                //saving success
                                //map members

                                var unmappedMembers = [];
                                for (var i = 0; i < members.length; i++) {
                                    var unmappedMember = unMapMember(members[i]);
                                    unmappedMembers.push(unmappedMember);
                                }
                                deferred.resolve(unmappedMembers);
                            }, function() {
                                //saving error
                                deferred.reject();
                            });
                        }, function() {
                            // Cancel button was clicked
                            deferred.reject("cancel");
                            return [];
                        });
                        return deferred.promise;
                    };

                    $scope.onEditLdapMember = function(member) {
                        var deferred = $q.defer();
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                            controller: [ '$scope', '$modalInstance', function($scope, $modalInstance) {
                                $scope.enableUsernameEdit = false;
                                $scope.cloneUser = false;
                                $scope.header = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.title";
                                $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.btn.ok";
                                $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.editLdapMember.btn.cancel";
                                $scope.user = mapMember(member);
                                $scope.ok = function() {
                                    $modalInstance.close($scope.user);
                                };
                            } ],
                            size: 'md',
                            backdrop: 'static'
                        });

                        modalInstance.result.then(function(user) {
                            organizationalHierarchyService.editGroupMember(user).then(function(member) {
                                var unmappedMember = unMapMember(member);
                                deferred.resolve(unmappedMember);
                            }, function() {
                                //saving error
                                deferred.reject();
                            });
                        }, function() {
                            // Cancel button was clicked
                            deferred.reject("cancel");
                            return [];
                        });
                        return deferred.promise;
                    };

                    $scope.onDeleteLdapMember = function(data) {
                        var deferred = $q.defer();
                        var modalOptions = {
                            closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.cancelBtn'),
                            actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.deleteBtn'),
                            headerText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.headerText'),
                            bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.member.confirm.delete.bodyText')
                        };
                        ModalDialogService.showModal({}, modalOptions).then(function() {
                            //ok btn
                            organizationalHierarchyService.deleteLdapUserMember(data).then(function(payload) {
                                deferred.resolve(payload);
                            }, function(payload) {
                                deferred.reject(payload);
                            });
                        }, function() {
                            //cancel btn
                            deferred.reject("cancel");
                        });
                        return deferred.promise;
                    };

                    $scope.onLazyLoad = function(event, groupNode) {

                        var groupId = groupNode.object_id_s;

                        var children = [];

                        var dfd = $q.defer();

                        var memberGroupsPromise;
                        var memberUsersPromise;

                        //find child groups
                        memberGroupsPromise = organizationalHierarchyService.getSubGroupsForGroup(groupId);

                        //find child users
                        memberUsersPromise = organizationalHierarchyService.getUsersForGroup(groupId, 'VALID');

                        $q.all([ memberGroupsPromise, memberUsersPromise ]).then(function(data) {
                            var memberGroups = _.get(data[0], 'data.response.docs');
                            addToGroupsMap(memberGroups);
                            createTreeData(memberGroups);

                            var group = groupsMap[groupId];

                            children = children.concat(memberGroups);

                            var memberUsers = _.get(data[1], 'data.response.docs');
                            for (var i = 0; i < memberUsers.length; i++) {
                                memberUsers[i].title = memberUsers[i].name;
                                memberUsers[i].isMember = true;
                                children.push(memberUsers[i]);
                            }
                            group.children = children;
                            dfd.resolve(children);
                        });
                        return dfd.promise;
                    };

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

                    $scope.createGroup = function() {
                        var deferred = $q.defer();
                        var modalInstance = openAdHocGroupModal({});

                        var onAdd = function(data) {
                            onAddAdHocGroup(deferred, data.group);
                        };
                        //handle the result
                        modalInstance.result.then(onAdd, function() {
                            //button cancel, nothing to do.
                        });
                        return deferred.promise;
                    };

                    function onAddAdHocGroup(deferred, adHocGroup) {
                        organizationalHierarchyService.addAdHocGroup(adHocGroup).then(function(payload) {
                            //added successfully
                            var newGroup = unmapGroupMember(payload.data);
                            groupsMap[newGroup.object_id_s] = newGroup;
                            addToTree(newGroup, true);
                            deferred.resolve(newGroup);
                            messageService.succsessAction();
                        }, function(error) {
                            //error adding group
                            if (error.data.extra && error.data.field === 'name') {
                                var onAdd = function(data) {
                                    onAddAdHocGroup(deferred, data.group);
                                };
                                openAdHocGroupModal(error.data.extra.group, null, error.data.message).result.then(onAdd, function() {
                                    //cancel button clicked
                                    deferred.reject('cancel');
                                });
                            } else {
                                messageService.error(error.data.message);
                            }
                            deferred.reject();
                        });
                    }

                    $scope.addLdapSubgroup = function(parentGroup) {
                        var deferred = $q.defer();
                        var modalInstance = openLdapSubGroupModal({}, parentGroup);

                        var onAdd = function(data) {
                            onAddLdapSubGroup(deferred, data);
                        };
                        //handle the result
                        modalInstance.result.then(onAdd, function() {
                            //button cancel, nothing to do.
                            deferred.reject("cancel");
                            return [];
                        });
                        return deferred.promise;
                    };

                    function onAddLdapSubGroup(deferred, data) {
                        //button ok
                        organizationalHierarchyService.createLdapSubgroup(data.subgroup, data.parentGroupName, data.parentGroupDirectoryName).then(function(group) {
                            //added successfully
                            var newGroup = unmapGroupMember(group, data.parentGroupName, data.parentGroupDirectoryName);
                            groupsMap[group.name] = newGroup;
                            addToTree(newGroup);
                            deferred.resolve(newGroup);
                            messageService.succsessAction();
                        }, function(error) {
                            //error adding group
                            if (error.data.extra && error.data.field === 'name') {
                                var onAdd = function(data) {
                                    onAddLdapSubGroup(deferred, data);
                                };
                                var parentGroup = {
                                    "object_id_s": data.parentGroupName,
                                    "directory_name_s": data.parentGroupDirectoryName
                                };
                                openLdapSubGroupModal(error.data.extra.subgroup, parentGroup, error.data.message).result.then(onAdd, function() {
                                    //cancel button clicked
                                    deferred.reject('cancel');
                                });
                            } else {
                                messageService.error(error.data.message);
                            }
                        });
                    }

                    $scope.onDeleteLdapGroup = function(group, parent) {

                        var deferred = $q.defer();
                        var controlGroup;

                        organizationalHierarchyService.getControlGroup(group.directory_name_s).then(function (response) {
                            controlGroup = response.data;
                            var headerText = $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.headerText') + group.name;
                            if (parent.name == controlGroup) {
                                headerText = $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.controlGroupHeaderWarning', {
                                    groupName: group.name,
                                    controlGroupName: controlGroup
                                });
                            }
                            var modalOptions = {
                                closeButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.cancelBtn'),
                                actionButtonText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.deleteBtn'),
                                headerText: headerText,
                                bodyText: $translate.instant('admin.security.organizationalHierarchy.dialog.group.confirm.delete.bodyText')
                            };
                            ModalDialogService.showModal({}, modalOptions).then(function () {
                                //ok btn
                                if (!_.isEmpty(parent)) {
                                    organizationalHierarchyService.removeLdapGroupMembership(group, parent).then(function (payload) {
                                        refreshPageData();
                                        deferred.resolve(payload);
                                    }, function (payload) {
                                        deferred.reject(payload);
                                    });
                                } else {
                                    organizationalHierarchyService.deleteLdapGroup(group).then(function (payload) {
                                        refreshPageData();
                                        deferred.resolve(payload);
                                    }, function (payload) {
                                        deferred.reject(payload);
                                    });
                                }
                            }, function () {
                                //cancel btn
                                deferred.reject("cancel");
                            });
                        });

                        return deferred.promise;
                    };

                    var groupController = function(seeDirectorySelect, group, errorMessage, parentGroup, onOK, directoryServers) {
                        return function($scope, $modalInstance) {
                            $scope.header = "admin.security.organizationalHierarchy.createGroupDialog.ldapGroup.title";
                            $scope.addLdapGroupModal = seeDirectorySelect;
                            $scope.maxLdapGroupNameLength = 64;
                            $scope.group = group;
                            $scope.error = errorMessage;
                            $scope.directoryServers = directoryServers;
                            $scope.selectedConfig = {};
                            $scope.ok = onOK($scope, $modalInstance, $scope.selectedConfig);
                            $scope.cancel = function() {
                                $modalInstance.dismiss('cancel');
                            };
                        };
                    };

                    function groupModal(controllerFunction) {
                        return $modal.open({
                            animation: true,
                            templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-group.dialog.html',
                            controller: [ '$scope', '$modalInstance', controllerFunction ],
                            size: 'sm',
                            backdrop: 'static'
                        });
                    }

                    function openLdapSubGroupModal(group, parentGroup, errorMessage) {
                        return groupModal(groupController(false, group, errorMessage, parentGroup, function(scope, modal) {
                            return function() {
                                var group = scope.group;
                                group.type = "LDAP_GROUP";
                                scope.data = {
                                    "subgroup": group,
                                    "parentGroupName": parentGroup.object_id_s,
                                    "parentGroupDirectoryName": parentGroup.directory_name_s
                                };
                                modal.close(scope.data);
                            };
                        }))
                    }

                    function openCreateGroupModal(group, errorMessage) {
                        return groupModal(groupController(true, group, errorMessage, {}, function(scope, modal) {
                            return function() {
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
                        var group = data.group;
                        group.type = "LDAP_GROUP";
                        organizationalHierarchyService.createLdapGroup(group, data.selectedDirectory.id).then(function(group) {
                            //added successfully
                            var newGroup = {};
                            newGroup.object_sub_type_s = group.type;
                            newGroup.object_id_s = group.name;
                            newGroup.name = group.name;
                            newGroup.directory_name_s = group.directoryName;
                            groupsMap[group.name] = newGroup;
                            addToTree(newGroup, true);
                            messageService.succsessAction();
                        }, function(error) {
                            if (error.data.extra && error.data.field === 'name') {
                                var onAdd = function(data) {
                                    onLdapGroupAdd(data, deferred);
                                };
                                openCreateGroupModal(error.data.extra.group, error.data.message).result.then(onAdd, function() {
                                    deferred.reject("cancel");
                                    return [];
                                });
                            } else {
                                messageService.error(error.data.message);
                            }
                        });
                    }

                    $scope.createLdapGroup = function() {
                        var deferred = $q.defer();
                        var modalInstance = openCreateGroupModal({});

                        var onAdd = function(data) {
                            onLdapGroupAdd(data, deferred);
                        };

                        modalInstance.result.then(onAdd, function() {
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

                    $scope.onSetSupervisor = function(group) {
                        var deferred = $q.defer();
                        var params = {
                            filter: "\"Object Type\": USER %26status_lcs:VALID"
                        };
                        var modalInstance = openMembersPicker(params, true);

                        modalInstance.result.then(function(memberSelected) {
                            //ok button clicked
                            organizationalHierarchyService.setSupervisor(group, mapMember(memberSelected)).then(function(payload) {
                                //saving success
                                deferred.resolve(payload.data);
                            }, function(payload) {
                                //saving error
                                deferred.reject();
                            });
                        }, function() {
                            // Cancel button was clicked
                        });
                        return deferred.promise;
                    };

                    function unmapGroupMember(mapped, parentId, directory) {
                        var member = {};
                        member.object_id_s = mapped.name;
                        member.name = mapped.name;
                        member.create_date_tdt = mapped.created;
                        member.modified_date_tdt = mapped.modified;
                        member.status_lcs = mapped.status;
                        member.title = mapped.name;
                        member.object_sub_type_s = mapped.type;
                        member.ascendants_id_ss = mapped.ascendants;
                        if (parentId) {
                            member.parent_id_s = parentId;
                            if (member.supervisor) {
                                member.supervisor = member.supervisor.fullName;
                            }
                        }
                        if (directory) {
                            member.directory_name_s = directory;
                        }
                        return member;
                    }
                } ]);