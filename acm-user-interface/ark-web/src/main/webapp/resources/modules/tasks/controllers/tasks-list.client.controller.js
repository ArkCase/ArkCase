'use strict';

angular.module('tasks').controller('TasksListController', ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ObjectService', 'Task.InfoService', 'Task.ListService', 'ConfigService', 'Helper.ObjectTreeService',
    function ($scope, $state, $stateParams, $translate, Util, ObjectService, TaskInfoService, TaskListService, ConfigService, HelperObjectTreeService) {
        ConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var treeHelper = new HelperObjectTreeService.Tree({
            scope: $scope
            , nodeId: $stateParams.id
            , getTreeData: function (start, n, sort, filters) {
                return TaskListService.queryTasksTreeData(start, n, sort, filters);
            }
            , getNodeData: function (taskId) {
                return TaskInfoService.getTaskInfo(taskId);
            }
            , makeTreeNode: function (taskInfo) {
                return {
                    nodeId: Util.goodValue(taskInfo.taskId, 0)
                    , nodeType: ObjectService.ObjectTypes.TASK
                    , nodeTitle: Util.goodValue(taskInfo.title)
                    , nodeToolTip: Util.goodValue(taskInfo.title)
                };
            }
        });
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };


        $scope.onSelect = function (selectedTask) {
            $scope.$emit('req-select-task', selectedTask);
            var components = Util.goodArray(selectedTask.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('tasks.' + componentType, {
                id: selectedTask.nodeId
            });
        };

    }
]);