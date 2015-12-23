'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService', 'Object.ModelService'
    , function ($scope, $stateParams, Util, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService) {

        ConfigService.getComponentConfig("tasks", "info").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });


        LookupService.getUsers().then(
            function (users) {
                var options = [];
                _.each(users, function (user) {
                    options.push({object_id_s: user.object_id_s, name: user.name});
                });
                $scope.assignableUsers = options;
                return users;
            }
        );

        ObjectLookupService.getPriorities().then(
            function (priorities) {
                var options = [];
                _.each(priorities, function (priority) {
                    options.push({value: priority, text: priority});
                });
                $scope.priorities = options;
                return priorities;
            }
        );


        $scope.$on('object-updated', function (e, data) {
            if (TaskInfoService.validateTaskInfo(data)) {
                $scope.taskInfo = data;
                $scope.assignee = ObjectModelService.getAssignee(data);
            }
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
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                TaskInfoService.saveTaskInfo(taskInfo).then(
                    function (taskInfo) {
                        //update tree node tittle
                        $scope.$emit("report-object-updated", taskInfo);
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