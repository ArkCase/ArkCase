'use strict';

angular.module('time-tracking').controller('TimeTracking.PersonController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'Helper.UiGridService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, Util, ConfigService, HelperUiGridService, TimeTrackingInfoService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "time-tracking",
                componentId: "person",
                retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo,
                validateObjectInfo: TimeTrackingInfoService.validateTimesheet,
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
                    gridHelper.disableGridScrolling(updatedConfig);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                $scope.gridOptions = $scope.gridOptions || {};
                $scope.gridOptions.data = [ $scope.objectInfo.user ];
            };

        } ]);
