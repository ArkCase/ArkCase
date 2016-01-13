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
    .controller('Dashboard.SignatureController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Task.InfoService'
        , 'Authentication', 'Dashboard.DashboardService', 'Object.SignatureService', 'ObjectService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, TaskInfoService, Authentication, DashboardService, ObjectSignatureService
        , ObjectService, ConfigService) {

            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'main');
            $scope.config = null;
            //var userInfo = null;

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "TASK", configName: "tasks", getInfo: ObjectSignatureService.findSignatures, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "ADHOC", configName: "tasks", getInfo: ObjectSignatureService.findSignatures, objectType: ObjectService.ObjectTypes.TASK}
            ]

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(module.objectType, $stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "signatures";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        var signatures = info[0];
                        $scope.gridOptions.data = signatures;
                        $scope.gridOptions.totalItems = signatures.length;
                    },
                    function (err) {

                    }
                );
            }
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