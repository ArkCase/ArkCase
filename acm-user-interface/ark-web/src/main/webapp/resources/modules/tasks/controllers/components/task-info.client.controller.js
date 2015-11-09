'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'TasksService', 'TasksModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, LookupService, TasksService, TasksModelsService) {
        return;
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });


        // Obtains the dropdown menu selection options via REST calls to ArkTask
        $scope.priorities = [];
        var cachePriorities = new Store.SessionData(Helper.SessionCacheNames.PRIORITIES);
        var priorities = cachePriorities.get();
        Util.serviceCall({
            service: LookupService.getPriorities
            , result: priorities
            , onSuccess: function (data) {
                if (Validator.validatePriorities(data)) {
                    priorities = data;
                    cachePriorities.set(priorities);
                    return priorities;
                }
            }
        }).then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );
        var z = 1;
        return;

        $scope.assignableUsers = [];
        var cacheUsers = new Store.SessionData(Helper.SessionCacheNames.USERS);
        var users = cacheUsers.get();
        Util.serviceCall({
            service: LookupService.getUsers
            , result: users
            , onSuccess: function (data) {
                if (Validator.validateUsers(data)) {
                    users = data;
                    cacheUsers.set(users);
                    return users;
                }
            }
        }).then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    var userInfo = JSON.parse(user);
                    options.push({value: userInfo.object_id_s, text: userInfo.object_id_s});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        $scope.owningGroups = [];
        var cacheGroups = new Store.SessionData(Helper.SessionCacheNames.GROUPS);
        var groups = cacheGroups.get();
        Util.serviceCall({
            service: LookupService.getGroups
            , result: groups
            , onSuccess: function (data) {
                if (Validator.validateSolrData(data)) {
                    var groups = data.response.docs;
                    cacheGroups.set(groups);
                    return groups;
                }
            }
        }).then(
            function (groups) {
                var options = [];
                _.each(groups, function (item) {
                    options.push({value: item.name, text: item.name});
                });
                $scope.owningGroups = options;
                return groups;
            }
        );

        $scope.taskTypes = [];
        var cacheTaskTypes = new Store.SessionData(Helper.SessionCacheNames.CASE_TYPES);
        var taskTypes = cacheTaskTypes.get();
        Util.serviceCall({
            service: LookupService.getTaskTypes
            , result: taskTypes
            , onSuccess: function (data) {
                if (Validator.validateTaskTypes(data)) {
                    taskTypes = data;
                    cacheTaskTypes.set(taskTypes);
                    return taskTypes;
                }
            }
        }).then(
            function (taskTypes) {
                var options = [];
                _.forEach(taskTypes, function (item) {
                    options.push({value: item, text: item});
                });
                $scope.taskTypes = options;
                return taskTypes;
            }
        );

        $scope.taskSolr = null;
        $scope.taskInfo = null;
        $scope.$on('task-selected', function onSelectedTask(e, selectedTask) {
            $scope.taskSolr = selectedTask;
        });
        $scope.assignee = null;
        $scope.owningGroup = null;
        $scope.$on('task-retrieved', function (e, data) {
            if (Validator.validateTask(data)) {
                $scope.taskInfo = data;
                $scope.assignee = TasksModelsService.getAssignee(data);
                $scope.owningGroup = TasksModelsService.getGroup(data);
            }
        });

        /**
         * Persists the updated task metadata to the ArkTask database
         */
        function saveTask() {
            if (Validator.validateTask($scope.taskInfo)) {
                var taskInfo = Util.omitNg($scope.taskInfo);
                Util.serviceCall({
                    service: TasksService.save
                    , data: taskInfo
                    , onSuccess: function (data) {
                        return data;
                    }
                }).then(
                    function (successData) {
                        //notify "task saved successfully" ?
                    }
                    , function (errorData) {
                        //handle error
                    }
                );
            }

            //var taskInfo = Util.omitNg($scope.taskInfo);
            //TasksService.save({}, taskInfo
            //    ,function(successData) {
            //        $log.debug("task saved successfully");
            //    }
            //    ,function(errorData) {
            //        $log.error("task save failed");
            //    }
            //);
        }

        // Updates the ArkTask database when the user changes a task attribute
        // in a task top bar menu item and clicks the save check button
        $scope.updateTitle = function () {
            saveTask();
        };
        $scope.updateOwningGroup = function () {
            TasksModelsService.setGroup($scope.taskInfo, $scope.owningGroup);
            saveTask();
        };
        $scope.updatePriority = function () {
            saveTask();
        };
        $scope.updateTaskType = function () {
            saveTask();
        };
        $scope.updateAssignee = function () {
            TasksModelsService.setAssignee($scope.taskInfo, $scope.assignee);
            saveTask();
        };
        $scope.updateDueDate = function () {
            saveTask();
        };

    }
]);