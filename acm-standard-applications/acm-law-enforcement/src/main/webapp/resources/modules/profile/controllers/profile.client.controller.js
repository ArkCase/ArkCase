'use strict';

angular.module('profile').controller('ProfileController', ['$scope', 'ConfigService'
    , 'Admin.OrganizationalHierarchyService', '$modal', 'Authentication',
    function ($scope, ConfigService, OrganizationalHierarchyService, $modal, Authentication
    ) {

        //TODO: Remove following phased out code block. Leave it just in case some extension are still using
        //the 'req-component-config' and 'component-config' events to get config.
        $scope._phaseout_config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope._phaseout_config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        //end of block

        ConfigService.getModuleConfig("profile").then(function (moduleConfig) {
            $scope.config = moduleConfig;
            return moduleConfig;
        });

        Authentication.queryUserInfo().then(function (userInfo) {
            var directoryName = userInfo.directoryName;
            OrganizationalHierarchyService.isEnabledEditingLdapUsers(directoryName).then(function (enableEditingLdapUsers) {
                $scope.exposeChangePassword = enableEditingLdapUsers;
            });
        });

        $scope.openPasswordDialog = function () {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changePassword.client.view.html',
                controller: 'ChangePasswordModalController',
                backdrop: false,
                size: 'sm'
            });
        };
        $scope.openChangeLdapPasswordDialog = function () {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changeLdapPassword.client.view.html',
                controller: 'ChangeLdapPasswordModalController',
                size: 'sm'
            });
        }
    }
]);
angular.module('profile').run(function (editableOptions, editableThemes) {
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';
});

angular.module('profile').controller('ChangePasswordModalController', ['$scope', '$modalInstance', 'Profile.ChangePasswordService', '$modal',
    function ($scope, $modalInstance, ChangePasswordService, $modal) {
        $scope.close = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
            $scope.newPassword = '';
            $scope.newPasswordAgain = '';
        };

        function openModal(params) {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-password-info.client.view.html',
                controller: ['$scope', 'params', function ($scope, params) {
                    $scope.message = params.message;
                }],
                resolve: {
                    params: params
                },
                backdrop: false,
                size: 'sm'
            });
        }

        $scope.changePassword = function () {
            if (!this.newPassword) {
                openModal({"message": "profile.modal.emptyPassword"});
            }
            else if (!this.newPasswordAgain) {
                openModal({"message": "profile.modal.comfirmation"});
            }
            else if (this.newPassword !== this.newPasswordAgain) {
                openModal({"message": "profile.modal.differentPasswords"});
                this.newPassword = '';
                this.newPasswordAgain = '';
            }
            else {
                var data = {"outlookPassword": this.newPassword};
                ChangePasswordService.changePassword(data);
                $modalInstance.close('done');
                this.newPassword = '';
                this.newPasswordAgain = '';
            }
        };
    }
]);

angular.module('profile').controller('ChangeLdapPasswordModalController', ['$scope', '$modalInstance'
    , '$modal', '$translate', 'Profile.ChangePasswordService', 'Authentication', 'MessageService'
    , function ($scope, $modalInstance, $modal, $translate, ChangePasswordService, Authentication, MessageService) {

        Authentication.queryUserInfo().then(function (userInfo) {
            $scope.userInfo = userInfo;
        });

        $scope.isPasswordError = false;
        $scope.authError = null;
        $scope.loading = false;

        $scope.changePassword = function () {
            if ($scope.newPassword !== $scope.confirmNewPassword) {
                $scope.confirmNewPassword = '';
                $scope.isPasswordError = true;
            }
            else {
                var data = {
                    currentPassword: $scope.currentPassword,
                    newPassword: $scope.newPassword,
                    userInfo: $scope.userInfo
                };
                $scope.loading = true;
                ChangePasswordService.changeLdapPassword(data).then(function () {
                    $modalInstance.close('done');
                    $scope.loading = false;
                    MessageService.info($translate.instant("profile.modal.success"));
                }, function (errorData) {
                    $scope.loading = false;
                    var message = errorData.data.authError;
                    if (message) {
                        $scope.authError = message;
                        $scope.currentPassword = '';
                    }
                    else {
                        $modalInstance.close('done');
                        if (errorData.data.message) {
                            MessageService.error(errorData.data.message);
                        } else {
                            MessageService.errorAction();
                        }
                    }
                });
            }
        };
    }
]);