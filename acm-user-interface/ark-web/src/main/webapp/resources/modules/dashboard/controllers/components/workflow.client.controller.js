'use strict';

angular.module('dashboard.workflow', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('workflow', {
                    title: 'Workflow Widget',
                    description: 'Displays workflow',
                    controller: 'Dashboard.WorkflowOverviewController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/workflow.client.view.html'
                }
            );
    })
    .controller('Dashboard.WorkflowOverviewController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Task.InfoService', 'Task.HistoryService'
        , 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, TaskInfoService, TaskHistoryService, Authentication, DashboardService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[5].columnDefs; //tasks.config.widget[5] = workflow

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            TaskInfoService.getTaskInfo($stateParams.id).then(
                                function (taskInfo) {
                                    var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory(taskInfo);
                                    $q.all([promiseQueryTaskHistory, promiseUsers]).then(function (data) {
                                        var taskHistory = data[0];
                                        $scope.gridOptions.data = taskHistory;
                                        $scope.gridOptions.totalItems = taskHistory.length;
                                        //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                                    });
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("tasks.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);