'use strict';

angular.module('cost-tracking').controller('CostTracking.PersonController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, Util, ConfigService, HelperUiGridService, CostTrackingInfoService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cost-tracking",
                componentId: "person",
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
            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function(config) {
                $scope.config = config;
                //first the filter is set, and after that everything else,
                //so that the data loads with the new filter applied
                gridHelper.setUserNameFilterToConfig(promiseUsers).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();

                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = [ $scope.objectInfo.user ];
            };

        } ]);