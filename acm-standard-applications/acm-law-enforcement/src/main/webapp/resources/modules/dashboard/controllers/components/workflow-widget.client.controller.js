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
    .controller('Dashboard.WorkflowOverviewController', ['$scope', '$stateParams'
        , 'Task.InfoService', 'Task.HistoryService', 'Helper.ObjectBrowserService'
        , function ($scope, $stateParams, TaskInfoService, TaskHistoryService, HelperObjectBrowserService) {

            var modules = [
                {
                    name: "TASK",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo,
                    getHistory: TaskHistoryService.queryTaskHistory
                }
                , {
                    name: "ADHOC",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo,
                    getHistory: TaskHistoryService.queryTaskHistory
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

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
                module.getHistory(objectInfo).then(function (taskHistoryInfo) {
                    var taskHistory = taskHistoryInfo[0];
                    $scope.gridOptions.data = taskHistory ? [taskHistory] : [];
                    $scope.gridOptions.totalItems = taskHistoryInfo.length;
                });
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "workflow";
                });
                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);