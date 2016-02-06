'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Task.HistoryService', 'Task.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, TaskHistoryService, TaskInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "workflow"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (taskInfo) {
                $scope.taskInfo = taskInfo;
            }
            , onConfigRetrieved: function (componentConfig) {
                onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
        };


        $scope.retrieveGridData = function () {
            if (Util.goodPositive($scope.currentObjectId, false)) {
                TaskInfoService.getTaskInfo($scope.currentObjectId).then(function (taskInfo) {
                    $scope.taskInfo = taskInfo;

                    var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory($scope.taskInfo);
                    $q.all([promiseQueryTaskHistory, promiseUsers]).then(function (data) {
                        var taskHistory = data[0];
                        $scope.gridOptions.data = taskHistory;
                        $scope.gridOptions.totalItems = taskHistory.length;
                        //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                    });
                    return taskInfo;
                });
            }
        };
    }
]);
