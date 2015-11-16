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


        $scope.$on('task-retrieved', function (e, data) {
            $scope.taskInfo = data;
        });


        $scope.updateTitle = function () {
            saveTask();
        };
        $scope.updatePercentage = function () {
            saveTask();
        };
        $scope.updateAssignee = function () {
            //var taskInfo = Util.omitNg($scope.taskInfo);
            //TasksModelsService.setAssignee($scope.taskInfo, $scope.assignee);
            saveTask();
        };
        $scope.updatePriority = function () {
            saveTask();
        };
        $scope.updateStartDate = function () {
            saveTask();
        };
        $scope.updateDueDate = function () {
            saveTask();
        };

        function saveTask() {
            var taskInfo = Util.omitNg($scope.taskInfo);
            if (CallTasksService.validateTaskInfo(taskInfo)) {
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
            }
        }
    }
]);