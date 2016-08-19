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
    .controller('Dashboard.PersonController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'CostTracking.InfoService', 'TimeTracking.InfoService'
        , 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $translate, $stateParams, $q, Util, Authentication, DashboardService, ConfigService, CostTrackingInfoService
        , TimeTrackingInfoService, HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "COSTSHEET", configName: "cost-tracking", getInfo: CostTrackingInfoService.getCostsheetInfo}
                , {name: "TIMESHEET", configName: "time-tracking", getInfo: TimeTrackingInfoService.getTimesheetInfo}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });


            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (module && Util.goodPositive(currentObjectId, false)) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(currentObjectId);
                var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                var promiseUsers = gridHelper.getUsers();

                $q.all([promiseConfig, promiseInfo, promiseUsers]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "person";
                        });
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.costsheetInfo = info;
                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = [$scope.costsheetInfo.user];
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);