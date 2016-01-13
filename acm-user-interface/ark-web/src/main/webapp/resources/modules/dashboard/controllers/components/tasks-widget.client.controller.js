'use strict';

angular.module('dashboard.tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tasks', {
                    title: 'Tasks Widget',
                    description: 'Displays Tasks',
                    controller: 'Dashboard.TasksController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/tasks-widget.client.view.html'
                }
            );
    })
    .controller('Dashboard.TasksController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.TaskService'
        , 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ObjectService, ObjectTaskService, ConfigService) {

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
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectTaskService.queryChildTasks, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectTaskService.queryChildTasks, objectType: ObjectService.ObjectTypes.COMPLAINT}
            ]

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

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

                        var tasks = info.response.docs;
                        $scope.gridOptions.data = tasks;
                        $scope.gridOptions.totalItems = info.response.numFound;
                    },
                    function (err) {

                    }
                );
            }

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;
                    $scope.gridOptions.columnDefs = config.widgets[5].columnDefs; //widget[5] = tasks

                    //set gridOptions.data
                    // See case-tasks.client.controller.js
                    if ($stateParams.type) {
                        if ($stateParams.type == "casefile") {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function (data) {
                                var tasks = data.response.docs;
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;
                            });
                        }
                        else if ($stateParams.type == 'complaint') {
                            ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT
                                , $stateParams.id
                                , 0
                                , 5
                            ).then(function (data) {
                                var tasks = data.response.docs;
                                $scope.gridOptions.data = tasks;
                                $scope.gridOptions.totalItems = data.response.numFound;
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