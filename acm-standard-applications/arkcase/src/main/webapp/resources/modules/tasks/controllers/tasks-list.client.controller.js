'use strict';

angular.module('tasks').controller(
        'TasksListController',
        [ '$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ObjectService', 'Task.InfoService', 'Task.ListService', 'Helper.ObjectBrowserService', 'MessageService',
                function($scope, $state, $stateParams, $translate, Util, ObjectService, TaskInfoService, TaskListService, HelperObjectBrowserService, MessageService) {
                    //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
                    new HelperObjectBrowserService.Tree({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "tasks",
                        resetTreeData: function() {
                            return TaskListService.resetTasksTreeData();
                        },
                        updateTreeData: function(start, n, sort, filters, query, nodeData) {
                            return TaskListService.updateTasksTreeData(start, n, sort, filters, query, nodeData);
                        },
                        getTreeData: function(start, n, sort, filters, query) {
                            return TaskListService.queryTasksTreeData(start, n, sort, filters, query);
                        },
                        getNodeData: function(taskId) {
                            return TaskInfoService.getTaskInfo(taskId);
                        },
                        makeTreeNode: function(taskInfo) {
                            var adhocTask = Util.goodValue(taskInfo.adhocTask, false);
                            return {
                                nodeId: Util.goodValue(taskInfo.taskId, 0),
                                nodeType: (adhocTask) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK,
                                nodeTitle: Util.goodValue(taskInfo.title),
                                nodeToolTip: Util.goodValue(taskInfo.title)
                            };
                        }
                    });
                } ]);