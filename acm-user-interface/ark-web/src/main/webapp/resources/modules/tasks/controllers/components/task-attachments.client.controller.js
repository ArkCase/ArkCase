'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService'
    , function ($scope, $stateParams, $modal, Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService) {

        ConfigService.getComponentConfig("tasks", "attachments").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );

        $scope.objectType = $stateParams.type; //ObjectService.ObjectTypes.TASK;
        $scope.objectId = $stateParams.id;
        TaskInfoService.getTaskInfo($stateParams.id).then(function (taskInfo) {
            $scope.taskInfo = taskInfo;
            return taskInfo;
        });

    }
]);