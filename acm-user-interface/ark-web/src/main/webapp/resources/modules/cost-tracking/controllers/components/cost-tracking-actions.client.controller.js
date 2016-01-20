'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state', '$stateParams'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, Util, ConfigService, CostTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cost-tracking", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('object-updated', function (e, data) {
            if (CostTrackingInfoService.validateCostsheet(data)) {
                $scope.costsheetInfo = data;
            }
        });

        $scope.$on('object-refreshed', function (e, data) {
            if (CostTrackingInfoService.validateCostsheet(data)) {
                $scope.costsheetInfo = data;
            }
        });

        $scope.createNew = function () {
            $state.go("frevvo", {
                name: "new-costsheet"
            });
        };

        $scope.edit = function (costsheetInfo) {
            $state.go("frevvo", {
                name: "edit-costsheet",
                arg: {
                    id: costsheetInfo.id
                }
            });
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

    }
]);