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
    .controller('Dashboard.LocationController', ['$scope', '$stateParams', 'Task.InfoService', 'Helper.ObjectBrowserService'
        ,function ($scope, $stateParams, TaskInfoService, HelperObjectBrowserService) {

            var modules = [
                {
                    name: "TASK",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo
                }
                , {
                    name: "ADHOC",
                    configName: "tasks",
                    getInfo: TaskInfoService.getTaskInfo,
                    validateInfo: TaskInfoService.validateTaskInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                var data = angular.copy(objectInfo);
                $scope.gridOptions.data = [data];
                if (!$scope.gridOptions.data[0].reworkInstructions) {
                    $scope.gridOptions.data[0].taskStartDate = "";
                    $scope.gridOptions.data[0].assignee = "";
                }
                $scope.gridOptions.totalItems = $scope.gridOptions.data ? 1 : 0;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "reworkDetails";
                });

                $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
            };
        }
    ]);