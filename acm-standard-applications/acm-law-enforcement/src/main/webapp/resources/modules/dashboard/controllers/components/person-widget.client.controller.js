'use strict';

angular.module('dashboard.person', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('person', {
                title: 'dashboard.widgets.person.title',
                description: 'dashboard.widgets.person.description',
                controller: 'Dashboard.PersonController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/person.client.view.html',
                commonName: 'person'
            });
    })
    .controller('Dashboard.PersonController', ['$scope', '$stateParams', '$translate',
        'CostTracking.InfoService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'Helper.UiGridService', 'UtilService',
            function ($scope, $stateParams, $translate,
                      CostTrackingInfoService, TimeTrackingInfoService, HelperObjectBrowserService, HelperUiGridService, Util) {

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
                    if(!Util.isArrayEmpty(objectInfo.user)) {
                        $scope.gridOptions.data = objectInfo.user;
                        $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                        $scope.gridOptions.noData = false;
                    }
                    else {
                        $scope.gridOptions.data = [];
                        $scope.gridOptions.totalItems = 0;
                        $scope.gridOptions.noData = true;
                        $scope.noDataMessage = $translate.instant('dashboard.widgets.person.noDataMessage');
                    }
                };

                var onConfigRetrieved = function (componentConfig) {
                    var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                        return widget.id === "person";
                    });

                    $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
                    gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                };
        }
    ]);