'use strict';

angular.module('dashboard.reworkdetails', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('reworkdetails', {
                    title: 'Rework Details Widget',
                    description: 'Displays location',
                    controller: 'Dashboard.LocationController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/location-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.LocationController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Task.InfoService'
        , 'Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, TaskInfoService, Authentication, DashboardService, ConfigService) {

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
                {name: "TASK", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
                , {name: "ADHOC", configName: "tasks", getInfo: TaskInfoService.getTaskInfo}
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
                            return widget.id === "reworkdetails";
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

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[1].columnDefs; //tasks.config.widget[1] = rework

                    //set gridOptions.data
                    if ($stateParams.type) {
                        if ($stateParams.type == 'task' || $stateParams.type == 'ADHOC') {
                            TaskInfoService.getTaskInfo($stateParams.id).then(
                                function (data) {
                                    $scope.gridOptions.data = [data];
                                    $scope.gridOptions.totalItems = 1;
                                }
                                , function (error) {
                                    $scope.complaintInfo = null;
                                    $scope.progressMsg = $translate.instant("tasks.progressError") + " " + id;
                                    return error;
                                }
                            );
                        }
                        else {
                            //do nothing
                        }
                    }
                }
            }
        }
    ]);