'use strict';

angular.module('admin').controller('Admin.FunctionalAccessController', ['$scope', 'Admin.FunctionalAccessControlService', '$q',
    function ($scope, functionalAccessControlService, $q) {
        var tempAppRolesPromise = functionalAccessControlService.getAppRoles();
        var tempUserGroupsPromise = functionalAccessControlService.getUserGroups();
        var tempAppRolesUserGroupsPromise = functionalAccessControlService.getAppUserToGroups();
        var deferred = $q.defer();
        
        $scope.appRoles = [];
        $scope.userGroupsAll = [];

        //wait all promises to resolve
        $q.all([tempAppRolesPromise, tempUserGroupsPromise, tempAppRolesUserGroupsPromise]).then(function (payload) {
            //get all appRoles
            angular.forEach(payload[0].data, function (appRole) {
                var element = new Object;
                element.name = appRole;
                element.key = appRole;
                $scope.appRoles.push(element);
            });

            //get all user groups
            angular.forEach(payload[1].data.response.docs, function (userGroup) {
                $scope.userGroupsAll[userGroup['object_id_s']] = userGroup;
            });

            //get all app roles to groups
            $scope.appRolesUserGroups = payload[2].data;
        });

        //callback function when app role is selected
        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized) {

            //set authorized groups
            angular.forEach($scope.appRolesUserGroups[selectedObject.key], function (element) {
                //we need to create wrapper, since appRolesUserGroups doesn't have name which directive expect to have
                var authObject = {};
                authObject.key = element;
                authObject.name = element;
                authorized.push(authObject);
            });

            //set not authorized groups.
            // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
            for (var key in $scope.userGroupsAll) {
                // appRolesUserGroups might not have this particular selected object at all.
                if ($scope.appRolesUserGroups[selectedObject.key] === undefined || $scope.appRolesUserGroups[selectedObject.key].indexOf(key) == -1) {
                    var notAuthObject = {};
                    notAuthObject.key = key;
                    notAuthObject.name = key;
                    notAuthorized.push(notAuthObject);
                }
            }
        };

        //callback function when groups are moved
        $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized) {
        	
            //get authorized user groups for selected app role and save all app roles user groups
            $scope.appRolesUserGroups[selectedObject.key] = [];
            angular.forEach(authorized, function (element) {
                $scope.appRolesUserGroups[selectedObject.key].push(element.key);
            });
            functionalAccessControlService.saveAppRolesToGroups($scope.appRolesUserGroups).then(function() {
            	deferred.resolve();
            }, function(){
            	deferred.reject();
            });

            return deferred.promise;
        };
    }
]);