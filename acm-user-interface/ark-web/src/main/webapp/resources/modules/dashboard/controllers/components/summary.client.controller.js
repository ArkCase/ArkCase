'use strict';

angular.module('dashboard.summary', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('summary', {
                    title: 'Summary Widget',
                    description: 'Displays a summary of hours',
                    controller: 'Dashboard.SummaryController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/summary.client.view.html'
                }
            );
    })
    .controller('Dashboard.SummaryController', ['$scope', '$translate', '$stateParams', 'UtilService',
        , 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, Authentication, DashboardService) {

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
                    $scope.gridOptions.columnDefs = config.widgets[7].columnDefs; //tasks.config.widget[7] = signature

                    //set gridOptions.data
                    if ($stateParams.type) {
                        //if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                        //
                        //}
                        //else {
                        //    //do nothing
                        //}
                    }
                }
            }
        }
    ]);