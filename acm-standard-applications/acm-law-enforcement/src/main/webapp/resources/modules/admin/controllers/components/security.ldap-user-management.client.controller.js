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
                function($scope, $q, $modal, $timeout, LdapUserManagementService, LookupService, MessageService, Store, Util) {

                    $scope.cloneUser = cloneUser;
                    $scope.onObjSelect = onObjSelect;
                    $scope.onAuthRoleSelected = onAuthRoleSelected;

                    $scope.showFilter = true;
                    $scope.appUsers = [];
                    $scope.appGroups = [];

                    $scope.userData = {
                        "appUsers" : $scope.appUsers,
                        "selectedNotAuthorized" : [],
                        "selectedAuthorized" : []
                    };

                    $scope.$watch('appUsers', function() {
                        $scope.userData.appUsers = $scope.appUsers;
                    }, true);

                    LdapUserManagementService.getNUsers({}).then(function(response) {
                        _.forEach(response.data, function(user) {
                            var element = {};
                            element.name = user.name;
                            element.key = user.object_id_s;
                            element.directory = user.directory_name_s;
                            $scope.appUsers.push(element);
                        });
                    });

                    $scope.lastSelectedUser = "";
                    var selectedUser;
                    var currentAuthGroups;

                    //callback function when user is selected
                    function onObjSelect(selectedObject, authorized, notAuthorized) {
                        var data = {};
                        data.member_id = selectedObject;
                        selectedUser = selectedObject;
                        $scope.lastSelectedUser = selectedUser;
                        currentAuthGroups = [];

                        var allUnauthorizedGroups = LdapUserManagementService.getAllUnauthorizedGroups(data);
                        var allAuthorizedGroups = LdapUserManagementService.getAllAuthorizedGroups(data);

                        $q.all([ allUnauthorizedGroups, allAuthorizedGroups ]).then(function(result) {
                            _.forEach(result[0].data.response.docs, function(group) {
                                var authObject = {};
                                authObject.key = group.name;
                                authObject.name = group.name;
                                notAuthorized.push(authObject);
                            });

                            _.forEach(result[1].data.response.docs, function(group) {
                                var authObject = {};
                                authObject.key = group.name;
                                authObject.name = group.name;
                                authorized.push(authObject);
                                currentAuthGroups.push(authObject.key);
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
                                    $scope.appUsers.push(element);

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

                    $scope.deleteUser = function() {
                        LdapUserManagementService.deleteUser(selectedUser).then(function() {

                            var cacheUsers = new Store.SessionData(LookupService.SessionCacheNames.USERS);
                            var users = cacheUsers.get();
                            var cacheKeyUser = _.find(users, {
                                'object_id_s' : selectedUser.key
                            });
                            cacheUsers.remove(cacheKeyUser);

                            $scope.appUsers = _.reject($scope.appUsers, function(element) {
                                return element.key === selectedUser.key;
                            });

                            MessageService.succsessAction();
                        }, function() {
                            MessageService.errorAction();
                        });
                    };

                    $scope.$bus.subscribe('ChooseUserManagementFilter', function(data) {
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 20 : data.n;
                            LdapUserManagementService.getNUsers(data).then(function(response) {
                                $scope.appUsers = [];
                                if (!Util.isEmpty(response.data)) {
                                    $scope.fillList($scope.appUsers, response.data);
                                }
                            });
                        } else {
                            LdapUserManagementService.getFilteredUsersByWord(data).then(function(response) {
                                $scope.appUsers = [];
                                if (!Util.isEmpty(response.data)) {
                                    $scope.fillList($scope.appUsers, response.data);
                                }
                            }, function() {
                                console.log("error");
                            });
                        }
                    });

                    $scope.$bus.subscribe('UnauthorizedUserManagementFilter', function(data) {
                        data.member_id = $scope.lastSelectedUser;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 20 : data.n;
                            LdapUserManagementService.getAllUnauthorizedGroups(data).then(function(response) {
                                $scope.userData.selectedNotAuthorized = [];
                                if (!Util.isEmpty(response.data.response.docs)) {
                                    $scope.fillList($scope.userData.selectedNotAuthorized, response.data.response.docs);
                                }
                            });
                        } else {
                            LdapUserManagementService.getFilteredUnauthorizedGroups(data).then(function(response) {
                                $scope.userData.selectedNotAuthorized = [];
                                if (!Util.isEmpty(response.data.response.docs)) {
                                    $scope.fillList($scope.userData.selectedNotAuthorized, response.data.response.docs);
                                }
                            }, function() {
                                console.log("error");
                            });
                        }
                    });

                    $scope.$bus.subscribe('AuthorizedUserManagementFilter', function(data) {
                        data.member_id = $scope.lastSelectedUser;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 20 : data.n;
                            LdapUserManagementService.getAllAuthorizedGroups(data).then(function(response) {
                                $scope.userData.selectedAuthorized = [];
                                if (!Util.isEmpty(response.data.response.docs)) {
                                    $scope.fillList($scope.userData.selectedAuthorized, response.data.response.docs);
                                }
                            });
                        } else {
                            LdapUserManagementService.getFilteredAuthorizedGroups(data).then(function(response) {
                                $scope.userData.selectedAuthorized = [];
                                if (!Util.isEmpty(response.data.response.docs)) {
                                    $scope.fillList($scope.userData.selectedAuthorized, response.data.response.docs);
                                }
                            }, function() {
                                console.log("error");
                            });
                        }
                    });

                    $scope.fillList = function(listToFill, data) {
                        _.forEach(data, function(obj) {
                            var element = {};
                            element.name = obj.name;
                            element.key = obj.object_id_s;
                            element.directory = obj.directory_name_s;
                            listToFill.push(element);
                        });
                    };
                } ]);
