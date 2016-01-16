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

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "TASK", configName: "tasks", getInfo: ObjectSignatureService.findSignatures, objectType: ObjectService.ObjectTypes.TASK}
                , {name: "ADHOC", configName: "tasks", getInfo: ObjectSignatureService.findSignatures, objectType: ObjectService.ObjectTypes.TASK}
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

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
        }
    ]);