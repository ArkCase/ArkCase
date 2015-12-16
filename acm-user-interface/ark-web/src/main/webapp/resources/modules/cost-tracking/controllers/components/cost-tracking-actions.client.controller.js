'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state'
    , 'ConfigService', 'CostTracking.InfoService'
    , function ($scope, $state, ConfigService, CostTrackingInfoService) {

        ConfigService.getComponentConfig("cost-tracking", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('costsheet-updated', function (e, data) {
            if (CostTrackingInfoService.validateCostsheet(data)) {
                $scope.costsheetInfo = data;
            }
        });

        $scope.createNew = function () {
            $state.go("frevvo-new-costsheet", {
                name: "new-costsheet"
            });
        };

        $scope.edit = function (costsheetInfo) {
            $state.go("frevvo-edit-costsheet", {
                name: "edit-costsheet",
                arg: {
                    id: costsheetInfo.id
                }
            });
        };

    }
]);