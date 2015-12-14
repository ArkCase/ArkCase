'use strict';

angular.module('dashboard.details', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('details', {
                    title: 'Details Widget',
                    description: 'Displays details',
                    controller: 'Dashboard.DetailsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/details.client.view.html'
                }
            );
    })
    .controller('Dashboard.DetailsController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Case.InfoService','Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, CaseInfoService, Authentication, DashboardService) {

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
                    $scope.gridOptions.columnDefs = config.widgets[0].columnDefs; //widgets[0] = details

                    //set gridOptions.data
                    CaseInfoService.getCaseInfo($stateParams.id).then(
                        function (data) {
                            $scope.gridOptions.data = [Util.omitNg(data)];
                            $scope.gridOptions.totalItems = 1;
                        }
                        , function (error) {
                            $scope.caseInfo = null;
                            $scope.progressMsg = $translate.instant("cases.progressError") + " " + id;
                            return error;
                        }
                    );
                }
            }
        }
    ]);