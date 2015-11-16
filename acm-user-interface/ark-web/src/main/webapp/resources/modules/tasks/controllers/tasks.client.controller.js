'use strict';

angular.module('tasks').controller('TasksController', ['$scope', '$stateParams', '$translate', 'StoreService', 'UtilService', 'CallConfigService', 'CallTasksService',
    function ($scope, $stateParams, $translate, Store, Util, CallConfigService, CallTasksService) {
        var promiseGetModuleConfig = CallConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.config = config;
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });


        $scope.progressMsg = $translate.instant("tasks.progressNoTask");
        $scope.$on('req-select-task', function (e, selectedTask) {
            var componentsStore = new Store.Variable("TaskComponentsStore");
            componentsStore.set(selectedTask.components);
            $scope.$broadcast('task-selected', selectedTask);

            var id = Util.goodMapValue(selectedTask, "nodeId", null);
            loadTask(id);
        });


        var loadTask = function (id) {
            if (id) {
                if ($scope.taskInfo && $scope.taskInfo.taskId != id) {
                    $scope.taskInfo = null;
                }
                $scope.progressMsg = $translate.instant("tasks.progressLoading") + " " + id + "...";

                CallTasksService.getTaskInfo(id).then(
                    function (taskInfo) {
                        $scope.progressMsg = null;
                        $scope.taskInfo = taskInfo;
                        $scope.$broadcast('task-retrieved', taskInfo);
                        return taskInfo;
                    }
                    , function (error) {
                        $scope.taskInfo = null;
                        $scope.progressMsg = $translate.instant("tasks.progressError") + " " + id;
                        return error;
                    }
                );
            }
        };

        loadTask($stateParams.id);
    }
]);