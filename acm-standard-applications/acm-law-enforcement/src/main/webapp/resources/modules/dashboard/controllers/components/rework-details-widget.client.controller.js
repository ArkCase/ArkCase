'use strict';

angular.module('dashboard.reworkDetails', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('reworkDetails', {
                    title: 'Rework Details',
                    description: 'Displays location',
                    controller: 'Dashboard.LocationController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/rework-details-widget.client.view.html',
                    commonName: 'reworkDetails'
                }
            );
    })
    .controller('Dashboard.LocationController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Task.InfoService'
        , 'Authentication', 'Dashboard.DashboardService', 'ConfigService', 'Helper.ObjectBrowserService',
        function ($scope, $translate, $stateParams, $q, Util, TaskInfoService, Authentication, DashboardService
            , ConfigService, HelperObjectBrowserService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "TASK", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
                , {name: "ADHOC", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
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
                promiseInfo = module.getInfo(currentObjectId);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = angular.copy(data[1]);
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "reworkDetails";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = [info];
                        if(!$scope.gridOptions.data[0].reworkInstructions) {
                            $scope.gridOptions.data[0].taskStartDate = "";
                            $scope.gridOptions.data[0].assignee = "";
                        }
                        $scope.gridOptions.totalItems = $scope.gridOptions.data ? 1 : 0;
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);