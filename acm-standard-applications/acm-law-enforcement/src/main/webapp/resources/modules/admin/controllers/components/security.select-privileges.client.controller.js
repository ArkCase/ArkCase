'use strict';

angular.module('admin').controller('Admin.SelectPrivilegesController', ['$scope', 'Admin.SelectPrivilegesService', '$q', '$modal', '$translate',
    function ($scope, selectPrivilegesService, $q, $modal, $translate) {
        var tempAppRolesPromise = selectPrivilegesService.getAppRoles();
        var tempAllPrivilegesPromise = selectPrivilegesService.getAllPrivileges();

        $scope.appRoles = [];
        $scope.allPrivileges = {};
        $scope.selectedRole = null;
        $scope.editBtnDisabled = true;


        //wait all promises to resolve
        $q.all([tempAppRolesPromise, tempAllPrivilegesPromise]).then(function (payload) {
            //get all appRoles
            angular.forEach(payload[0].data, function (appRole) {
                var element = new Object;
                element.name = appRole;
                element.key = appRole;
                $scope.appRoles.push(element);
            });

            //set all privileges
            $scope.allPrivileges = payload[1].data;
        });

        //callback function when app role is selected
        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized) {
            $scope.selectedRole = selectedObject;
            $scope.editBtnDisabled = false;


            selectPrivilegesService.getRolePrivileges(selectedObject.key).then(function (payload) {
                var rolePrivileges = payload.data;
                //set authorized privileges
                for (var key in rolePrivileges) {
                    var rolePrivilege = {};
                    rolePrivilege.key = key;
                    rolePrivilege.name = rolePrivileges[key];
                    authorized.push(rolePrivilege);
                }

                //set not authorized groups.
                // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
                for (var key in $scope.allPrivileges) {
                    if (rolePrivileges[key] == undefined) {
                        var notAuthObject = {};
                        notAuthObject.key = key;
                        notAuthObject.name = $scope.allPrivileges[key];
                        notAuthorized.push(notAuthObject);
                    }
                }
            });
        };

        //callback function when groups are moved
        $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized, isClicked) {
            
        	var deferred = $q.defer();
        	var privileges = [];
            angular.forEach(authorized, function (element) {
                privileges.push(element.key);
            });
            selectPrivilegesService.addRolePrivileges(selectedObject.key, privileges).then(function() {
            	deferred.resolve();
            }, function(){
            	deferred.reject();
            });

            return deferred.promise;
        };


        $scope.newRole = function () {
            $scope.showModal(null);
        };

        $scope.editRole = function () {
            $scope.showModal($scope.selectedRole.key);

        };

        //dialog for edit or create new role
        $scope.showModal = function (value) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/admin/views/components/security.select-privileges.create-edit.dialog.html',
                controller: function ($scope, $modalInstance) {
                    $scope.inputValid = true;
                    $scope.roleName = value;
                    if (value == null) {
                        $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.createRole');
                    } else {
                        $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.applyChanges');
                    }

                    //watch the input to enable/disable ok button
                    $scope.$watch('roleName', function (newValue) {
                        if (newValue) {
                            $scope.inputValid = false;
                        } else {
                            $scope.inputValid = true;
                        }
                    });
                    $scope.ok = function () {
                        $scope.roleName = $scope.roleName.toUpperCase().replace(/\s+/g, "_");
                        if (!$scope.roleName.startsWith("ROLE_")) {
                            $scope.roleName = "ROLE_" + $scope.roleName;
                        }
                        $modalInstance.close($scope.roleName);
                    };
                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                },
                size: 'md'
            });

            //handle the result
            modalInstance.result.then(function (result) {
                //button ok
                if (value == null) {
                    //handle create new item
                    selectPrivilegesService.upsertRole(result).then(function () {
                        var element = new Object;
                        element.name = result;
                        element.key = result;
                        $scope.appRoles.push(element);
                    });
                } else {
                    //handle edit item
                    selectPrivilegesService.upsertRole(result, value).then(function () {
                        $scope.selectedRole.key = result;
                        $scope.selectedRole.name = result;
                    });

                }
            }, function (result) {
                //button cancel, nothing to do.
            });
        }
    }
]);
