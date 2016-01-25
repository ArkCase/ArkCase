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
    .controller('Dashboard.DocReviewController', ['$scope', '$translate', '$stateParams', '$q', 'UtilService', 'Task.InfoService'
        , 'Authentication', 'Dashboard.DashboardService', 'ConfigService',
        function ($scope, $translate, $stateParams, $q, Util, TaskInfoService, Authentication, DashboardService, ConfigService) {

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

            if (module) {
                promiseConfig = ConfigService.getModuleConfig(module.configName);
                promiseInfo = module.getInfo($stateParams.id);

                $q.all([promiseConfig, promiseInfo]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "docsreview";
                        });
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;

                        $scope.gridOptions.data = info.documentUnderReview;
                        $scope.gridOptions.totalItems = $scope.gridOptions.data ? 1 : 0;
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);