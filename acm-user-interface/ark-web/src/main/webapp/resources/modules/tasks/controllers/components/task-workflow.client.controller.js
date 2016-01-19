'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Task.HistoryService', 'Task.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, TaskHistoryService, TaskInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        ConfigService.getComponentConfig("tasks", "workflow").then(function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
            return config;
        });


        $scope.retrieveGridData = function () {
            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                TaskInfoService.getTaskInfo(currentObjectId).then(function (taskInfo) {
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
