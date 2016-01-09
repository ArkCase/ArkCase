'use strict';

angular.module('tasks').controller('Tasks.ReworkDetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Task.InfoService', 'MessageService'
    , function ($scope, $stateParams, $translate, Util, ConfigService, TaskInfoService, MessageService) {

        ConfigService.getComponentConfig("tasks", "reworkdetails").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
            $scope.taskInfo = taskInfo;
            return taskInfo;
        });

        $scope.options = {
            focus: true
            //,height: 120
        };

        //$scope.editDetails = function() {
        //    $scope.editor.summernote({focus: true});
        //}
        $scope.saveDetails = function () {
            //$scope.editor.destroy();
            var taskInfo = Util.omitNg($scope.taskInfo);
            TaskInfoService.saveTaskInfo(taskInfo).then(
                function (taskInfo) {
                    $scope.$emit("report-object-updated", taskInfo);
                    MessageService.info($translate.instant("tasks.comp.details.informSaved"));
                    return taskInfo;
                }
            );
        };
    }
]);