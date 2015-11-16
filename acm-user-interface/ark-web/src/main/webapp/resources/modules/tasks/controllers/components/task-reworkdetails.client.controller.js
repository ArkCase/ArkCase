'use strict';

angular.module('tasks').controller('Tasks.ReworkDetailsController', ['$scope', '$stateParams', '$translate', 'UtilService', 'CallTasksService', 'MessageService',
    function ($scope, $stateParams, $translate, Util, CallTasksService, MessageService) {
        $scope.$emit('req-component-config', 'reworkdetails');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('reworkdetails' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            $scope.taskInfo = data;
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
            CallTasksService.saveTaskInfo(taskInfo).then(
                function (taskInfo) {
                    MessageService.info($translate.instant("tasks.comp.details.informSaved"));
                    return taskInfo;
                }
            );
        };
    }
]);