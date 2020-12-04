'use strict';

angular.module('admin').controller('Admin.ModulesController', [ '$scope', 'Admin.ModulesService', 'Admin.SelectPrivilegesService', '$q', function($scope, ModulesService, SelectPrivilegesService, $q) {
    var tempAppModulesPromise = ModulesService.getAppModulesPaged({});
    var tempAppRolesPromise = SelectPrivilegesService.getAppRoles();

    $scope.fillList = fillList;
    $scope.retrieveDataScroll = retrieveDataScroll;
    //scroll functions
    $scope.privilegeScroll = privilegeScroll;
    $scope.unauthorizedScroll = unauthorizedScroll;
    $scope.authorizedScroll = authorizedScroll;
    //filter functions
    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;
    $scope.appRoles = [];
    $scope.lastSelectedModule = {};
    // Loaded data after the initialization
    var initModulesData = {
        "notAuthorized": [],
        "authorized": []
    };
    $scope.modulesData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.scrollLoadData = {
        "loadObjectsScroll": $scope.privilegeScroll,
        "loadUnauthorizedScroll": $scope.unauthorizedScroll,
        "loadAuthorizedScroll": $scope.authorizedScroll
    };
    $scope.filterData = {
        "objectsFilter": $scope.chooseAppRoleFilter,
        "unauthorizedFilter": $scope.appRoleUnauthorizedFilter,
        "authorizedFilter": $scope.appRoleAuthorizedFilter
    };

    var currentAuthRoles = [];

    function fillList(listToFill, data) {
        _.forEach(data, function(obj) {
            listToFill.push({
                key: obj,
                name: obj
            });
        });
    }

    function retrieveDataScroll(data, methodName, panelName) {
        ModulesService[methodName](data).then(function(response) {
            $scope.fillList($scope.modulesData[panelName], response.data);

            if (panelName === "selectedAuthorized") {
                currentAuthRoles = [];
                _.forEach($scope.modulesData[panelName], function(obj) {
                    currentAuthRoles.push(obj.key);
                });
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function privilegeScroll() {
        var data = {
            start: $scope.modulesData.chooseObject.length
        };
        ModulesService.getAppModulesPaged(data).then(function(response) {
            $scope.modulesData.chooseObject = $scope.modulesData.chooseObject.concat(response.data);
            $scope.onObjSelect($scope.lastSelectedModule);
        });
    }

    function authorizedScroll() {
        var data = {
            module: $scope.lastSelectedModule,
            start: $scope.modulesData.selectedAuthorized.length,
            isAuthorized: true
        };
        $scope.retrieveDataScroll(data, "getRolesForModulePaged", "selectedAuthorized");
    }

    function unauthorizedScroll() {
        var data = {
            module: $scope.lastSelectedModule,
            start: $scope.modulesData.selectedNotAuthorized.length,
            isAuthorized: false
        };
        $scope.retrieveDataScroll(data, "getRolesForModulePaged", "selectedNotAuthorized");
    }

    function chooseAppRoleFilter(data) {
        ModulesService.getAppModulesByName(data).then(function(response) {
            $scope.modulesData.chooseObject = response.data;
            $scope.onObjSelect($scope.modulesData.chooseObject[0]);
        });
    }

    function appRoleUnauthorizedFilter(searchData) {
        $scope.modulesData.selectedNotAuthorized = [];

        var data = {
            isAuthorized: false,
            module: $scope.lastSelectedModule,
            filterWord: searchData.filterWord
        };
        ModulesService.getRolesForModuleByName(data).then(function(response) {
            $scope.modulesData.selectedNotAuthorized = [];
            $scope.fillList($scope.modulesData.selectedNotAuthorized, response.data);
        });
    }

    function appRoleAuthorizedFilter(searchData) {
        $scope.modulesData.selectedAuthorized = [];

        var data = {
            isAuthorized: true,
            module: $scope.lastSelectedModule,
            filterWord: searchData.filterWord
        };
        ModulesService.getRolesForModuleByName(data).then(function(response) {
            $scope.modulesData.selectedAuthorized = [];
            $scope.fillList($scope.modulesData.selectedAuthorized, response.data);
        });
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
    $scope.onObjSelect = function(selectedObject) {
        $scope.lastSelectedModule = selectedObject;

        var data = {
            module: selectedObject,
            isAuthorized: false
        };
        var unAuthorizedRolesGroupsForWidgetPromise = ModulesService.getRolesForModulePaged(data);
        data.isAuthorized = true;
        var authorizedRolesGroupsForWidgetPromise = ModulesService.getRolesForModulePaged(data);

        $q.all([ authorizedRolesGroupsForWidgetPromise, unAuthorizedRolesGroupsForWidgetPromise ]).then(function(payload) {
            $scope.modulesData.selectedAuthorized = [];
            $scope.modulesData.selectedNotAuthorized = [];
            currentAuthRoles = [];
            _.forEach(payload[0].data, function(item) {
                var element = {};
                element.key = item;
                element.name = item;
                $scope.modulesData.selectedAuthorized.push(element);
                currentAuthRoles.push(element.key);
            });

            fillList($scope.modulesData.selectedNotAuthorized, payload[1].data);
        });
    };

    //callback function when groups are moved
    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
        var toBeAdded = [];
        var toBeRemoved = [];
        var deferred = $q.defer();

        //get roles which needs to be added
        angular.forEach(authorized, function(role) {
            if (currentAuthRoles.indexOf(role.key) == -1) {
                toBeAdded.push(role.key);
            }
        });
        //perform adding on server
        if (toBeAdded.length > 0) {
            ModulesService.addRolesToModule(selectedObject['privilege'], toBeAdded).then(function() {
                deferred.resolve();
            }, function() {
                deferred.reject();
            });

            currentAuthRoles = currentAuthRoles.concat(toBeAdded);
            return deferred.promise;
        }

        //get roles which needs to be removed
        angular.forEach(notAuthorized, function(role) {
            if (currentAuthRoles.indexOf(role.key) != -1) {
                toBeRemoved.push(role.key);
            }
        });
        if (toBeRemoved.length > 0) {
            //perform removing on server
            ModulesService.removeRolesFromModule(selectedObject['privilege'], toBeRemoved).then(function() {
                deferred.resolve();
            }, function() {
                deferred.reject();
            });

            //remove from currentAuthRoles
            angular.forEach(toBeRemoved, function(element) {
                currentAuthRoles.splice(currentAuthRoles.indexOf(element), 1);
            });

            return deferred.promise;
        }
    };
} ]);
