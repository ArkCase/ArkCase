'use strict';

angular.module('cost-tracking').controller('CostTracking.DetailsController', ['$scope', '$translate'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'MessageService'
    , function ($scope, $translate, Util, ConfigService, CostTrackingInfoService, MessageService) {

        ConfigService.getComponentConfig("cost-tracking", "details").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
        });

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