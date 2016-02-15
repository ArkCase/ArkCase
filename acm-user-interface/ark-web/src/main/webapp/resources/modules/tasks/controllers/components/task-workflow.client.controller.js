'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Task.HistoryService', 'Task.InfoService'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, ObjectService, TaskHistoryService, TaskInfoService
        , HelperUiGridService, HelperObjectBrowserService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "workflow"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
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
            if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                TaskInfoService.getTaskInfo(componentHelper.currentObjectId).then(function (taskInfo) {
                    $scope.objectInfo = taskInfo;

                    var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory($scope.objectInfo);
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
