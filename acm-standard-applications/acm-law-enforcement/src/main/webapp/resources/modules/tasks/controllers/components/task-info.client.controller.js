'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams'
    , 'UtilService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService', 'Object.ModelService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams
        , Util, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "tasks"
            , componentId: "info"
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
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

        $scope.dueDate = null;
        $scope.taskStartDate = null;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.dueDate = ($scope.objectInfo.dueDate) ? moment($scope.objectInfo.dueDate).toDate() : null;
            $scope.taskStartDate = ($scope.objectInfo.taskStartDate) ? moment($scope.objectInfo.taskStartDate).toDate() : null;
            $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo);
        };

        $scope.validatePercentComplete = function(value) {
            if(value < 0 || value > 100) {
                return "Invalid value";
            }
        };

        $scope.updateTitle = function () {
            saveTask();
        };
        $scope.updatePercentage = function () {
            saveTask();
        };
        $scope.updateAssignee = function () {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //TasksModelsService.setAssignee($scope.objectInfo, $scope.assignee);
            saveTask();
        };
        $scope.updatePriority = function () {
            saveTask();
        };
        $scope.updateStartDate = function (taskStartDate) {
            $scope.objectInfo.taskStartDate = (taskStartDate) ? moment(taskStartDate).format($scope.config.dateFormat): null;
            saveTask();
        };
        $scope.updateDueDate = function (dueDate) {
            $scope.objectInfo.dueDate = (dueDate) ? moment(dueDate).format($scope.config.dateFormat): null;
            saveTask();
        };

        function saveTask() {
            var taskInfo = Util.omitNg($scope.objectInfo);
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