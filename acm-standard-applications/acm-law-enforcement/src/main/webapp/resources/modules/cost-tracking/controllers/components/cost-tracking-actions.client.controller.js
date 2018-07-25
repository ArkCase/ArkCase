'use strict';

angular.module('cost-tracking').controller('CostTracking.ActionsController',
        [ '$scope', '$state', '$stateParams', 'UtilService', 'ConfigService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService', function($scope, $state, $stateParams, Util, ConfigService, CostTrackingInfoService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cost-tracking",
                componentId: "actions",
                retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                validateObjectInfo: CostTrackingInfoService.validateCostsheet,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.editCostsheetParams = {
                    id: objectInfo.id
                };
            };

            $scope.refresh = function() {
                $scope.$emit('report-object-refreshed', $stateParams.id);
            };

            $scope.isVisible = function() {
                return !Util.isEmpty($scope.objectInfo) && $scope.objectInfo.status === 'DRAFT';
            };

        } ]);