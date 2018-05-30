'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.ApproverController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService', 'LookupService',
                function($scope, $stateParams, Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService, LookupService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        componentId: "approvers",
                        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        },
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.gridOptions = $scope.gridOptions || {};

                        LookupService.getApprovers(objectInfo).then(function(approvers) {
                            $scope.gridOptions.data = approvers;
                        });
                    };
                } ]);