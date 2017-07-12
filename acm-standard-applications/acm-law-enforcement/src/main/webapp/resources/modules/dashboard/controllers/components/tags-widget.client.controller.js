'use strict';

angular.module('dashboard.tags', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tags', {
                    title: 'dashboard.widgets.tags.title',
                    description: 'dashboard.widgets.tags.description',
                    controller: 'Dashboard.TagsController',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/tags-widget.client.view.html',
                    commonName: 'tags'
                }
            );
    })
    .controller('Dashboard.TagsController', ['$scope', '$stateParams', '$q', 'UtilService', 'Case.InfoService'
        , 'Complaint.InfoService', 'Task.InfoService', 'Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.TagsService'
        , 'ConfigService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $stateParams, $q, Util, CaseInfoService, ComplaintInfoService, TaskInfoService, Authentication
            , DashboardService, ObjectService, ObjectTagsService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

            var promiseConfig;
            var promiseInfo;
            var modules = [
                {name: "CASE_FILE", configName: "cases", getInfo: ObjectTagsService.getAssociateTags, objectType: ObjectService.ObjectTypes.CASE_FILE}
                , {name: "COMPLAINT", configName: "complaints", getInfo: ObjectTagsService.getAssociateTags, objectType: ObjectService.ObjectTypes.COMPLAINT}
                , {name: "TASK", configName: "tasks", getInfo: ObjectTagsService.getAssociateTags, objectType: ObjectService.ObjectTypes.TASK}
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
                promiseInfo = module.getInfo(currentObjectId, module.objectType);
                var gridHelper = new HelperUiGridService.Grid({scope: $scope});
                var promiseUsers = gridHelper.getUsers();

                $q.all([promiseConfig, promiseInfo, promiseUsers]).then(function (data) {
                        var config = _.find(data[0].components, {id: "main"});
                        var info = data[1];
                        var widgetInfo = _.find(config.widgets, function (widget) {
                            return widget.id === "tags";
                        });
                        gridHelper.setUserNameFilterToConfig(promiseUsers, widgetInfo);
                        $scope.config = config;
                        $scope.gridOptions.columnDefs = widgetInfo.columnDefs;
                        var tags = info;

                        if (tags) {
                            $scope.gridOptions.data = tags;
                            $scope.gridOptions.totalItems = tags ? tags.length : 0;
                            $scope.gridOptions.noData = false;
                        }
                        else
                        {
                            $scope.gridOptions.data = [];
                            $scope.gridOptions.noData = true;
                        }
                    },
                    function (err) {

                    }
                );
            }
        }
    ]);