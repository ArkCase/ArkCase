'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.InfoController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, Util, ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, CostTrackingInfoService, HelperObjectBrowserService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        componentId: "info",
                        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                    };
                } ]);