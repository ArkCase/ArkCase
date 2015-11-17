'use strict';

angular.module('tasks').controller('Tasks.WorkflowOverviewController', ['$scope', '$stateParams', '$q', 'UtilService', 'HelperService', 'ConstantService', 'Task.HistoryService',
    function ($scope, $stateParams, $q, Util, Helper, Constant, TaskHistoryService) {
        $scope.$emit('req-component-config', 'workflow');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('workflow' == componentId) {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);
                Helper.Grid.setUserNameFilter($scope, promiseUsers);

                $scope.retrieveGridData();
            }
        });

        var promiseUsers = Helper.Grid.getUsers($scope);

        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;
        });

        $scope.retrieveGridData = function () {
            if ($scope.taskInfo) {
                var promiseQueryTaskHistory = TaskHistoryService.queryTaskHistory($scope.taskInfo);
                $q.all([promiseQueryTaskHistory, promiseUsers]).then(function (data) {
                    var taskHistory = data[0];
                    $scope.gridOptions.data = taskHistory;
                    $scope.gridOptions.totalItems = taskHistory.length;
                    Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                });
            }
        };
    }
]);
