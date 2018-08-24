'use strict';

angular.module('admin').controller('Admin.FormsTypeConfigController', [ '$scope', '$q', '$modal', 'Admin.ApplicationFormsTypeConfigService', 'MessageService', function($scope, $q, $modal, ApplicationFormsTypeConfigService, messageService) {
    var oldPropertyValue;
    ApplicationFormsTypeConfigService.getProperty(ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE).then(function(response) {
        $scope.nameProperty = response.data[ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE];
        oldPropertyValue = $scope.nameProperty;
    });

    $scope.applyChanges = function() {
        if (oldPropertyValue != $scope.nameProperty) {
            ApplicationFormsTypeConfigService.setProperty(ApplicationFormsTypeConfigService.PROPERTIES.FORMS_TYPE, $scope.nameProperty);
            var modalInstance = $modal.open({
                templateUrl: 'modules/admin/views/components/application-user-name.config.modal-info.client.view.html',
                controller: 'AdminFormsTypeModalController',
                backdrop: false,
                size: 'sm'
            });

            modalInstance.result.then(function() {
            }, function() {
                messageService.succsessAction();
            });

            oldPropertyValue = $scope.nameProperty;
        }
    };
} ]);

angular.module('admin').controller('AdminFormsTypeModalController', [ '$scope', '$modalInstance', '$modal', function($scope, $modalInstance, $modal) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);