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
        , 'ConfigService', 'Helper.ObjectBrowserService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ObjectCorrespondenceService, ObjectService
            , Authentication, DashboardService, ConfigService, HelperObjectBrowserService) {

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

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (module && Util.goodPositive(currentObjectId, false)) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo(module.objectType
                    , currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10));

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "correspondence";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        var correspondenceData = info;
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