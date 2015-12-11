/**
 * Created by maksud.sharif on 12/11/2015.
 */
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
    .controller('Dashboard.DetailsController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Object.InfoService','Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, ObjectInfoService, Authentication, DashboardService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'details');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            function applyConfig(e, componentId, config) {
                if (componentId == 'details') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.columnDefs;

                    //set gridOptions.data
                    ObjectInfoService.get({
                            type: 'casefile',
                            id: ($stateParams.id ? $stateParams.id : 101)
                        },
                        function (data) {
                            $scope.gridOptions.data = [Util.omitNg(data)];
                            $scope.gridOptions.totalItems = 1;
                        }
                    );
                }
            }
        }
    ]);