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
                    $scope.gridOptions.columnDefs = config.widgets[1].columnDefs; //widget[1] = people

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            CaseInfoService.getCaseInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.personAssociations;
                                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                                }
                                , function (error) {
                                    $scope.caseInfo = null;
                                    $scope.progressMsg = $translate.instant("cases.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else if ($stateParams.type == 'complaint') {
                            ComplaintInfoService.getComplaintInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = data.personAssociations;
                                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("complaint.progressError") + " " + id;
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