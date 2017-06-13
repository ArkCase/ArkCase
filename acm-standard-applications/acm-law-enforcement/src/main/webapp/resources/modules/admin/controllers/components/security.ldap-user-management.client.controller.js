'use strict';

angular.module('admin').controller('Admin.LdapUserManagementController', ['$scope', 'Admin.FunctionalAccessControlService', 'Admin.LdapUserManagementService', '$q', '$modal',
    function ($scope, functionalAccessControlService, ldapUserManagementService, $q, $modal) {

        $scope.cloneUser = cloneUser;

        $scope.selectedUser = null;

        var tempUserGroupsPromise = functionalAccessControlService.getUserGroups();
        var tempUserPromise = ldapUserManagementService.queryUsers();

        $scope.appUsers = [];
        $scope.appGroups = [];
        $scope.currentAuthGroups = [];

        function cloneUser(user, selectedUser) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.addUser = true;
                    $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                    $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                    $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                    $scope.user = user;
                    $scope.selectedUser = selectedUser;
                    $scope.data = {
                        "acmUser": $scope.user,
                        "selectedUser": $scope.selectedUser
                    };
                    $scope.ok = function () {
                        $modalInstance.close($scope.data);
                    };
                }],
                size: 'sm'
            });

            modalInstance.result.then(function (user) {

                console.log(user);



            }, function () {
                // Cancel button was clicked
            });
        }

        //wait all promises to resolve
        $q.all([tempUserGroupsPromise, tempUserPromise]).then(function (payload) {

            //get all appRoles
            angular.forEach(payload[0].data.response.docs, function (appRole) {
                var element = new Object;
                element.name = appRole.name;
                element.key = appRole.name;
                element.members = appRole.member_id_ss;
                $scope.appGroups.push(element);
            });

            //get all app roles

            angular.forEach(payload[1].data.response.docs, function (appRole) {
                var element = new Object;
                element.name = appRole.name;
                element.key = appRole.object_id_s;
                element.directory = appRole.directory_name_s;
                $scope.appUsers.push(element);
            });

        });

        //callback function when app role is selected
        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized) {

            $scope.selectedUser = selectedObject;

            //set authorized groups
            _.forEach($scope.appGroups, function (group) {
                //we need to create wrapper, since appRolesUserGroups doesn't have name which directive expect to have

                _.forEach(group.members, function (groupMember) {
                    if (groupMember === selectedObject.key) {
                        var authObject = {};
                        authObject.key = group.name;
                        authObject.name = group.name;
                        authorized.push(authObject);
                        $scope.currentAuthGroups.push(authObject.key);
                    }

                });


            });

            //set not authorized groups.
            // Logic: iterate all user groups and if not already exists in selected app role user groups, add to the array
            _.forEach($scope.appGroups, function (group) {
                if ($scope.currentAuthGroups.indexOf(group.key) == -1) {
                    //we need to create wrapper to provide a name property
                    var notAuthorizedRole = {};
                    notAuthorizedRole.key = group.name;
                    notAuthorizedRole.name = group.name;
                    notAuthorized.push(notAuthorizedRole);
                }
            });

            //callback function when groups are moved
            $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized) {
                var toBeAdded = [];
                var toBeRemoved = [];
                var deferred = $q.defer();

                //get roles which needs to be added
                angular.forEach(authorized, function (role) {
                    if ($scope.currentAuthGroups.indexOf(role.key) == -1) {
                        toBeAdded.push(role.key);
                    }
                });
                //perform adding on server
                if (toBeAdded.length > 0) {
                    ldapUserManagementService.addGroupsToUser(selectedObject.key, toBeAdded).then(function (data) {
                        console.log(data);
                    });

                    $scope.currentAuthRoles = $scope.currentAuthRoles.concat(toBeAdded);
                    return deferred.promise;
                }

                //get roles which needs to be removed
                angular.forEach(notAuthorized, function (role) {
                    if ($scope.currentAuthGroups.indexOf(role.key) != -1) {
                        toBeRemoved.push(role.key);
                    }
                });
                if (toBeRemoved.length > 0) {
                    ldapUserManagementService.removeGroupsFromUser(selectedObject.key, toBeAdded).then(function (data) {
                        console.log(data);
                    });


                    //remove from $scope.currentAuthGroups
                    angular.forEach(toBeRemoved, function (element) {
                        $scope.currentAuthGroups.splice($scope.currentAuthGroups.indexOf(element), 1);
                    });

                    return deferred.promise;
                }
            };
        };

    }
]);