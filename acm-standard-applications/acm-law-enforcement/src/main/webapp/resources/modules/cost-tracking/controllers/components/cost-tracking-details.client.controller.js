'use strict';

angular.module('cost-tracking').controller('CostTracking.DetailsController', ['$scope', '$translate', '$stateParams'
    , 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $translate, $stateParams
        , Util, ConfigService, CostTrackingInfoService, MessageService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "details"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
        });

        $scope.options = {
            focus: true,
            dialogsInBody:true
            //,height: 120
        };

        $scope.saveDetails = function() {
            var costsheetInfo = Util.omitNg($scope.objectInfo);
            CostTrackingInfoService.saveCostsheetInfo(costsheetInfo).then(
                function (costsheetInfo) {
                    MessageService.info($translate.instant("costTracking.comp.details.informSaved"));
                    return costsheetInfo;
                }
            )
        };

    }
]);