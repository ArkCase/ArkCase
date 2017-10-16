'use strict';

angular.module('admin').controller('Admin.ModulesController', ['$scope', 'Admin.ModulesService', 'Admin.SelectPrivilegesService', '$q',
    function ($scope, modulesService, selectPrivilegesService, $q)
    {
        var tempAppModulesPromise = modulesService.getAppModules();
        var tempAppRolesPromise = selectPrivilegesService.getAppRoles();

        $scope.appModules = [];
        $scope.appRoles = [];
        $scope.currentAuthRoles = [];

        //wait all promises to resolve
        $q.all([tempAppModulesPromise, tempAppRolesPromise]).then(function (payload)
        {

            //get all appRoles
            $scope.appModules = payload[0].data;

            //get all app roles
            $scope.appRoles = payload[1].data;
        });

        //callback function when app role is selected
        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized)
        {

            var rolesForModulePromise = modulesService.getRolesForModulePrivilege(selectedObject['privilege']);
            rolesForModulePromise.then(function (payload)
            {
                //set authorized roles
                $scope.currentAuthRoles = payload.data;
                angular.forEach($scope.currentAuthRoles, function (element)
                {
                    //we need to create wrapper to provide a name property
                    var authObject = {};
                    authObject.key = element;
                    authObject.name = element;
                    authorized.push(authObject);
                });

                //set not authorized roles.
                angular.forEach($scope.appRoles, function (role)
                {
                    if ($scope.currentAuthRoles.indexOf(role) == -1)
                    {
                        //we need to create wrapper to provide a name property
                        var notAuthorizedRole = {};
                        notAuthorizedRole.key = role;
                        notAuthorizedRole.name = role;
                        notAuthorized.push(notAuthorizedRole);
                    }
                });
            });
        };

        //callback function when groups are moved
        $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized)
        {
            var toBeAdded = [];
            var toBeRemoved = [];
            var deferred = $q.defer();

            //get roles which needs to be added
            angular.forEach(authorized, function (role)
            {
                if ($scope.currentAuthRoles.indexOf(role.key) == -1)
                {
                    toBeAdded.push(role.key);
                }
            });
            //perform adding on server
            if (toBeAdded.length > 0)
            {
                modulesService.addRolesToModule(selectedObject['privilege'], toBeAdded).then(function() {
                	deferred.resolve();
                }, function(){
                	deferred.reject();
                });
                
                $scope.currentAuthRoles = $scope.currentAuthRoles.concat(toBeAdded);
                return deferred.promise;
            }

            //get roles which needs to be removed
            angular.forEach(notAuthorized, function (role)
            {
                if ($scope.currentAuthRoles.indexOf(role.key) != -1)
                {
                    toBeRemoved.push(role.key);
                }
            });
            if (toBeRemoved.length > 0)
            {
                //perform removing on server
                modulesService.removeRolesFromModule(selectedObject['privilege'], toBeRemoved).then(function() {
                	deferred.resolve();
                }, function(){
                	deferred.reject();
                });
                
                //remove from $scope.currentAuthRoles
                angular.forEach(toBeRemoved, function (element)
                {
                    $scope.currentAuthRoles.splice($scope.currentAuthRoles.indexOf(element), 1);
                });
                
                return deferred.promise;
            }
        };
    }
]);