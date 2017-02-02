'use strict';

angular.module('profile').controller('ProfileController', ['$scope', 'ConfigService'
    , 'Admin.OrganizationalHierarchyService', '$modal',
    function ($scope, ConfigService, OrganizationalHierarchyService, $modal) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }

        $scope.exposeChangePassword = false;

        OrganizationalHierarchyService.isEnabledEditingLdapUsers().then(function (enableEditingLdapUsers) {
            $scope.exposeChangePassword = enableEditingLdapUsers;
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
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changePassword.client.view.html',
                controller: 'ChangeLdapPasswordModalController',
                backdrop: false,
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
                openModal({"message":"profile.modal.emptyPassword"});
            }
            else if (!this.newPasswordAgain) {
                openModal({"message":"profile.modal.comfirmation"});
            }
            else if (this.newPassword !== this.newPasswordAgain) {
                openModal({"message":"profile.modal.differentPasswords"});
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

angular.module('profile').controller('ChangeLdapPasswordModalController', ['$scope', '$modalInstance',
    'Profile.ChangePasswordService', '$modal',
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
                openModal({"message":"profile.modal.emptyPassword"});
            }
            else if (!this.newPasswordAgain) {
                openModal({"message":"profile.modal.comfirmation"});
            }
            else if (this.newPassword !== this.newPasswordAgain) {
                openModal({"message":"profile.modal.differentPasswords"});
                this.newPassword = '';
                this.newPasswordAgain = '';
            }
            else {
                var data = {"password": this.newPassword};
                ChangePasswordService.changeLdapPassword(data);
                $modalInstance.close('done');
                this.newPassword = '';
                this.newPasswordAgain = '';
            }
        };
    }
]);