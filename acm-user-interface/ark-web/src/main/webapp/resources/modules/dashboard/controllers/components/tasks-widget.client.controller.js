'use strict';

angular.module('dashboard.tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tasks', {
                    title: 'Tasks Widget',
                    description: 'Displays Tasks',
                    controller: 'Dashboard.TasksController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/tasks-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.TasksController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.TaskService',
        function ($scope, $translate, $stateParams, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ObjectService, ObjectTaskService) {

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
                    $scope.gridOptions.columnDefs = config.widgets[5].columnDefs; //widget[5] = tasks

                    //set gridOptions.data
                    // See case-tasks.client.controller.js
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function (data) {
                                var tasks = data.response.docs;
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;
                            });
                        }
                        else if ($stateParams.type == 'complaint') {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function (data) {
                                var tasks = data.response.docs;
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;
                            });
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);