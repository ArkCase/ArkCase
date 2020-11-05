'use strict';

angular.module('admin').controller('Admin.OcrManagementController',
    [ '$scope', 'Admin.OcrManagementService', 'ConfigService', 'MessageService', 'UtilService', function($scope, OcrManagementService, ConfigService, MessageService, Util) {

        $scope.ocrConfigDataModel = {
        };

        OcrManagementService.getProperties().then(function(response) {
            if (!Util.isEmpty(response.data)) {
                $scope.ocrConfigDataModel = response.data;
            }
        });

        $scope.saveChanges = function() {
            OcrManagementService.saveProperties($scope.ocrConfigDataModel).then(function(response) {
                MessageService.succsessAction();
            }, function(err) {
                MessageService.error(err.data);
            });
        };

    } ]);