'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'Helper.UiGridService', 'ObjectService', 'Task.HistoryService', 'Task.InfoService'
    , function ($scope, $stateParams, $q, Util, HelperUiGridService, ObjectService, TaskHistoryService, TaskInfoService) {

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        $scope.$emit('req-component-config', 'workflow');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('workflow' == componentId) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.setUserNameFilter(promiseUsers);

                $scope.retrieveGridData();
            }
        });

        $scope.$on('task-updated', function (e, data) {
            if (TaskInfoService.validateTaskInfo(data)) {
                $scope.taskInfo = data;
            }
        });

        $scope.retrieveGridData = function () {
            if ($scope.taskInfo) {
                var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory($scope.taskInfo);
                $q.all([promiseQueryTaskHistory, promiseUsers]).then(function (data) {
                    var taskHistory = data[0];
                    $scope.gridOptions.data = taskHistory;
                    $scope.gridOptions.totalItems = taskHistory.length;
                    gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
