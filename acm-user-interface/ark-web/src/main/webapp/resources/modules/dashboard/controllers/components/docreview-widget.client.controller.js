'use strict';

angular.module('dashboard.docreview', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('docreview', {
                    title: 'Documents Under Review Widget',
                    description: 'Displays documents under review',
                    controller: 'Dashboard.DocReviewController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/location-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.DocReviewController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Task.InfoService'
        , 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, TaskInfoService, Authentication, DashboardService) {

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
                    $scope.gridOptions.columnDefs = config.widgets[2].columnDefs; //tasks.config.widget[2] = docsreview

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            TaskInfoService.getTaskInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.documentUnderReview;
                                    $scope.gridOptions.totalItems = 1;
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