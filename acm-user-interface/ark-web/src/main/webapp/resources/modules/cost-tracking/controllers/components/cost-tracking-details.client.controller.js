'use strict';

angular.module('cost-tracking').controller('CostTracking.DetailsController', ['$scope', '$translate', 'UtilService', 'CallCostTrackingService', 'MessageService',
    function ($scope, $translate, Util, CallCostTrackingService, MessageService) {
        $scope.$emit('req-component-config', 'details');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('details' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('costsheet-updated', function (e, data) {
            $scope.costsheetInfo = data;
        });

        $scope.saveDetails = function() {
            var costsheetInfo = Util.omitNg($scope.costsheetInfo);
            CallCostTrackingService.saveCostsheetInfo(costsheetInfo).then(
                function (costsheetInfo) {
                    MessageService.info($translate.instant("costTracking.comp.details.informSaved"));
                    return costsheetInfo;
                }
            )
        };

    }
]);