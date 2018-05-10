'use strict';

angular.module('cost-tracking').controller(
        'CostTrackingController',
        [ '$scope', '$stateParams', '$state', '$translate', 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'ObjectService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, $state, $translate, Util, ConfigService, CostTrackingInfoService, ObjectService, HelperObjectBrowserService) {

                    new HelperObjectBrowserService.Content({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        resetObjectInfo: CostTrackingInfoService.resetCostsheetInfo,
                        getObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        updateObjectInfo: CostTrackingInfoService.saveCostsheetInfo,
                        getObjectTypeFromInfo: function(complaintInfo) {
                            return ObjectService.ObjectTypes.COSTSHEET;
                        }
                    });

                } ]);