'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, Util, ConfigService, CostTrackingInfoService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cost-tracking", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('object-updated', function (e, data) {
            if (CostTrackingInfoService.validateCostsheet(data)) {
                $scope.costsheetInfo = data;
            }
        });
        //var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        //if (Util.goodPositive(currentObjectId, false)) {
        //    CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(function (costsheetInfo) {
        //        $scope.costsheetInfo = costsheetInfo;
        //        return costsheetInfo;
        //    });
        //}

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