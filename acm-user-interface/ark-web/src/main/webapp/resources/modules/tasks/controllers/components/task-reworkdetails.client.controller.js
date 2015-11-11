'use strict';

angular.module('tasks').controller('Tasks.ReworkDetailsController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'TasksService',
    function ($scope, $stateParams, Util, Validator, TasksService) {
        return;
        $scope.$emit('req-component-config', 'reworkdetails');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('reworkdetails' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            if (Validator.validateTask(data)) {
                $scope.taskInfo = data;
            }
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
            Util.serviceCall({
                service: TasksService.save
                , data: taskInfo
            });
        };
    }
]);