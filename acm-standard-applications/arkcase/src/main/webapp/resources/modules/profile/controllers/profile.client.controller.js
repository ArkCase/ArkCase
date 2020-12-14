'use strict';

angular.module('profile').controller('ProfileController',
        [ '$scope', 'ConfigService', 'Admin.OrganizationalHierarchyService', '$modal', 'Authentication', 'Profile.MfaService', 'Profile.ChangePasswordService', function($scope, ConfigService, OrganizationalHierarchyService, $modal, Authentication, ProfileMfaService, ChangePasswordService) {

            //TODO: Remove following phased out code block. Leave it just in case some extension are still using
            //the 'req-component-config' and 'component-config' events to get config.
            $scope._phaseout_config = ConfigService.getModule({
                moduleId: 'profile'
            });
            $scope.$on('req-component-config', onConfigRequest);
            function onConfigRequest(e, componentId) {
                $scope._phaseout_config.$promise.then(function(config) {
                    var componentConfig = _.find(config.components, {
                        id: componentId
                    });
                    $scope.$broadcast('component-config', componentId, componentConfig);
                });
            }
            //end of block

            ConfigService.getModuleConfig("profile").then(function(moduleConfig) {
                $scope.config = moduleConfig;
                return moduleConfig;
            });

            ProfileMfaService.getAuthProfile().then(function(authProfile) {
                $scope.authProfile = authProfile;
            });

            $scope.openPasswordDialog = function() {
                $modal.open({
                    templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changePassword.client.view.html',
                    controller: 'ChangePasswordModalController',
                    backdrop: false,
                    size: 'sm'
                });
            };
            $scope.openChangeLdapPasswordDialog = function() {
                $modal.open({
                    templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changeLdapPassword.client.view.html',
                    controller: 'ChangeLdapPasswordModalController',
                    size: 'sm'
                });
            };

            Authentication.queryUserInfo().then(function(userInfo) {
                ChangePasswordService.canManageLdapPassword(userInfo.directoryName).then(function(res) {
                    $scope.manageLdapPasswordEnabled = res.data.managePasswordEnabled;
                });
            });
        } ]);
angular.module('profile').run(function(editableOptions, editableThemes) {
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';
});

angular.module('profile').controller('ChangePasswordModalController', [ '$scope', '$modalInstance', 'Profile.ChangePasswordService', '$modal', function($scope, $modalInstance, ChangePasswordService, $modal) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
        $scope.newPassword = '';
        $scope.newPasswordAgain = '';
    };

    function openModal(params) {
        $modal.open({
            templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-password-info.client.view.html',
            controller: [ '$scope', 'params', function($scope, params) {
                $scope.message = params.message;
            } ],
            resolve: {
                params: params
            },
            backdrop: false,
            size: 'sm'
        });
    }

    $scope.changePassword = function() {
        if (!this.newPassword) {
            openModal({
                "message": "profile.modal.emptyPassword"
            });
        } else if (!this.newPasswordAgain) {
            openModal({
                "message": "profile.modal.comfirmation"
            });
        } else if (this.newPassword !== this.newPasswordAgain) {
            openModal({
                "message": "profile.modal.differentPasswords"
            });
            this.newPassword = '';
            this.newPasswordAgain = '';
        } else {
            var data = {
                "outlookPassword": this.newPassword
            };
            ChangePasswordService.changePassword(data);
            $modalInstance.close('done');
            this.newPassword = '';
            this.newPasswordAgain = '';
        }
    };
} ]);

angular.module('profile').controller('ChangeLdapPasswordModalController',
        [ '$scope', '$modalInstance', '$modal', '$translate', 'UtilService', 'Profile.ChangePasswordService', 'Authentication', 'MessageService', function($scope, $modalInstance, $modal, $translate, Util, ChangePasswordService, Authentication, MessageService) {

            Authentication.queryUserInfo().then(function(userInfo) {
                $scope.userInfo = userInfo;
            });

            $scope.$bus.subscribe('ldap-change-password-clear-errors', function() {
                $scope.authError = '';
                $scope.errorMessage = '';
            });

            $scope.authError = false;
            $scope.loading = false;
            $scope.passwordErrorMessages = {
                notSamePasswordsMessage: ''
            };

            $scope.changePassword = function() {

                var data = {
                    currentPassword: $scope.currentPassword,
                    password: $scope.newPassword,
                    userId: $scope.userInfo.userId,
                    directory: $scope.userInfo.directoryName
                };
                $scope.loading = true;
                ChangePasswordService.changeLdapPassword(data).then(function() {
                    $modalInstance.close('done');
                    $scope.loading = false;
                    MessageService.info($translate.instant("profile.modal.success"));
                }, function(errorData) {
                    $scope.loading = false;
                    var authError = errorData.data.authError; //auth error
                    var passwordError = errorData.data.passError;
                    var message = errorData.data.message;
                    if (authError) {
                        $scope.authError = authError;
                        $scope.currentPassword = '';
                    } else if (passwordError) {
                        $scope.authError = false;
                        $scope.errorMessage = passwordError;
                    } else if (message) {
                        $modalInstance.close('done');
                        MessageService.error(message);
                    } else {
                        $modalInstance.close('done');
                        MessageService.errorAction();
                    }
                });

            };
        } ]);