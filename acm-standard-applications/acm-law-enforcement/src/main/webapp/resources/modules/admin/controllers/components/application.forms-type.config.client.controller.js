'use strict';

angular.module('admin').controller('Admin.FormsTypeConfigController', [ '$scope', '$q', '$modal', 'Admin.ApplicationSettingsService', 'MessageService', function($scope, $q, $modal, ApplicationSettingsService, messageService) {
    var oldPropertyValue;
    ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.FORMS_TYPE).then(function(response) {
        $scope.nameProperty = response.data[ApplicationSettingsService.PROPERTIES.FORMS_TYPE];
        oldPropertyValue = $scope.nameProperty;
    });

    $scope.applyChanges = function() {
        if (oldPropertyValue != $scope.nameProperty) {
            ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.FORMS_TYPE, $scope.nameProperty);
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