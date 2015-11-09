'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', 'ConfigService', 'TasksService', 'UtilService', 'ValidationService',
    function ($scope, $state, ConfigService, TasksService, Util, Validator) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            if (Validator.validateTask(data)) {
                $scope.taskInfo = data;
            }
        });

        // Displays the create new task Frevvo form for the user
        $scope.loadNewTaskFrevvoForm = function () {
            $state.go('wizard');
        };

        // Displays the change task status Frevvo form for the user
        $scope.loadChangeTaskStatusFrevvoForm = function (taskInfo) {
            if (taskInfo && taskInfo.id && taskInfo.taskNumber && taskInfo.status) {
                $state.go('status', {id: taskInfo.id, taskNumber: taskInfo.taskNumber, status: taskInfo.status});
            }
        };
    }
]);