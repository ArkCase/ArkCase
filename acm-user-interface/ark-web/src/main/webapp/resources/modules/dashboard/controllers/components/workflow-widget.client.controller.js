'use strict';

angular.module('dashboard.workflow', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('workflow', {
                    title: 'Workflow',
                    description: 'Displays workflow',
                    controller: 'Dashboard.WorkflowOverviewController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/workflow.client.view.html',
                    commonName: 'workflow'
                }
            );
    })
    .controller('Dashboard.WorkflowOverviewController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Task.InfoService', 'Task.HistoryService', 'Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, TaskInfoService, TaskHistoryService, Authentication
            , DashboardService, ConfigService) {

            var promiseConfig;
            var promiseTaskInfo;
            var promiseQueryTaskHistory;

            var modules = [
                {name: "TASK", configName: "tasks", getInfo: TaskInfoService.getTaskInfo, getHistory : TaskHistoryService.queryTaskHistory}
                , {name: "ADHOC", configName: "tasks", getInfo: TaskInfoService.getTaskInfo, getHistory : TaskHistoryService.queryTaskHistory}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseTaskInfo = module.getInfo($stateParams.id);


                $q.all([promiseConfig, promiseTaskInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "workflow";
                        });

                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        promiseQueryTaskHistory = module.getHistory(info).then( function (taskHistoryInfo){
                            var taskHistory = taskHistoryInfo[0];
                            $scope.gridOptions.data = [taskHistory];
                            $scope.gridOptions.totalItems = taskHistoryInfo.length;
                        });
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);