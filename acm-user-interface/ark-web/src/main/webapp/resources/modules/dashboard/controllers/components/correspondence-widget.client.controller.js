'use strict';

angular.module('dashboard.correspondence', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('correspondence', {
                    title: 'Correspondence',
                    description: 'Displays correspondence',
                    controller: 'Dashboard.CorrespondenceController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/correspondence-widget.client.view.html',
                    commonName: 'correspondence'
                }
            );
    })
    .controller('Dashboard.CorrespondenceController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Case.InfoService', 'Object.CorrespondenceService', 'ObjectService', 'Authentication', 'Dashboard.DashboardService'
        , 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ObjectCorrespondenceService, ObjectService
            , Authentication, DashboardService, ConfigService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectCorrespondenceService.queryCorrespondences, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectCorrespondenceService.queryCorrespondences, objectType: ObjectService.ObjectTypes.COMPLAINT}
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
                promiseInfo = module.getInfo(module.objectType, $stateParams.id, 0, 5);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "tasks";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        var correspondenceData = info[0];
                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = correspondenceData.children;
                        $scope.gridOptions.totalItems = Util.goodValue(correspondenceData.totalChildren, 0);
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);