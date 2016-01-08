'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService) {

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

        $scope.objectType = $stateParams.type;
        $scope.objectId = $stateParams.id;
        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TaskInfoService.getTaskInfo(currentObjectId).then(function (taskInfo) {
                $scope.taskInfo = taskInfo;
                $scope.objectType = (taskInfo.adhocTask) ? ObjectService.ObjectTypes.ADHOC_TASK : ObjectService.ObjectTypes.TASK;
                $scope.objectId = taskInfo.taskId;
                return taskInfo;
            });
        }

    }
]);