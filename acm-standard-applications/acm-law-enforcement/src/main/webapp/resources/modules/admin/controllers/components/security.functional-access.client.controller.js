'use strict';

angular.module('admin').controller('Admin.FunctionalAccessController', [ '$scope', 'Admin.FunctionalAccessControlService', '$q', '$log', 'UtilService', 'MessageService', function($scope, FunctionalAccessControlService, $q, $log, Util, MessageService) {
    var tempAppRolesPromise = FunctionalAccessControlService.getAppRolesPaged({});
    var tempUserGroupsPromise = FunctionalAccessControlService.getUserGroups();
    var tempAppRolesUserGroupsPromise = FunctionalAccessControlService.getAppUserToGroups();

    $scope.onObjSelect = onObjSelect;
    $scope.fillList = fillList;
    //scroll functions
    $scope.rolesScroll = rolesScroll;
    $scope.unauthorizedScroll = unauthorizedScroll;
    $scope.authorizedScroll = authorizedScroll;
    $scope.retrieveDataScroll = retrieveDataScroll;
    $scope.onAuthRoleSelected = onAuthRoleSelected;
    //filter functions
    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;

    var currentAuthGroups = [];
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
        "loadObjectsScroll": $scope.rolesScroll,
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
        $scope.fillList($scope.rolesData.chooseObject, payload[0].data);

        $scope.onObjSelect($scope.rolesData.chooseObject[0]);
    });

    //callback function when app role is selected
    function onObjSelect(selectedObject) {
        if (!_.isEmpty($scope.rolesData.chooseObject)) {
            $scope.lastSelectedRole = selectedObject;
            var data = {};
            data.roleName = selectedObject;
            currentAuthGroups = [];

            data.isAuthorized = true;
            var authorizedGroupsForRole = FunctionalAccessControlService.getGroupsForRolePaged(data);
            data.isAuthorized = false;
            var unAuthorizedGroupsForRole = FunctionalAccessControlService.getGroupsForRolePaged(data);

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
                    currentAuthGroups.push(authObject.key);
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

            FunctionalAccessControlService.addGroupsToApplicationRole(selectedObject.key, toBeAdded).then(function(data) {
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

            FunctionalAccessControlService.deleteGroupsFromApplicationRole(selectedObject.key, toBeRemoved).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }
    }

    function retrieveDataScroll(data, methodName, panelName) {
        FunctionalAccessControlService[methodName](data).then(function(response) {
            if (_.isArray(response.data)) {
                $scope.fillList($scope.rolesData[panelName], response.data);
            } else {
                $scope.fillList($scope.rolesData[panelName], response.data.response.docs);
            }
            if (panelName === "selectedAuthorized") {
                currentAuthGroups = [];
                _.forEach($scope.rolesData[panelName], function(obj) {
                    currentAuthGroups.push(obj.key);
                });
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function rolesScroll() {
        var data = {
            start: $scope.rolesData.chooseObject.length
        };
        $scope.retrieveDataScroll(data, "getAppRolesPaged", "chooseObject");
    }

    function authorizedScroll() {
        var data = {};
        data.roleName = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getGroupsForRolePaged", "selectedAuthorized");
    }

    function unauthorizedScroll() {
        var data = {};
        data.roleName = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getGroupsForRolePaged", "selectedNotAuthorized");
    }

    function fillList(listToFill, data) {
        _.forEach(data, function(obj) {
            var element = {};
            element.name = obj;
            element.key = obj;
            listToFill.push(element);
        });
    }

    function chooseAppRoleFilter(searchData) {
        var data = {
            filterWord: searchData.filterWord
        };
        FunctionalAccessControlService.getAppRolesByName(data).then(function(response) {
            $scope.rolesData.chooseObject = [];
            $scope.fillList($scope.rolesData.chooseObject, response.data);
        });
    }

    function appRoleUnauthorizedFilter(searchData) {
        $scope.rolesData.selectedNotAuthorized = [];

        var data = {
            isAuthorized: false,
            roleName: $scope.lastSelectedRole,
            filterWord: searchData.filterWord
        };
        FunctionalAccessControlService.getGroupsForRoleByName(data).then(function(response) {
            $scope.rolesData.selectedNotAuthorized = [];
            $scope.fillList($scope.rolesData.selectedNotAuthorized, response.data);
        });
    }

    function appRoleAuthorizedFilter(searchData) {
        $scope.rolesData.selectedAuthorized = [];

        var data = {
            isAuthorized: true,
            roleName: $scope.lastSelectedRole,
            filterWord: searchData.filterWord
        };
        FunctionalAccessControlService.getGroupsForRoleByName(data).then(function(response) {
            $scope.rolesData.selectedAuthorized = [];
            $scope.fillList($scope.rolesData.selectedAuthorized, response.data);
        });
    }
} ]);
