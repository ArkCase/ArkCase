'use strict';

angular.module('admin').controller(
        'Admin.LdapUserManagementController',
        [
                '$scope',
                '$q',
                '$modal',
                '$timeout',
                'Admin.LdapUserManagementService',
                'LookupService',
                'MessageService',
                'Acm.StoreService',
                'UtilService',
                '$log',
                function($scope, $q, $modal, $timeout, LdapUserManagementService, LookupService, MessageService, Store, Util, $log) {

                    $scope.cloneUser = cloneUser;
                    $scope.onObjSelect = onObjSelect;
                    $scope.onAuthRoleSelected = onAuthRoleSelected;
                    $scope.initUser = initUser;
                    $scope.fillList = fillList;
                    $scope.deleteUser = deleteUser;

                    var makePaginationRequest = true;
                    var currentAuthGroups;
                    $scope.showFilter = true;
                    $scope.appUsers = [];
                    $scope.appGroups = [];
                    $scope.lastSelectedUser = "";
                    $scope.userData = {
                        "appUsers" : $scope.appUsers,
                        "selectedNotAuthorized" : [],
                        "selectedAuthorized" : []
                    };

                    function initUser(userNumber) {
                        var userRequestInfo = {};
                        userRequestInfo.n = Util.isEmpty(userNumber) ? 50 : userNumber;
                        if (makePaginationRequest) {
                            LdapUserManagementService.getNUsers(userRequestInfo).then(function(response) {
                                $scope.userData.appUsers = [];
                                $scope.fillList($scope.userData.appUsers, response.data.response.docs);
                                makePaginationRequest = response.data.response.numFound > userRequestInfo.n;
                            });
                        }
                    }

                    $scope.initUser();

                    //callback function when user is selected
                    function onObjSelect(selectedObject, authorized, notAuthorized) {
                        var data = {};
                        data.member_id = selectedObject;
                        $scope.lastSelectedUser = selectedObject;
                        currentAuthGroups = [];

                        data.isAuthorized = false;
                        var unAuthorizedGroupsForUser = LdapUserManagementService.getGroupsForUser(data);
                        data.isAuthorized = true;
                        var authorizedGroupsForUser = LdapUserManagementService.getGroupsForUser(data);

                        $q.all([ authorizedGroupsForUser, unAuthorizedGroupsForUser ]).then(function(result) {
                            var groups = _.union(result[0].data.response.docs, result[1].data.response.docs);
                            _.forEach(groups, function(group) {
                                _.forEach(group.member_id_ss, function(groupMember) {
                                    if (groupMember === selectedObject.key) {
                                        var authObject = {};
                                        authObject.key = group.name;
                                        authObject.name = group.name;
                                        authorized.push(authObject);
                                        currentAuthGroups.push(authObject.key);
                                    }
                                });
                                if (currentAuthGroups.indexOf(group.name) === -1) {
                                    //we need to create wrapper to provide a name property
                                    var notAuthorizedRole = {};
                                    notAuthorizedRole.key = group.name;
                                    notAuthorizedRole.name = group.name;
                                    notAuthorized.push(notAuthorizedRole);
                                }
                            });
                        });
                    }

                    //callback function when groups are moved
                    function onAuthRoleSelected(selectedObject, authorized, notAuthorized) {
                        var toBeAdded = [];
                        var toBeRemoved = [];
                        var deferred = $q.defer();

                        //get roles which needs to be added
                        _.forEach(authorized, function(group) {
                            if (currentAuthGroups.indexOf(group.key) === -1) {
                                toBeAdded.push(group.key);
                            }
                        });
                        _.forEach(notAuthorized, function(group) {
                            if (currentAuthGroups.indexOf(group.key) !== -1) {
                                toBeRemoved.push(group.key);
                            }
                        });
                        //perform adding on server
                        if (toBeAdded.length > 0) {
                            currentAuthGroups = currentAuthGroups.concat(toBeAdded);

                            LdapUserManagementService.addGroupsToUser(selectedObject.key, toBeAdded, selectedObject.directory).then(
                                    function(data) {
                                        MessageService.succsessAction();
                                    }, function() {
                                        //error adding group
                                        MessageService.errorAction();
                                    });
                            return deferred.promise;
                        }

                        if (toBeRemoved.length > 0) {
                            _.forEach(toBeRemoved, function(element) {
                                currentAuthGroups.splice(currentAuthGroups.indexOf(element), 1);
                            });

                            LdapUserManagementService.removeGroupsFromUser(selectedObject.key, toBeRemoved, selectedObject.directory).then(
                                    function(data) {
                                        MessageService.succsessAction();
                                    }, function() {
                                        //error adding group
                                        MessageService.errorAction();
                                    });
                            return deferred.promise;
                        }
                    }

                    function openCloneUserModal(userForm, passwordError, usernameError) {

                        return $modal.open({
                            animation : $scope.animationsEnabled,
                            templateUrl : 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                            controller : [ '$scope', '$modalInstance', 'UtilService', function($scope, $modalInstance, Util) {
                                $scope.addUser = true;
                                $scope.cloneUser = true;
                                $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                                $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                                $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                                $scope.user = userForm;
                                $scope.passwordErrorMessage = passwordError;
                                $scope.error = usernameError;
                                $scope.data = {
                                    "user" : $scope.user,
                                    "selectedUser" : selectedUser
                                };
                                $scope.clearPasswordError = function() {
                                    if ($scope.passwordErrorMessage) {
                                        $scope.passwordErrorMessage = '';
                                    }
                                };
                                $scope.clearUsernameError = function() {
                                    if ($scope.error) {
                                        $scope.error = '';
                                    }
                                };
                                $scope.passwordErrorMessages = {
                                    notSamePasswordsMessage : ''
                                };
                                $scope.ok = function() {
                                    $modalInstance.close($scope.data);
                                };
                            } ],
                            size : 'sm'
                        });
                    }

                    function onCloneUser(data, deferred) {
                        LdapUserManagementService.cloneUser(data).then(
                                function(response) {
                                    // add the new user to the list
                                    var element = {};
                                    element.name = response.data.fullName;
                                    element.key = response.data.userId;
                                    element.directory = response.data.userDirectoryName;
                                    $scope.userData.appUsers.push(element);

                                    //add the new user to cache store
                                    var cacheUsers = new Store.SessionData(LookupService.SessionCacheNames.USERS);
                                    var users = cacheUsers.get();
                                    users.push(element);
                                    cacheUsers.set(users);

                                    MessageService.succsessAction();
                                },
                                function(error) {
                                    //error adding user
                                    if (error.data.message) {
                                        var passwordError;
                                        var usernameError;
                                        var onAdd = function(data) {
                                            return onCloneUser(data);
                                        };
                                        if (error.data.field == 'username') {
                                            usernameError = error.data.message;
                                            openCloneUserModal(error.data.extra.user, passwordError, usernameError).result.then(onAdd,
                                                    function() {
                                                        deferred.reject("cancel");
                                                        return {};
                                                    });
                                        } else if (error.data.field == 'password') {
                                            passwordError = error.data.message;
                                            openCloneUserModal(error.data.extra.userForm, passwordError, usernameError).result.then(onAdd,
                                                    function() {
                                                        deferred.reject("cancel");
                                                        return {};
                                                    });
                                        } else {
                                            MessageService.error(error.data.message);
                                        }

                                    }

                                    else {
                                        MessageService.errorAction();
                                    }

                                });

                    }

                    function cloneUser() {
                        var modalInstance = openCloneUserModal({}, "");
                        var deferred = $q.defer();
                        modalInstance.result.then(function(data) {
                            onCloneUser(data, deferred);
                        }, function() {
                            // Cancel button was clicked
                        });

                    }

                    function fillList(listToFill, data) {
                        _.forEach(data, function(obj) {
                            var element = {};
                            element.name = obj.name;
                            element.key = obj.object_id_s;
                            element.directory = obj.directory_name_s;
                            listToFill.push(element);
                        });
                    }

                    function deleteUser() {
                        LdapUserManagementService.deleteUser(selectedUser).then(function() {

                            var cacheUsers = new Store.SessionData(LookupService.SessionCacheNames.USERS);
                            var users = cacheUsers.get();
                            var cacheKeyUser = _.find(users, {
                                'object_id_s' : selectedUser.key
                            });
                            cacheUsers.remove(cacheKeyUser);

                            $scope.userData.appUsers = _.reject($scope.userData.appUsers, function(element) {
                                return element.key === selectedUser.key;
                            });

                            MessageService.succsessAction();
                        }, function() {
                            MessageService.errorAction();
                        });
                    }

                    $scope.$bus.subscribe('ChooseUserManagementFilter', function(data) {
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            LdapUserManagementService.getNUsers(data).then(function(response) {
                                $scope.userData.appUsers = [];
                                $scope.fillList($scope.userData.appUsers, response.data.response.docs);
                            }, function() {
                                error('Error during returning n users');
                            });
                        } else {
                            LdapUserManagementService.getUsersFiltered(data).then(function success(response) {
                                $scope.userData.appUsers = [];
                                $scope.fillList($scope.userData.appUsers, response.data);
                            }, function() {
                                $log.error('Error during returning the filtered(by word) groups for user');
                            });
                        }
                    });

                    $scope.$bus.subscribe('UnauthorizedUserManagementFilter', function(data) {
                        data.isAuthorized = false;
                        data.member_id = $scope.lastSelectedUser;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            LdapUserManagementService.getGroupsForUser(data).then(function(response) {
                                $scope.userData.selectedNotAuthorized = [];
                                $scope.fillList($scope.userData.selectedNotAuthorized, response.data.response.docs);
                            }, function() {
                                $log.error('Error during returning unathorized groups for user');
                            });
                        } else {
                            LdapUserManagementService.getGroupsFiltered(data).then(function(response) {
                                $scope.userData.selectedNotAuthorized = [];
                                $scope.fillList($scope.userData.selectedNotAuthorized, response.data.response.docs);
                            }, function() {
                                $log.error('Error during returning the filtered(unathorized) groups for user');
                            });
                        }
                    });

                    $scope.$bus.subscribe('AuthorizedUserManagementFilter', function(data) {
                        data.isAuthorized = true;
                        data.member_id = $scope.lastSelectedUser;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            LdapUserManagementService.getGroupsForUser(data).then(function(response) {
                                $scope.userData.selectedAuthorized = [];
                                $scope.fillList($scope.userData.selectedAuthorized, response.data.response.docs);
                            }, function() {
                                error('Error during returning athorized groups for user');
                            });
                        } else {
                            LdapUserManagementService.getGroupsFiltered(data).then(function(response) {
                                $scope.userData.selectedAuthorized = [];
                                $scope.fillList($scope.userData.selectedAuthorized, response.data.response.docs);
                            }, function() {
                                error('Error during returning the filtered(athorized) groups for user');
                            });
                        }
                    });

                } ]);
