'use strict';

angular.module('dashboard.references', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('references', {
                    title: 'References',
                    description: 'Displays references',
                    controller: 'Dashboard.ReferencesController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/references-widget.client.view.html',
                    commonName: 'references'
                }
            );
    })
    .controller('Dashboard.ReferencesController', ['$scope', '$translate', '$stateParams', '$q','UtilService'
        , 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ConfigService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: CaseInfoService.getCaseInfo}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ComplaintInfoService.getComplaintInfo}
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
                promiseInfo = module.getInfo($stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "references";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = info.references;
                        $scope.gridOptions.totalItems = info.references ? $scope.gridOptions.data.length : 0;
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);