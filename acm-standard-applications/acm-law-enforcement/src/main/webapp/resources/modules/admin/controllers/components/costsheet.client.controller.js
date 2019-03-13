'use strict';

angular.module('admin').controller('Admin.CostsheetController',
        [ '$scope', 'Admin.CostsheetConfigurationService', 'MessageService', 'UtilService', function($scope, CostsheetConfigurationService, MessageService, Util) {

            $scope.costsheetProperties = {
                "cost.plugin.useApprovalWorkflow": "true"
            };

            CostsheetConfigurationService.getProperties().then(function(response) {
                if (!Util.isEmpty(response.data)) {
                    $scope.costsheetProperties = response.data;
                }
            });

            $scope.saveCostsheetProperties = function() {
                CostsheetConfigurationService.saveProperties($scope.costsheetProperties).then(function(response) {
                    MessageService.succsessAction();
                }, function(reaspon) {
                    MessageService.errorAction();
                });
            };

        } ]);