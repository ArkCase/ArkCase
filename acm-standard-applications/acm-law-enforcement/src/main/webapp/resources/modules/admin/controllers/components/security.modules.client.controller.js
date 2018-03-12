'use strict';

angular.module('admin').controller(
        'Admin.ModulesController',
        [ '$scope', 'Admin.ModulesService', 'Admin.SelectPrivilegesService', '$q',
                function($scope, modulesService, selectPrivilegesService, $q) {
                    var tempAppModulesPromise = modulesService.getAppModulesPaged({});
                    var tempAppRolesPromise = selectPrivilegesService.getAppRoles();

                    $scope.fillList = fillList;
                    //scroll functions
                    $scope.privilegeScroll = privilegeScroll;
                    //filter functions
                    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
                    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
                    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;
                    $scope.appRoles = [];
                    $scope.showFilter = true;
                    // Loaded data after the initialization
                    var initModulesData = {
                        "notAuthorized" : [],
                        "authorized" : []
                    };
                    $scope.modulesData = {
                        "chooseObject" : [],
                        "selectedNotAuthorized" : [],
                        "selectedAuthorized" : []
                    };
                    $scope.scrollLoadData = {
                        "loadObjectsScroll" : $scope.privilegeScroll
                    };
                    $scope.filterData = {
                        "objectsFilter" : $scope.chooseAppRoleFilter,
                        "unauthorizedFilter" : $scope.appRoleUnauthorizedFilter,
                        "authorizedFilter" : $scope.appRoleAuthorizedFilter
                    };

                    $scope.currentAuthRoles = [];

                    function fillList(listToFill, data) {
                        _.forEach(data, function(obj) {
                            var element = {};
                            element.key = obj;
                            element.name = obj;
                            listToFill.push(element);
                        });
                    }

                    function privilegeScroll() {
                        var data = {};
                        data.start = $scope.modulesData.chooseObject.length;
                        modulesService.getAppModulesPaged(data).then(function(response) {
                            $scope.modulesData.chooseObject = $scope.modulesData.chooseObject.concat(response.data);
                            $scope.onObjSelect($scope.lastSelectedPrivilege);
                        });
                    }

                    function chooseAppRoleFilter(data) {
                        modulesService.getAppModulesByName(data).then(function(response) {
                            $scope.modulesData.chooseObject = response.data;
                            $scope.onObjSelect($scope.modulesData.chooseObject[0]);
                        });
                    }

                    function appRoleUnauthorizedFilter(data) {
                        $scope.modulesData.selectedNotAuthorized = [];

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.modulesData.selectedNotAuthorized = _.filter(initModulesData.notAuthorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.modulesData.selectedNotAuthorized = angular.copy(initModulesData.notAuthorized);
                        }
                    }

                    function appRoleAuthorizedFilter(data) {
                        $scope.modulesData.selectedAuthorized = [];

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.modulesData.selectedAuthorized = _.filter(initModulesData.authorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.modulesData.selectedAuthorized = angular.copy(initModulesData.authorized);
                        }
                    }

                    //wait all promises to resolve
                    $q.all([ tempAppModulesPromise, tempAppRolesPromise ]).then(function(payload) {
                        //get N appPrivileges
                        $scope.modulesData.chooseObject = payload[0].data;

                        //get all app roles
                        $scope.appRoles = payload[1].data;

                        $scope.onObjSelect($scope.modulesData.chooseObject[0]);
                    });

                    //callback function when app role is selected
                    $scope.onObjSelect = function(selectedObject, authorized, notAuthorized) {
                        $scope.lastSelectedPrivilege = {};
                        $scope.lastSelectedPrivilege = selectedObject;

                        var rolesForModulePromise = modulesService.getRolesForModulePrivilege(selectedObject['privilege']);
                        rolesForModulePromise.then(function(payload) {
                            $scope.currentAuthRoles = payload.data;
                            initModulesData.authorized = [];
                            initModulesData.notAuthorized = [];

                            //set authorized roles
                            fillList(initModulesData.authorized, $scope.currentAuthRoles);
                            $scope.modulesData.selectedAuthorized = angular.copy(initModulesData.authorized);

                            //set not authorized roles
                            angular.forEach($scope.appRoles, function(role) {
                                if ($scope.currentAuthRoles.indexOf(role) == -1) {
                                    //we need to create wrapper to provide a name property
                                    var notAuthorizedRole = {};
                                    notAuthorizedRole.key = role;
                                    notAuthorizedRole.name = role;
                                    initModulesData.notAuthorized.push(notAuthorizedRole);
                                }
                            });
                            $scope.modulesData.selectedNotAuthorized = angular.copy(initModulesData.notAuthorized);

                        });
                    };

                    //callback function when groups are moved
                    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
                        var toBeAdded = [];
                        var toBeRemoved = [];
                        var deferred = $q.defer();

                        //get roles which needs to be added
                        angular.forEach(authorized, function(role) {
                            if ($scope.currentAuthRoles.indexOf(role.key) == -1) {
                                toBeAdded.push(role.key);
                            }
                        });
                        //perform adding on server
                        if (toBeAdded.length > 0) {
                            modulesService.addRolesToModule(selectedObject['privilege'], toBeAdded).then(function() {
                                deferred.resolve();
                            }, function() {
                                deferred.reject();
                            });

                            $scope.currentAuthRoles = $scope.currentAuthRoles.concat(toBeAdded);
                            return deferred.promise;
                        }

                        //get roles which needs to be removed
                        angular.forEach(notAuthorized, function(role) {
                            if ($scope.currentAuthRoles.indexOf(role.key) != -1) {
                                toBeRemoved.push(role.key);
                            }
                        });
                        if (toBeRemoved.length > 0) {
                            //perform removing on server
                            modulesService.removeRolesFromModule(selectedObject['privilege'], toBeRemoved).then(function() {
                                deferred.resolve();
                            }, function() {
                                deferred.reject();
                            });

                            //remove from $scope.currentAuthRoles
                            angular.forEach(toBeRemoved, function(element) {
                                $scope.currentAuthRoles.splice($scope.currentAuthRoles.indexOf(element), 1);
                            });

                            return deferred.promise;
                        }
                    };
                } ]);