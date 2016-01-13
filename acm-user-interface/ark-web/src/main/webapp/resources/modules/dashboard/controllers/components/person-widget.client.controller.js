'use strict';

angular.module('dashboard.person', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('person', {
                    title: 'Person Widget',
                    description: 'Displays person',
                    controller: 'Dashboard.PersonController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/person.client.view.html'
                }
            );
    })
    .controller('Dashboard.PersonController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService'
        , 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'CostTracking.InfoService', 'TimeTracking.InfoService',
        function ($scope, $translate, $stateParams, $q, Util, Authentication, DashboardService, ConfigService, CostTrackingInfoService
        , TimeTrackingInfoService) {

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
                {name: "COSTSHEET", configName: "cost-tracking", getInfo: CostTrackingInfoService.getCostsheetInfo}
                , {name: "TIMESHEET", configName: "time-tracking", getInfo: TimeTrackingInfoService.getTimesheetInfo}
                ]

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo($stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "person";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = [$scope.costsheetInfo.user];
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
                        //if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                        //
                        //}
                        //else {
                        //    //do nothing
                        //}
                    }
                }
            }
        }
    ]);