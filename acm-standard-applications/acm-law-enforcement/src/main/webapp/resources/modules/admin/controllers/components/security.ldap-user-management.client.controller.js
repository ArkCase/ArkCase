'use strict';

angular.module('admin').controller('Admin.LdapUserManagementController', ['$scope', 'Admin.FunctionalAccessControlService', 'Admin.LdapUserManagementService', '$q', '$modal', 'LookupService', 'MessageService',
    function ($scope, functionalAccessControlService, ldapUserManagementService, $q, $modal, LookupService, messageService) {

        $scope.cloneUser = cloneUser;
        $scope.selectedUser = null;
        $scope.onObjSelect = onObjSelect;
        $scope.onAuthRoleSelected = onAuthRoleSelected;

        $scope.appUsers = [];
        $scope.appGroups = [];

        LookupService.getUsers().then(function (data) {
            _.forEach(data, function (user) {
                var element = new Object;
                element.name = user.name;
                element.key = user.object_id_s;
                element.directory = user.directory_name_s;
                $scope.appUsers.push(element);
            });
        });

        //callback function when user is selected
        function onObjSelect(selectedObject, authorized, notAuthorized) {
            $scope.currentAuthGroups = [];
            $scope.selectedUser = selectedObject;

            var ldapGroupsPromise = ldapUserManagementService.queryGroupsByDirectory(selectedObject.directory);
            var adHocGroupsPromise = ldapUserManagementService.queryAdhocGroups();

            $q.all([ldapGroupsPromise, adHocGroupsPromise]).then(function (result) {
                // merge LDAP and Ad-hoc groups into a single structure
                var groups = _.union(result[0].data.response.docs, result[1].data.response.docs);

                _.forEach(groups, function (group) {
                    _.forEach(group.member_id_ss, function (groupMember) {
                        if (groupMember === selectedObject.key) {
                            var authObject = {};
                            authObject.key = group.name;
                            authObject.name = group.name;
                            authorized.push(authObject);
                            $scope.currentAuthGroups.push(authObject.key);
                        }
                    });
                    if ($scope.currentAuthGroups.indexOf(group.name) == -1) {
                        //we need to create wrapper to provide a name property
                        var notAuthorizedRole = {};
                        notAuthorizedRole.key = group.name;
                        notAuthorizedRole.name = group.name;
                        notAuthorized.push(notAuthorizedRole);
                    }
                });
            });
        }

        //callback function when groups are moved
        function onAuthRoleSelected(selectedObject, authorized, notAuthorized) {
            var toBeAdded = [];
            var toBeRemoved = [];
            var deferred = $q.defer();

            //get roles which needs to be added
            _.forEach(authorized, function (group) {
                if ($scope.currentAuthGroups.indexOf(group.key) == -1) {
                    toBeAdded.push(group.key);
                }
            });
            _.forEach(notAuthorized, function (group) {
                if ($scope.currentAuthGroups.indexOf(group.key) != -1) {
                    toBeRemoved.push(group.key);
                }
            });
            //perform adding on server
            if (toBeAdded.length > 0) {
                $scope.currentAuthGroups = $scope.currentAuthGroups.concat(toBeAdded);

                ldapUserManagementService.addGroupsToUser(selectedObject.key, toBeAdded, selectedObject.directory).then(function (data) {
                    messageService.succsessAction();
                }, function () {
                    //error adding group
                    messageService.errorAction();
                });
                return deferred.promise;
            }

            if (toBeRemoved.length > 0) {


                _.forEach(toBeRemoved, function (element) {
                    $scope.currentAuthGroups.splice($scope.currentAuthGroups.indexOf(element), 1);
                });

                ldapUserManagementService.removeGroupsFromUser(selectedObject.key, toBeRemoved, selectedObject.directory).then(function (data) {
                    messageService.succsessAction();
                }, function () {
                    //error adding group
                    messageService.errorAction();
                });
                return deferred.promise;
            }
        };

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
                ldapUserManagementService.cloneUser(user).then(function (response) {
                    // add the new user to the list
                    var element = new Object;
                    element.name = response.data.fullName;
                    element.key = response.data.userId;
                    element.directory = response.data.userDirectoryName;
                    $scope.appUsers.push(element);
                    messageService.succsessAction();
                }, function () {
                    //error adding group
                    messageService.errorAction();
                });
            }, function () {
                // Cancel button was clicked
            });
        }
    }
]);
