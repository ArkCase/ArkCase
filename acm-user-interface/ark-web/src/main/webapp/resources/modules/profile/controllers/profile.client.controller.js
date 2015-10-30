'use strict';

angular.module('profile').controller('ProfileController', ['$scope', 'ConfigService', 'Profile.ChangePasswordService', '$modal',
    function ($scope, ConfigService, ChangePasswordService, $modal) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.openPasswordDialog = function () {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-changePassword.client.view.html',
                controller: 'ChangePasswordModalController',
                backdrop: false,
                size: 'sm'
            });
        };
    }
]);
angular.module('profile').run(function (editableOptions, editableThemes) {
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';
});

angular.module('profile').controller('ChangePasswordModalController', function ($scope, $modalInstance, $modal,ChangePasswordService) {
    $scope.close = function () {
        $modalInstance.dismiss('cancel');
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
        $scope.newPassword = '';
        $scope.newPasswordAgain = '';
    };
    $scope.changePassword = function () {
        if (!this.newPassword) {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-emptyPassword.client.view.html',
                controller: 'ChangePasswordModalController',
                backdrop: false,
                size: 'sm'
            });
        }
        else if (!this.newPasswordAgain) {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-emptyPasswordAgain.client.view.html',
                controller: 'ChangePasswordModalController',
                backdrop: false,
                size: 'sm'
            });
        }
        else if (this.newPassword !== this.newPasswordAgain) {
            $modal.open({
                templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-differentPasswords.client.view.html',
                controller: 'ChangePasswordModalController',
                backdrop: false,
                size: 'sm'
            });
            this.newPassword = '';
            this.newPasswordAgain = '';
        }
        else {
            var data = '{"outlookPassword":' + '"' + this.newPassword + '"}';
            ChangePasswordService.changePassword(data);
            $modalInstance.close('done');
            this.newPassword = '';
            this.newPasswordAgain = '';
        }
    };
});