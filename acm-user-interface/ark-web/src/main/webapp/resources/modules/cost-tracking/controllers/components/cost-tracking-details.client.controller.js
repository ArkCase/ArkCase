'use strict';

angular.module('cost-tracking').controller('CostTracking.DetailsController', ['$scope', '$translate', '$stateParams'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $translate, $stateParams
        , Util, ConfigService, CostTrackingInfoService, MessageService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("cost-tracking", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(function (costsheetInfo) {
                $scope.costsheetInfo = costsheetInfo;
                return costsheetInfo;
            });
        }

        $scope.saveDetails = function() {
            var costsheetInfo = Util.omitNg($scope.costsheetInfo);
            CostTrackingInfoService.saveCostsheetInfo(costsheetInfo).then(
                function (costsheetInfo) {
                    MessageService.info($translate.instant("costTracking.comp.details.informSaved"));
                    return costsheetInfo;
                }
            )
        };

    }
]);