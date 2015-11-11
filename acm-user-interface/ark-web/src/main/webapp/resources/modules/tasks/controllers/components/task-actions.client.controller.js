'use strict';

angular.module('tasks').controller('Tasks.ActionsController', ['$scope', '$state', 'UtilService', 'CallTasksService',
    function ($scope, $state, Util, CallTasksService) {
        $scope.$emit('req-component-config', 'actions');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('actions' == componentId) {
                $scope.config = config;
            }
        });

        $scope.$on('task-retrieved', function (e, data) {
            $scope.taskInfo = data;
        });

        $scope.sign = function () {
            console.log('sign');
        };
        $scope.subscribe = function () {
            console.log('subscribe');
        };
        $scope.reject = function () {
            console.log('reject');
        };
        $scope.delete = function () {
            console.log('delete');
        };
        $scope.complete = function () {
            console.log('complete');
        };
        $scope.approve = function () {
            console.log('approve');
        };
        $scope.rework = function () {
            console.log('rework');
        };

    }
]);