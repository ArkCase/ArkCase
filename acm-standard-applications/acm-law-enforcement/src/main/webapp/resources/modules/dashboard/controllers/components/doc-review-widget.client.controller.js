'use strict';

angular.module('dashboard.docReview', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('docReview', {
                    title: 'Documents Under Review',
                    description: 'Displays documents under review',
                    controller: 'Dashboard.DocReviewController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/doc-review-widget.client.view.html',
                    commonName: 'docReview'
                }
            );
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
                $scope.gridOptions.data = objectInfo.documentUnderReview ? [objectInfo.documentUnderReview] : [];
                $scope.gridOptions.totalItems = $scope.gridOptions.data.length;
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "docsReview";
                });

                $scope.gridOptions.columnDefs = widgetInfo ? widgetInfo.columnDefs : [];
            };
        }
    ]);