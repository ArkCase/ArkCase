'use strict';

angular.module('dashboard.tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tasks', {
                    title: 'Tasks',
                    description: 'Displays Tasks',
                    controller: 'Dashboard.TasksController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/tasks-widget.client.view.html',
                    commonName: 'tasks'
                }
            );
    })
    .controller('Dashboard.TasksController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.TaskService'
        , 'ConfigService', 'Helper.ObjectBrowserService',
        function ($scope, $translate, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, Authentication
            , DashboardService, ObjectService, ObjectTaskService, ConfigService, HelperObjectBrowserService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectTaskService.queryChildTasks, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectTaskService.queryChildTasks, objectType: ObjectService.ObjectTypes.COMPLAINT}
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
                promiseInfo = module.getInfo(module.objectType, currentObjectId, 0, 5);

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
        }
    ]);