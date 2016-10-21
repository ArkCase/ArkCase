'use strict';

angular.module('tasks').controller('Tasks.InfoController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'Util.DateService', 'ConfigService', 'LookupService', 'Object.LookupService', 'Task.InfoService', 'Object.ModelService'
    , 'Helper.ObjectBrowserService', 'MessageService'
    , function ($scope, $stateParams, $translate, $timeout
        , Util, UtilDateService, ConfigService, LookupService, ObjectLookupService, TaskInfoService, ObjectModelService
        , HelperObjectBrowserService, MessageService) {

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

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.dateInfo = $scope.dateInfo || {};
            $scope.dateInfo.dueDate = UtilDateService.isoToDate($scope.objectInfo.dueDate);
            $scope.dateInfo.taskStartDate = UtilDateService.isoToDate($scope.objectInfo.taskStartDate);
            $scope.assignee = ObjectModelService.getAssignee($scope.objectInfo); 
            console.log("**onObjectInfoRetrieved**");
            
            console.log("--$scope.objectInfo--");
            console.log($scope.objectInfo);
            
            console.log("--$scope.assignee--");
            console.log($scope.assignee);
        };

        $scope.defaultDatePickerFormat = UtilDateService.defaultDatePickerFormat;
        $scope.picker = {opened: false};
        $scope.onPickerClick = function () {
        	$scope.picker.opened = true;
        };

        $scope.validatePercentComplete = function (value) {
            if (value < 0 || value > 100) {
                return "Invalid value";
            }
        };

        $scope.saveTask = function () {
            saveTask();
        };
        $scope.updateAssignee = function (assignee) {
            //var taskInfo = Util.omitNg($scope.objectInfo);
            //TasksModelsService.setAssignee($scope.objectInfo, $scope.assignee);
            $scope.objectInfo.assignee = assignee;
            console.log("**updateAssignee**");
            console.log("--assignee--");
            console.log(assignee);
            saveTask();
        };
        $scope.updateStartDate = function (taskStartDate) {
            $scope.objectInfo.taskStartDate = UtilDateService.dateToIso($scope.dateInfo.taskStartDate);
            console.log("**updateStartDate**");
            
            console.log("--taskStartDate--");
            console.log(taskStartDate);

            console.log("--$scope.objectInfo.taskStartDate--");
            console.log($scope.objectInfo.taskStartDate);

            saveTask();
        };
        $scope.updateDueDate = function (dueDate) {
            $scope.objectInfo.dueDate = UtilDateService.dateToIso($scope.dateInfo.dueDate);
            saveTask();
        };
        $scope.$on('accessDenied', function(event, message){
            MessageService.info(message);
        });

        function saveTask() {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            console.log("**saveTask**");
            
            if (TaskInfoService.validateTaskInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                console.log("--objectInfo---");
                console.log(objectInfo);
                
                promiseSaveInfo = TaskInfoService.saveTaskInfo(objectInfo);
                promiseSaveInfo.then(
                    function (taskInfo) {
                        $scope.$emit("report-object-updated", taskInfo);
                        return taskInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
            }
            return promiseSaveInfo;
        }

    }
]);