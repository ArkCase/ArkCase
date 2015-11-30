'use strict';

angular.module('tasks').controller('TasksController', ['$scope', '$stateParams', '$translate', 'StoreService', 'UtilService', 'ConfigService', 'Task.InfoService',
    function ($scope, $stateParams, $translate, Store, Util, ConfigService, TaskInfoService) {
        var promiseGetModuleConfig = ConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.config = config;
            return config;
        });
        $scope.$on('req-component-config', function (e, componentId) {
            promiseGetModuleConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        });
        $scope.$on('report-task-updated', function (e, taskInfo) {
            TaskInfoService.updateTaskInfo(taskInfo);
            $scope.$broadcast('task-updated', taskInfo);
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

                TaskInfoService.getTaskInfo(id).then(
                    function (taskInfo) {
                        $scope.progressMsg = null;
                        $scope.taskInfo = taskInfo;
                        $scope.$emit("report-task-updated", taskInfo);
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