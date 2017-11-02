'use strict';

angular.module('admin').controller('Admin.LdapUserManagementController', ['$scope', '$q', '$modal', '$timeout'
    , 'Admin.LdapUserManagementService', 'LookupService', 'MessageService'
    , function ($scope, $q, $modal, $timeout, LdapUserManagementService, LookupService, MessageService) {

        $scope.cloneUser = cloneUser;
        $scope.onObjSelect = onObjSelect;
        $scope.onAuthRoleSelected = onAuthRoleSelected;

        $scope.appUsers = [];
        $scope.appGroups = [];

        LookupService.getUsers().then(function (data) {
            _.forEach(data, function (user) {
                var element = {};
                element.name = user.name;
                element.key = user.object_id_s;
                element.directory = user.directory_name_s;
                $scope.appUsers.push(element);
            });
        });

        var selectedUser;
        var currentAuthGroups;

        //callback function when user is selected
        function onObjSelect(selectedObject, authorized, notAuthorized) {
            selectedUser = selectedObject;
            currentAuthGroups = [];

            var ldapGroupsPromise = LdapUserManagementService.queryGroupsByDirectory(selectedObject.directory);
            var adHocGroupsPromise = LdapUserManagementService.queryAdhocGroups();

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
                            currentAuthGroups.push(authObject.key);
                        }
                    });
                    if (currentAuthGroups.indexOf(group.name) === -1) {
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
                if (currentAuthGroups.indexOf(group.key) === -1) {
                    toBeAdded.push(group.key);
                }
            });
            _.forEach(notAuthorized, function (group) {
                if (currentAuthGroups.indexOf(group.key) !== -1) {
                    toBeRemoved.push(group.key);
                }
            });
            //perform adding on server
            if (toBeAdded.length > 0) {
                currentAuthGroups = currentAuthGroups.concat(toBeAdded);

                LdapUserManagementService.addGroupsToUser(selectedObject.key, toBeAdded, selectedObject.directory).then(function (data) {
                    MessageService.succsessAction();
                }, function () {
                    //error adding group
                    MessageService.errorAction();
                });
                return deferred.promise;
            }

            if (toBeRemoved.length > 0) {
                _.forEach(toBeRemoved, function (element) {
                    currentAuthGroups.splice(currentAuthGroups.indexOf(element), 1);
                });

                LdapUserManagementService.removeGroupsFromUser(selectedObject.key, toBeRemoved, selectedObject.directory).then(function (data) {
                    MessageService.succsessAction();
                }, function () {
                    //error adding group
                    MessageService.errorAction();
                });
                return deferred.promise;
            }
        }

        function cloneUser() {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                controller: ['$scope', '$modalInstance', function ($scope, $modalInstance) {
                    $scope.addUser = true;
                    $scope.cloneUser = true;
                    $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                    $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                    $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                    $scope.user = {};
                    $scope.data = {
                        "user": $scope.user,
                        "selectedUser": selectedUser
                    };
                    $scope.ok = function () {
                        $modalInstance.close($scope.data);
                    };
                }],
                size: 'sm'
            });

            modalInstance.result.then(function (data) {
                LdapUserManagementService.cloneUser(data).then(function (response) {
                    // add the new user to the list
                    var element = {};
                    element.name = response.data.fullName;
                    element.key = response.data.userId;
                    element.directory = response.data.userDirectoryName;
                    $scope.appUsers.push(element);
                    MessageService.succsessAction();
                }, function () {
                    //error adding user
                    MessageService.errorAction();
                });
            }, function () {
                // Cancel button was clicked
            });
        }

        $scope.deleteUser = function () {
            LdapUserManagementService.deleteUser(selectedUser).then(function () {
                $scope.appUsers = _.reject($scope.appUsers, function (element) {
                    return element.key === selectedUser.key;
                });
                MessageService.succsessAction();
            }, function () {
                MessageService.errorAction();
            });
        }
    }
]);
