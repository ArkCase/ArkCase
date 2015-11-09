'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'TasksService', 'CallLookupService', 'CallTasksService', 'TasksModelsService',
    function ($scope, $stateParams, Store, Util, Validator, Helper, LookupService, TasksService, CallLookupService, CallTasksService, TasksModelsService) {
        $scope.$emit('req-component-config', 'info');
        $scope.$on('component-config', function (e, componentId, config) {
            if ("info" == componentId) {
                $scope.config = config;
            }
        });


        CallLookupService.getUsers().then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    options.push({object_id_s: user.object_id_s, name: user.name});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        CallLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );


        $scope.updateTitle = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            CallTasksService.saveTaskInfo(taskInfo).then(
                function (taskInfo) {
                    //update tree node tittle
                    return taskInfo;
                }
                , function (error) {
                    //set error to x-editable title
                    //update tree node tittle
                    return error;
                }
            );
        };
        $scope.updatePercentage = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            console.log("updatePercentage");
        };
        $scope.updateAssignee = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            console.log("updateAssignee");
            //TasksModelsService.setAssignee($scope.taskInfo, $scope.assignee);
            //saveTask();
        };
        $scope.updatePriority = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            console.log("updatePriority");
            //saveTask();
        };
        $scope.updateStartDate = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            console.log("updateStartDate");
            //saveTask();
        };
        $scope.updateDueDate = function () {
            var taskInfo = Util.omitNg($scope.taskInfo);
            console.log("updateDueDate");
            //saveTask();
        };
        return;


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
        $scope.updateOwningGroup = function () {
            TasksModelsService.setGroup($scope.taskInfo, $scope.owningGroup);
            saveTask();
        };

    }
]);