'use strict';

angular.module('dashboard.person', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('person', {
                    title: 'Person Widget',
                    description: 'Displays person',
                    controller: 'Dashboard.PersonController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/person.client.view.html'
                }
            );
    })
    .controller('Dashboard.PersonController', ['$scope', '$translate', '$stateParams', 'UtilService',
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