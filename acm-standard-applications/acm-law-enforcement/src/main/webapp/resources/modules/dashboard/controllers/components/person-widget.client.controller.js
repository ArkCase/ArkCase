'use strict';

angular.module('dashboard.person', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('person', {
                    title: 'Person',
                    description: 'Displays person',
                    controller: 'Dashboard.PersonController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/person.client.view.html',
                    commonName: 'person'
                }
            );
    })
    .controller('Dashboard.PersonController', ['$scope', '$stateParams', 'CostTracking.InfoService'
        , 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService'
        , function ($scope, $stateParams, CostTrackingInfoService, TimeTrackingInfoService
            , HelperObjectBrowserService, HelperUiGridService) {

            var modules = [
                {
                    name: "COSTSHEET",
                    configName: "cost-tracking",
                    getInfo: CostTrackingInfoService.getCostsheetInfo,
                    validateInfo: CostTrackingInfoService.validateCostsheet
                }
                , {
                    name: "TIMESHEET",
                    configName: "time-tracking",
                    getInfo: TimeTrackingInfoService.getTimesheetInfo,
                    validateInfo: TimeTrackingInfoService.validateTimesheet
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});
            var promiseUsers = gridHelper.getUsers();

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.costsheetInfo = objectInfo;
                $scope.gridOptions.data = objectInfo.user ? [objectInfo.user] : [];
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "person";
                });

                gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
            };
        }
    ]);