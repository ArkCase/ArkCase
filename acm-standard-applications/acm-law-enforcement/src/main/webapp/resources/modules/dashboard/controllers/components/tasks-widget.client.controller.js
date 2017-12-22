'use strict';

angular.module('dashboard.tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('tasks', {
                title: 'dashboard.widgets.tasks.title',
                description: 'dashboard.widgets.tasks.description',
                controller: 'Dashboard.TasksController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/tasks-widget.client.view.html',
                commonName: 'tasks'
            });
    })
    .controller('Dashboard.TasksController', ['$scope', '$translate', '$stateParams', '$q',
        'UtilService', 'Case.InfoService', 'Complaint.InfoService','Authentication', 'Dashboard.DashboardService', 'ObjectService', 'Object.TaskService', 'ConfigService', 'Helper.ObjectBrowserService', 'Helper.UiGridService',
        function ($scope, $translate, $stateParams, $q,
                  Util, CaseInfoService, ComplaintInfoService, Authentication, DashboardService, ObjectService, ObjectTaskService, ConfigService, HelperObjectBrowserService, HelperUiGridService) {

            var getChildTasks =  function (parentId){
                var currentObjectId = parentId;
                return ObjectTaskService.queryChildTasks(module.objectType, currentObjectId).then(function (data) {
                    var tasks = data.response.docs;
                    gridHelper.setWidgetsGridData(tasks);
                });
            };

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: getChildTasks,
                    objectType: ObjectService.ObjectTypes.CASE_FILE
                },
                {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: getChildTasks,
                    objectType: ObjectService.ObjectTypes.COMPLAINT
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            $scope.gridOptions = {
                enableColumnResizing: true,
                columnDefs: []
            };

            var gridHelper = new HelperUiGridService.Grid({scope: $scope});

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
                , onConfigRetrieved: function (componentConfig) {
                    onConfigRetrieved(componentConfig);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.objectInfo = objectInfo;
                if (module.objectType == ObjectService.ObjectTypes.CASE_FILE) {
                    var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                    ObjectTaskService.queryChildTasks(module.objectType, currentObjectId).then(function (data) {
                        var tasks = data.response.docs;
                        gridHelper.setWidgetsGridData(tasks);
                    });
                }
                else {
                    var currentObjectId = Util.goodMapValue($scope.objectInfo, "complaintId");
                    ObjectTaskService.queryChildTasks(module.objectType, currentObjectId).then(function (data) {
                        var tasks = data.response.docs;
                        gridHelper.setWidgetsGridData(tasks);
                    });
                }
            };

            var onConfigRetrieved = function (componentConfig) {
                var widgetInfo = _.find(componentConfig.widgets, function (widget) {
                    return widget.id === "tasks";
                });
                gridHelper.setColumnDefs(widgetInfo);
            };
        }
    ]);