'use strict';

angular.module('admin').controller('Admin.UserNameConfigController', [ '$scope', '$q', '$modal', 'Admin.ApplicationSettingsService', 'MessageService', function($scope, $q, $modal, ApplicationSettingsService, messageService) {

    var oldPropertyValue;
    $scope.configDataModel = {};

    ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
        $scope.nameProperty = response.data[ApplicationSettingsService.PROPERTIES.DISPLAY_USERNAME];
        oldPropertyValue = $scope.nameProperty;
        $scope.configDataModel = response.data;
    });

    $scope.applyChanges = function() {
        if (oldPropertyValue != $scope.nameProperty) {
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.DISPLAY_USERNAME] = $scope.nameProperty;
            ApplicationSettingsService.saveApplicationPropertyConfig($scope.configDataModel);
            var modalInstance = $modal.open({
                templateUrl: 'modules/admin/views/components/application-user-name.config.modal-info.client.view.html',
                controller: 'AdminUserInfoModalController',
                backdrop: false,
                size: 'sm'
            });

            modalInstance.result.then(function() {
            }, function() {
                messageService.succsessAction();
            });

            oldPropertyValue = $scope.nameProperty;
        }
    }
} ]);

angular.module('admin').controller('AdminUserInfoModalController', [ '$scope', '$modalInstance', '$modal', function($scope, $modalInstance, $modal) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);