'use strict';

angular.module('dashboard.docReview', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('docReview', {
                title: 'dashboard.widgets.docReview.title',
                description: 'dashboard.widgets.docReview.description',
                controller: 'Dashboard.DocReviewController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/doc-review-widget.client.view.html',
                commonName: 'docReview'
            });
    })
    .controller('Dashboard.DocReviewController', ['$scope', '$stateParams', 'Task.InfoService', 'Helper.ObjectBrowserService'
        , function ($scope, $stateParams, TaskInfoService, HelperObjectBrowserService) {

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
                if (objectInfo.documentUnderReview != null) {
                    $scope.gridOptions.data = objectInfo.documentUnderReview;
                    $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
                    $scope.gridOptions.noData = false;
                }
                else {
                    $scope.gridOptions.data = [];
                    $scope.gridOptions.noData = true;
                    $scope.noDataMessage = $translate.instant('dashboard.widgets.docReview.noDataMessage');
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "docsReview";
                });

                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);