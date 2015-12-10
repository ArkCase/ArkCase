'use strict';

angular.module('tasks').controller('TasksController', ['$scope', '$stateParams', '$state', '$translate', 'StoreService'
    , 'UtilService', 'ConfigService', 'Task.InfoService', 'ObjectService', 'Helper.ObjectTreeService'
    , function ($scope, $stateParams, $state, $translate, Store
        , Util, ConfigService, TaskInfoService, ObjectService, HelperObjectTreeService) {

        var promiseGetModuleConfig = ConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.config = config;
            $scope.componentLinks = HelperObjectTreeService.createComponentLinks(config, ObjectService.ObjectTypes.TASK);
            $scope.activeLinkId = "main";
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

        $scope.$on('req-select-task', function (e, selectedTask) {
            var components = Util.goodArray(selectedTask.components);
            $scope.activeLinkId = (1 == components.length) ? components[0] : "main";
        });

        $scope.getActive = function (linkId) {
            return ($scope.activeLinkId == linkId) ? "active" : ""
        };

        $scope.onClickComponentLink = function (linkId) {
            $scope.activeLinkId = linkId;
            $state.go('tasks.' + linkId, {
                id: $stateParams.id
            });
        };

        $scope.progressMsg = $translate.instant("tasks.progressNoTask");
        $scope.$on('req-select-task', function (e, selectedTask) {
            var componentsStore = new Store.Variable("TaskComponentsStore");
            componentsStore.set(selectedTask.components);
            $scope.$broadcast('task-selected', selectedTask);

            var id = Util.goodMapValue(selectedTask, "nodeId", null);
            loadTask(id);
        });


        var loadTask = function (id) {
            if (Util.goodPositive(id)) {
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