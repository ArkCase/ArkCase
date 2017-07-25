'use strict';

angular.module('dashboard.reworkDetails', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('reworkDetails', {
                title: 'dashboard.widgets.reworkDetails.title',
                description: 'dashboard.widgets.reworkDetails.description',
                controller: 'Dashboard.ReworkDetailsController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/rework-details-widget.client.view.html',
                commonName: 'reworkDetails'
            });
    })
    .controller('Dashboard.ReworkDetailsController', ['$scope', '$stateParams', '$translate',
        'Task.InfoService', 'Helper.ObjectBrowserService', 'UtilService',
            function ($scope, $stateParams, $translate,
                      TaskInfoService, HelperObjectBrowserService, Util) {

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

                    if (!Util.isEmpty($scope.gridOptions.data[0].reworkInstructions)) {
                            $scope.gridOptions.data[0].taskStartDate = "";
                            $scope.gridOptions.data[0].assignee = "";
                            $scope.gridOptions.noData = false;
                    }
                    else {
                        $scope.gridOptions.data = [];
                        $scope.gridOptions.noData = true;
                        $scope.noDataMessage = $translate.instant('dashboard.widgets.reworkDetails.noDataMessage');
                    }
                };

                var onConfigRetrieved = function (componentConfig) {
                    var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                        return widget.id === "reworkDetails";
                    });

                    $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
                };
        }
    ]);