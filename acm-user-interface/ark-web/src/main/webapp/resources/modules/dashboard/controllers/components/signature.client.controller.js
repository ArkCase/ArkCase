'use strict';

angular.module('dashboard.signature', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('signature', {
                    title: 'Signature Widget',
                    description: 'Displays Signatures',
                    controller: 'Dashboard.SignatureController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/signature.client.view.html'
                }
            );
    })
    .controller('Dashboard.SignatureController', ['$scope', '$translate', '$stateParams', 'UtilService', 'Task.InfoService'
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
                    $scope.gridOptions.columnDefs = config.widgets[7].columnDefs; //tasks.config.widget[7] = signature

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            if (Util.goodPositive($stateParams.id, false)) {
                                var promiseQueryAudit = ObjectSignatureService.findSignatures(ObjectService.ObjectTypes.TASK, $stateParams.id);

                                $q.all([promiseQueryAudit, promiseUsers]).then(function (data) {
                                    var signatures = data[0];
                                    $scope.gridOptions.data = signatures;
                                    $scope.gridOptions.totalItems = signatures.length;
                                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                                });
                            }
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);