'use strict';

angular.module('dashboard.correspondence', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('correspondence', {
                    title: 'Correspondence Widget',
                    description: 'Displays correspondence',
                    controller: 'Dashboard.CorrespondenceController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/correspondence-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.CorrespondenceController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Case.InfoService'
        , 'Object.CorrespondenceService', 'ObjectService', 'Authentication', 'Dashboard.DashboardService',
        function ($scope, $translate, $stateParams, Util, CaseInfoService, ObjectCorrespondenceService, ObjectService, Authentication, DashboardService) {

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
                            ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.CASE_FILE
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function(data) {
                                var correspondenceData = data[0];
                                $scope.gridOptions = $scope.gridOptions || {};
                                $scope.gridOptions.data = correspondenceData.children;
                                $scope.gridOptions.totalItems = Util.goodValue(correspondenceData.totalChildren, 0);
                            });
                        }
                        else if ($stateParams.type == 'complaint') {
                            ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.COMPLAINT
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function(data) {
                                var correspondenceData = data[0];
                                $scope.gridOptions = $scope.gridOptions || {};
                                $scope.gridOptions.data = correspondenceData.children;
                                $scope.gridOptions.totalItems = Util.goodValue(correspondenceData.totalChildren, 0);
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