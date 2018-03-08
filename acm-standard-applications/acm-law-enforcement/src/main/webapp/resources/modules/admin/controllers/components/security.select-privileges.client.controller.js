'use strict';

angular.module('admin').controller(
        'Admin.SelectPrivilegesController',
        [
                '$scope',
                'Admin.SelectPrivilegesService',
                '$q',
                '$modal',
                '$translate',
                '$log',
                'UtilService',
                function($scope, selectPrivilegesService, $q, $modal, $translate, $log, Util) {
                    var tempAppRolesPromise = selectPrivilegesService.getAppRoles();

                    $scope.fillList = fillList;
                    $scope.onObjSelect = onObjSelect;
                    //filter functions
                    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
                    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
                    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;
                    //scroll functions
                    $scope.unauthorizedScroll = unauthorizedScroll;
                    $scope.authorizedScroll = authorizedScroll;
                    $scope.retrieveDataScroll = retrieveDataScroll;

                    $scope.lastSelectedRole = null;
                    $scope.showFilter = true;
                    // Loaded data after the initialization
                    var initRolesData = {
                        "chooseObject" : [],
                        "notAuthorized" : [],
                        "authorized" : []
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
                    $scope.scrollLoadData = {
                        "loadUnauthorizedScroll" : $scope.unauthorizedScroll,
                        "loadAuthorizedScroll" : $scope.authorizedScroll
                    };

                    //wait all promises to resolve
                    tempAppRolesPromise.then(function(response) {
                        //get all appRoles
                        angular.forEach(response.data, function(appRole) {
                            var element = new Object;
                            element.name = appRole;
                            element.key = appRole;
                            initRolesData.chooseObject.push(element);
                        });

                        //init the displaying object
                        $scope.rolesData.chooseObject = angular.copy(initRolesData.chooseObject);

                        $scope.onObjSelect($scope.rolesData.chooseObject[0], $scope.rolesData.selectedAuthorized,
                                $scope.rolesData.selectedNotAuthorized);
                    });

                    function fillList(listToFill, data) {
                        _.forEach(data, function(key, value) {
                            var element = {};
                            element.key = key;
                            element.name = value;
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
                        $scope.onObjSelect($scope.rolesData.chooseObject[0]);
                    }

                    function appRoleUnauthorizedFilter(data) {
                        $scope.rolesData.selectedNotAuthorized = [];

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.rolesData.selectedNotAuthorized = _.filter(initRolesData.notAuthorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.rolesData.selectedNotAuthorized = angular.copy(initRolesData.notAuthorized);
                        }
                    }

                    function appRoleAuthorizedFilter(data) {
                        $scope.rolesData.selectedAuthorized = [];

                        //filter
                        if (!_.isEmpty(data)) {
                            $scope.rolesData.selectedAuthorized = _.filter(initRolesData.authorized, function(item) {
                                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
                            });
                        } else {
                            $scope.rolesData.selectedAuthorized = angular.copy(initRolesData.authorized);
                        }
                    }

                    function retrieveDataScroll(data, methodName, panelName) {
                        selectPrivilegesService[methodName](data).then(function(response) {
                            if (_.isArray(response.data)) {
                                $scope.fillList($scope.rolesData[panelName], response.data);
                            } else {
                                $scope.fillList($scope.rolesData[panelName], response.data);
                            }
                        }, function() {
                            $log.error('Error during calling the method ' + methodName);
                        });
                    }

                    function authorizedScroll() {
                        var data = {};
                        data.role = $scope.lastSelectedRole;
                        data.n = Util.isArrayEmpty($scope.rolesData.selectedAuthorized) ? 18 : $scope.rolesData.selectedAuthorized.length;
                        data.start = $scope.rolesData.selectedAuthorized.length;
                        data.isAuthorized = true;
                        $scope.retrieveDataScroll(data, "getNRolePrivileges", "selectedAuthorized");
                    }

                    function unauthorizedScroll() {
                        var data = {};
                        data.role = $scope.lastSelectedRole;
                        data.n = Util.isArrayEmpty($scope.rolesData.selectedNotAuthorized) ? 18
                                : $scope.rolesData.selectedNotAuthorized.length;
                        data.start = $scope.rolesData.selectedNotAuthorized.length;
                        data.isAuthorized = false;
                        $scope.retrieveDataScroll(data, "getNRolePrivileges", "selectedNotAuthorized");
                    }

                    //callback function when app role is selected
                    function onObjSelect(selectedObject, authorized, notAuthorized) {
                        $scope.rolesData.selectedAuthorized = [];
                        $scope.rolesData.selectedNotAuthorized = [];
                        if (!_.isEmpty($scope.rolesData.chooseObject)) {
                            $scope.lastSelectedRole = {};
                            $scope.lastSelectedRole = selectedObject;
                            var data = {};
                            data.role = selectedObject;
                            data.isAuthorized = true;
                            var unAuthorizedGroupsForUserPromise = selectPrivilegesService.getNRolePrivileges(data);
                            data.isAuthorized = false;
                            var authorizedGroupsForUserPromise = selectPrivilegesService.getNRolePrivileges(data);

                            //wait all promises to resolve
                            $q.all([ unAuthorizedGroupsForUserPromise, authorizedGroupsForUserPromise ]).then(function(payload) {
                                initRolesData.authorized = [];
                                initRolesData.notAuthorized = [];
                                fillList(initRolesData.authorized, payload[0].data);
                                fillList(initRolesData.notAuthorized, payload[1].data);
                                fillList($scope.rolesData.selectedAuthorized, payload[0].data);
                                fillList($scope.rolesData.selectedNotAuthorized, payload[1].data);
                            });
                        }
                    }

                    //callback function when groups are moved
                    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized, isClicked) {

                        var deferred = $q.defer();
                        var privileges = [];
                        angular.forEach(authorized, function(element) {
                            privileges.push(element.key);
                        });
                        selectPrivilegesService.addRolePrivileges(selectedObject.key, privileges).then(function() {
                            deferred.resolve();
                        }, function() {
                            deferred.reject();
                        });

                        return deferred.promise;
                    };

                    $scope.newRole = function() {
                        $scope.showModal(null);
                    };

                    $scope.editRole = function() {
                        $scope.showModal($scope.lastSelectedRole.key);
                    };

                    //dialog for edit or create new role
                    $scope.showModal = function(value) {
                        var modalInstance = $modal.open({
                            animation : true,
                            templateUrl : 'modules/admin/views/components/security.select-privileges.create-edit.dialog.html',
                            controller : function($scope, $modalInstance) {
                                $scope.inputValid = true;
                                $scope.roleName = value;
                                if (value == null) {
                                    $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.createRole');
                                } else {
                                    $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.applyChanges');
                                }

                                //watch the input to enable/disable ok button
                                $scope.$watch('roleName', function(newValue) {
                                    if (newValue) {
                                        $scope.inputValid = false;
                                    } else {
                                        $scope.inputValid = true;
                                    }
                                });
                                $scope.ok = function() {
                                    $scope.roleName = $scope.roleName.toUpperCase().replace(/\s+/g, "_");
                                    if (!$scope.roleName.startsWith("ROLE_")) {
                                        $scope.roleName = "ROLE_" + $scope.roleName;
                                    }
                                    $modalInstance.close($scope.roleName);
                                };
                                $scope.cancel = function() {
                                    $modalInstance.dismiss('cancel');
                                };
                            },
                            size : 'md'
                        });

                        //handle the result
                        modalInstance.result.then(function(result) {
                            //button ok
                            if (value == null) {
                                //handle create new item
                                selectPrivilegesService.upsertRole(result).then(function() {
                                    var element = new Object;
                                    element.name = result;
                                    element.key = result;
                                    $scope.rolesData.chooseObject.push(element);
                                });
                            } else {
                                //handle edit item
                                selectPrivilegesService.upsertRole(result, value).then(function() {
                                    $scope.lastSelectedRole.key = result;
                                    $scope.lastSelectedRole.name = result;
                                });

                            }
                        }, function(result) {
                            //button cancel, nothing to do.
                        });
                    }
                } ]);
