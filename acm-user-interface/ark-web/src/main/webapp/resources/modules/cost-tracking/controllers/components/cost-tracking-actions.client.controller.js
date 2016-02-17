'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController', ['$scope', '$state', '$stateParams'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, Util, ConfigService, CostTrackingInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "actions"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
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