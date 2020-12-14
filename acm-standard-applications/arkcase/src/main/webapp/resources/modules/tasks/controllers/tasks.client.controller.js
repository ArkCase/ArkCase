'use strict';

angular.module('tasks').controller(
        'TasksController',
        [ '$scope', '$stateParams', '$state', '$translate', 'Acm.StoreService', 'UtilService', 'ConfigService', 'Task.InfoService', 'ObjectService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, $state, $translate, Store, Util, ConfigService, TaskInfoService, ObjectService, HelperObjectBrowserService) {

                    var contentHelper = new HelperObjectBrowserService.Content({
                        scope: $scope,
                        state: $state,
                        stateParams: $stateParams,
                        moduleId: "tasks",
                        resetObjectInfo: TaskInfoService.resetTaskInfo,
                        getObjectInfo: TaskInfoService.getTaskInfo,
                        updateObjectInfo: TaskInfoService.updateTaskInfo,
                        getObjectIdFromInfo: function(taskInfo) {
                            return Util.goodMapValue(taskInfo, "taskId");
                        },
                        getObjectTypeFromInfo: function(taskInfo) {
                            return (taskInfo.adhocTask) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                        },
                        initComponentLinks: function(config) {
                            $scope.taskLinks = HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.TASK);
                            $scope.adhocTaskLinks = HelperObjectBrowserService.createComponentLinks(config, ObjectService.ObjectTypes.ADHOC_TASK);
                            return (ObjectService.ObjectTypes.ADHOC_TASK == $stateParams.type) ? $scope.adhocTaskLinks : $scope.taskLinks;
                        },
                        selectComponentLinks: function(selectedTask) {
                            return (ObjectService.ObjectTypes.ADHOC_TASK == selectedTask.nodeType) ? $scope.adhocTaskLinks : $scope.taskLinks;
                        }
                    });

                } ]);