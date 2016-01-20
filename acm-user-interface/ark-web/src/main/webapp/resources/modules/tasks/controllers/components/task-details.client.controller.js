'use strict';

angular.module('tasks').controller('Tasks.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'Task.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, TaskInfoService, MessageService, HelperObjectBrowserService) {

        ConfigService.getComponentConfig("tasks", "actions").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TaskInfoService.getTaskInfo(currentObjectId).then(function (taskInfo) {
                $scope.taskInfo = taskInfo;
                return taskInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, taskInfo) {
            $scope.taskInfo = taskInfo;
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