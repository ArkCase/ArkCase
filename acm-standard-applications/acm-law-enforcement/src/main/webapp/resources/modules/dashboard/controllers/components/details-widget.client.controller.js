'use strict';

angular.module('dashboard.details', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('details', {
                    title: 'Details',
                    description: 'Displays details',
                    controller: 'Dashboard.DetailsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/details-widget.client.view.html',
                    commonName: 'details'
                }
            );
    })
    .controller('Dashboard.DetailsController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService', 'Task.InfoService', 'CostTracking.InfoService', 'TimeTracking.InfoService', 'Authentication'
        , 'Dashboard.DashboardService', 'ConfigService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, TaskInfoService
            , CostTrackingInfoService, TimeTrackingInfoService, Authentication, DashboardService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
                , {name: "COSTSHEET", configName: "cost-tracking", getInfo: CostTrackingInfoService.getCostsheetInfo}
                , {name: "TIMESHEET", configName: "time-tracking", getInfo: TimeTrackingInfoService.getTimesheetInfo}
                , {name: "TASK", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
                , {name: "ADHOC", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
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
                            return widget.id === "details";
                        });
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = [Util.omitNg(info)];
                        $scope.gridOptions.totalItems = 1;
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);