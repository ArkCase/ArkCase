'use strict';

angular.module('admin').controller('Admin.SelectPrivilegesController', [ '$scope', 'Admin.SelectPrivilegesService', '$q', '$modal', '$translate', '$log', 'UtilService', 'MessageService', 'RegexValidationService', 'ObjectService', function($scope, SelectPrivilegesService, $q, $modal, $translate, $log, Util, MessageService, RegexValidationService, ObjectService) {
    var tempAppRolesPromise = SelectPrivilegesService.getAppRoles();

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

    var currentAuthGroups = [];
    $scope.lastSelectedRole = null;
    // Loaded data after the initialization
    var initRolesData = {
        "chooseObject": []
    };
    // Data to be displayed
    $scope.rolesData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.filterData = {
        "objectsFilter": $scope.chooseAppRoleFilter,
        "unauthorizedFilter": $scope.appRoleUnauthorizedFilter,
        "authorizedFilter": $scope.appRoleAuthorizedFilter
    };
    $scope.scrollLoadData = {
        "loadUnauthorizedScroll": $scope.unauthorizedScroll,
        "loadAuthorizedScroll": $scope.authorizedScroll
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

        $scope.onObjSelect($scope.rolesData.chooseObject[0], $scope.rolesData.selectedAuthorized, $scope.rolesData.selectedNotAuthorized);
    });

    function fillList(listToFill, data) {
        _.forEach(data, function(item) {
            var element = {};
            element.key = item.key;
            element.name = item.value;
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
        data.isAuthorized = false;
        data.role = $scope.lastSelectedRole;
        SelectPrivilegesService.getRolePrivilegesByName(data).then(function(response) {
            $scope.rolesData.selectedNotAuthorized = [];
            $scope.fillList($scope.rolesData.selectedNotAuthorized, response.data);
        });
    }

    function appRoleAuthorizedFilter(data) {
        data.isAuthorized = true;
        data.role = $scope.lastSelectedRole;
        SelectPrivilegesService.getRolePrivilegesByName(data).then(function(response) {
            $scope.rolesData.selectedAuthorized = [];
            $scope.fillList($scope.rolesData.selectedAuthorized, response.data);
        });
    }

    function retrieveDataScroll(data, methodName, panelName) {
        SelectPrivilegesService[methodName](data).then(function(response) {
            $scope.fillList($scope.rolesData[panelName], response.data);

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

    function authorizedScroll() {
        var data = {};
        data.role = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getRolePrivilegesByName", "selectedAuthorized");
    }

    function unauthorizedScroll() {
        var data = {};
        data.role = $scope.lastSelectedRole;
        data.start = $scope.rolesData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getRolePrivilegesByName", "selectedNotAuthorized");
    }

    //callback function when app role is selected
    function onObjSelect(selectedObject) {
        $scope.rolesData.selectedAuthorized = [];
        $scope.rolesData.selectedNotAuthorized = [];
        if (!_.isEmpty($scope.rolesData.chooseObject)) {
            $scope.lastSelectedRole = selectedObject;
            var data = {};
            data.role = selectedObject;
            data.isAuthorized = true;
            var unAuthorizedGroupsForUserPromise = SelectPrivilegesService.getRolePrivilegesByName(data);
            data.isAuthorized = false;
            var authorizedGroupsForUserPromise = SelectPrivilegesService.getRolePrivilegesByName(data);

            //wait all promises to resolve
            $q.all([ unAuthorizedGroupsForUserPromise, authorizedGroupsForUserPromise ]).then(function(payload) {
                currentAuthGroups = [];
                _.forEach(payload[0].data, function(item) {
                    var element = {};
                    element.key = item.key;
                    element.name = item.value;
                    $scope.rolesData.selectedAuthorized.push(element);
                    currentAuthGroups.push(element.key);
                });

                fillList($scope.rolesData.selectedNotAuthorized, payload[1].data);
            });
        }
    }

    //callback function when groups are moved
    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized, isClicked) {
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

            SelectPrivilegesService.addPrivilegeToApplicationRole(selectedObject.key, toBeAdded).then(function(data) {
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

            SelectPrivilegesService.removePrivilegeFromApplicationRole(selectedObject.key, toBeRemoved).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }
    };

    $scope.newRole = function () {
        $scope.showModal(null);
    };

    $scope.editRole = function () {
        $scope.showModal($scope.lastSelectedRole.key);
    };


    //dialog for edit or create new role
    $scope.showModal = function(value) {
        var modalInstance = $modal.open({
            animation: true,
            templateUrl: 'modules/admin/views/components/security.select-privileges.create-edit.dialog.html',
            controller: function($scope, $modalInstance) {
                $scope.validateInput = function(value) {
                    var validatedObject = RegexValidationService.validateInput(value, ObjectService.RegexTypes.RULE_REGEX);
                        $scope.roleName = validatedObject.inputValue;
                        $scope.showRegexError = validatedObject.showRegexError;
                };
                if (value == null) {
                    $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.createRole');
                } else {
                    $scope.saveBtnText = $translate.instant('admin.security.selectPrivileges.applyChanges');
                }

                //watch the input to enable/disable ok button
                $scope.$watch('roleName', function(newValue) {
                    if (newValue && !$scope.showRegexError) {
                        $scope.inputValid = false;
                    } else {
                        $scope.inputValid = true;
                    }
                });
                $scope.ok = function() {
                    $scope.roleName = $scope.roleName.toUpperCase().replace(/\s+/g, "_");
                    if (!_.startsWith($scope.roleName, "ROLE_")) {
                        $scope.roleName = "ROLE_" + $scope.roleName;
                    }
                    $modalInstance.close($scope.roleName);
                };
                $scope.cancel = function() {
                    $modalInstance.dismiss('cancel');
                };
            },
            size: 'md',
            backdrop: 'static'
        });

        //handle the result
        modalInstance.result.then(function(result) {
            //button ok
            if (value == null) {
                //handle create new item
                SelectPrivilegesService.upsertRole(result).then(function() {
                    var element = new Object;
                    element.name = result;
                    element.key = result;
                    $scope.rolesData.chooseObject.push(element);
                });
            } else {
                //handle edit item
                SelectPrivilegesService.upsertRole(result, value).then(function() {
                    _.forEach($scope.rolesData.chooseObject, function(data) {
                        if (data.key === $scope.lastSelectedRole.key) {
                            data.key = result;
                            data.name = result;
                            $scope.lastSelectedRole = data;
                            return false;
                        }
                    });
                    MessageService.succsessAction();
                });

            }
        }, function(result) {
            //button cancel, nothing to do.
        });
    }
} ]);
