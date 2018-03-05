'use strict';

angular.module('admin').controller(
        'Admin.FunctionalAccessController',
        [
                '$scope',
                'Admin.FunctionalAccessControlService',
                '$q',
                function($scope, functionalAccessControlService, $q) {
                    var tempAppRolesPromise = functionalAccessControlService.getAppRoles();
                    var tempUserGroupsPromise = functionalAccessControlService.getUserGroups();
                    var tempAppRolesUserGroupsPromise = functionalAccessControlService.getAppUserToGroups();
                    var deferred = $q.defer();

                    $scope.onObjSelect = onObjSelect;
                    $scope.fillList = fillList;
                    $scope.onAuthRoleSelected = onAuthRoleSelected;
                    //filter functions
                    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
                    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
                    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;

                    var lastSelectedUser = "";
                    $scope.showFilter = true;
                    // Loaded data after the initialization
                    var initRolesData = {
                        "chooseObject" : [],
                        "userGroupsAll" : [],
                        "appRolesUserGroups" : []
                    };
                    // Data to be displayed
                    $scope.rolesData = {
                        "chooseObject" : [],
                        "selectedNotAuthorized" : [],
                        "selectedAuthorized" : []
                    };
                    $scope.filterData = {
                        "objectsFilter" : $scope.chooseAppRoleFilter,
                        "unauthorizedFilter" : $scope.appRoleUnauthorizedFilter,
                        "authorizedFilter" : $scope.appRoleAuthorizedFilter
                    };
                    $scope.userGroupsAll = [];

                    function fillList(listToFill, data) {
                        _.forEach(data, function(obj) {
                            var element = {};
                            element.name = obj;
                            element.key = obj;
                            listToFill.push(element);
                        });
                    }

                    function chooseAppRoleFilter(data) {
                        if (!_.isEmpty(data)) {
                            $scope.rolesData.chooseObject = _.filter(initRolesData.chooseObject, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.rolesData.chooseObject = angular.copy(initRolesData.chooseObject);
                        }
                    }

                    function appRoleUnauthorizedFilter(data) {
                        $scope.rolesData.selectedNotAuthorized = [];
                        var selectedNotAuthorized = [];
                        //set not authorized groups.
                        // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
                        for ( var key in initRolesData.userGroupsAll) {
                            // appRolesUserGroups might not have this particular selected object at all.
                            if (initRolesData.appRolesUserGroups[lastSelectedUser.key] === undefined
                                    || initRolesData.appRolesUserGroups[lastSelectedUser.key].indexOf(key) == -1) {
                                var notAuthObject = {};
                                notAuthObject.key = key;
                                notAuthObject.name = key;
                                selectedNotAuthorized.push(notAuthObject);
                            }
                        }

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.rolesData.selectedNotAuthorized = _.filter(selectedNotAuthorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.rolesData.selectedNotAuthorized = angular.copy(selectedNotAuthorized);
                        }
                    }

                    function appRoleAuthorizedFilter(data) {
                        $scope.rolesData.selectedAuthorized = [];
                        var selectedAuthorized = [];
                        //set authorized groups
                        fillList(selectedAuthorized, initRolesData.appRolesUserGroups[lastSelectedUser.key]);

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.rolesData.selectedAuthorized = _.filter(selectedAuthorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.rolesData.selectedAuthorized = angular.copy(selectedAuthorized);
                        }
                    }

                    //wait all promises to resolve
                    $q.all([ tempAppRolesPromise, tempUserGroupsPromise, tempAppRolesUserGroupsPromise ]).then(function(payload) {
                        //get all appRoles
                        angular.forEach(payload[0].data, function(appRole) {
                            var element = new Object;
                            element.name = appRole;
                            element.key = appRole;
                            initRolesData.chooseObject.push(element);
                        });

                        //get all user groups
                        angular.forEach(payload[1].data.response.docs, function(userGroup) {
                            initRolesData.userGroupsAll[userGroup['object_id_s']] = userGroup;
                        });

                        //get all app roles to groups
                        initRolesData.appRolesUserGroups = payload[2].data;

                        //init the displaying object
                        $scope.rolesData.chooseObject = angular.copy(initRolesData.chooseObject);
                    });

                    //callback function when app role is selected
                    function onObjSelect(selectedObject, authorized, notAuthorized) {
                        lastSelectedUser = selectedObject;

                        //set authorized groups
                        fillList(authorized, initRolesData.appRolesUserGroups[selectedObject.key]);

                        //set not authorized groups.
                        // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
                        for ( var key in initRolesData.userGroupsAll) {
                            // appRolesUserGroups might not have this particular selected object at all.
                            if (initRolesData.appRolesUserGroups[selectedObject.key] === undefined
                                    || initRolesData.appRolesUserGroups[selectedObject.key].indexOf(key) == -1) {
                                var notAuthObject = {};
                                notAuthObject.key = key;
                                notAuthObject.name = key;
                                notAuthorized.push(notAuthObject);
                            }
                        }
                    }

                    //callback function when groups are moved
                    function onAuthRoleSelected(selectedObject, authorized, notAuthorized) {

                        //get authorized user groups for selected app role and save all app roles user groups
                        initRolesData.appRolesUserGroups[selectedObject.key] = [];
                        angular.forEach(authorized, function(element) {
                            initRolesData.appRolesUserGroups[selectedObject.key].push(element.key);
                        });
                        functionalAccessControlService.saveAppRolesToGroups(initRolesData.appRolesUserGroups).then(function() {
                            deferred.resolve();
                        }, function() {
                            deferred.reject();
                        });

                        return deferred.promise;
                    }
                } ]);