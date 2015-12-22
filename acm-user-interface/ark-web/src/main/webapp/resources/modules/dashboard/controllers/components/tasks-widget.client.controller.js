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
    .controller('Dashboard.TasksController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, CaseInfoService, ComplaintInfoService, Authentication, DashboardService) {

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
                    $scope.gridOptions.data = {};

                }
            }
        }
    ]);