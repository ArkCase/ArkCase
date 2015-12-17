'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Task.HistoryService', 'Task.InfoService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, HelperUiGridService, ObjectService, TaskHistoryService, TaskInfoService) {

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

        TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
            $scope.taskInfo = taskInfo;
            return taskInfo;
        });

        $scope.retrieveGridData = function () {
            if ($scope.taskInfo) {
                var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory($scope.taskInfo);
                $q.all([promiseQueryTaskHistory, promiseUsers]).then(function (data) {
                    var taskHistory = data[0];
                    $scope.gridOptions.data = taskHistory;
                    $scope.gridOptions.totalItems = taskHistory.length;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
