'use strict';

angular.module('admin').controller('Admin.FunctionalAccessController', [ '$scope', 'Admin.FunctionalAccessControlService', '$q', '$log', 'UtilService', 'MessageService', function($scope, functionalAccessControlService, $q, $log, Util, MessageService) {
    var tempAppRolesPromise = functionalAccessControlService.getAppRoles();
    var tempUserGroupsPromise = functionalAccessControlService.getUserGroups();
    var tempAppRolesUserGroupsPromise = functionalAccessControlService.getAppUserToGroups();
    var deferred = $q.defer();

    $scope.onObjSelect = onObjSelect;
    $scope.fillList = fillList;
    //scroll functions
    $scope.unauthorizedScroll = unauthorizedScroll;
    $scope.authorizedScroll = authorizedScroll;
    $scope.retrieveDataScroll = retrieveDataScroll;
    $scope.onAuthRoleSelected = onAuthRoleSelected;
    //filter functions
    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;

    $scope.lastSelectedRole = "";
    $scope.userGroupsAll = [];
    // Loaded data after the initialization
    var initRolesData = {
        "chooseObject": [],
        "userGroupsAll": [],
        "appRolesUserGroups": []
    };
    // Data to be displayed
    $scope.rolesData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.scrollLoadData = {
        "loadUnauthorizedScroll": $scope.unauthorizedScroll,
        "loadAuthorizedScroll": $scope.authorizedScroll
    };
    $scope.filterData = {
        "objectsFilter": $scope.chooseAppRoleFilter,
        "unauthorizedFilter": $scope.appRoleUnauthorizedFilter,
        "authorizedFilter": $scope.appRoleAuthorizedFilter
    };

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

        $scope.onObjSelect($scope.rolesData.chooseObject[0]);
    });

    //callback function when app role is selected
    function onObjSelect(selectedObject) {
        if (!_.isEmpty($scope.rolesData.chooseObject)) {
            $scope.lastSelectedRole = [];
            $scope.lastSelectedRole = selectedObject;
            var data = {};
            data.roleName = selectedObject;

            data.isAuthorized = true;
            var authorizedGroupsForRole = functionalAccessControlService.getGroupsForRole(data);
            data.isAuthorized = false;
            var unAuthorizedGroupsForRole = functionalAccessControlService.getGroupsForRole(data);

            $q.all([ authorizedGroupsForRole, unAuthorizedGroupsForRole ]).then(function(result) {
                $scope.rolesData.selectedNotAuthorized = [];
                $scope.rolesData.selectedAuthorized = [];
                //set authorized groups
                angular.forEach(result[0].data, function(element) {
                    //we need to create wrapper, since appRolesUserGroups doesn't have name which directive expect to have
                    var authObject = {};
                    authObject.key = element;
                    authObject.name = element;
                    $scope.rolesData.selectedAuthorized.push(authObject);
                });

                //set unauthorized groups
                angular.forEach(result[1].data, function(element) {
                    //we need to create wrapper, since appRolesUserGroups doesn't have name which directive expect to have
                    var authObject = {};
                    authObject.key = element;
                    authObject.name = element;
                    $scope.rolesData.selectedNotAuthorized.push(authObject);
                });
            });
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

    function retrieveDataScroll(data, methodName, panelName) {
        functionalAccessControlService[methodName](data).then(function(response) {
            if (_.isArray(response.data)) {
                $scope.fillList($scope.rolesData[panelName], response.data);
            } else {
                $scope.fillList($scope.rolesData[panelName], response.data.response.docs);
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function authorizedScroll() {
        var data = {};
        data.roleName = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getGroupsForRole", "selectedAuthorized");
    }

    function unauthorizedScroll() {
        var data = {};
        data.roleName = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getGroupsForRole", "selectedNotAuthorized");
    }

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
        $scope.onObjSelect($scope.rolesData.chooseObject[0]);
    }

    function appRoleUnauthorizedFilter(data) {
        $scope.rolesData.selectedNotAuthorized = [];
        var selectedNotAuthorized = [];
        //set not authorized groups.
        // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
        for ( var key in initRolesData.userGroupsAll) {
            // appRolesUserGroups might not have this particular selected object at all.
            if (initRolesData.appRolesUserGroups[$scope.lastSelectedRole.key] === undefined || initRolesData.appRolesUserGroups[$scope.lastSelectedRole.key].indexOf(key) == -1) {
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
        fillList(selectedAuthorized, initRolesData.appRolesUserGroups[$scope.lastSelectedRole.key]);

        //filter
        if (!_.isEmpty(data)) {
            $scope.rolesData.selectedAuthorized = _.filter(selectedAuthorized, function(item) {
                return (item.name.toLowerCase().indexOf(data.filterWord.toLowerCase()) >= 0);
            });
        } else {
            $scope.rolesData.selectedAuthorized = angular.copy(selectedAuthorized);
        }
    }
} ]);