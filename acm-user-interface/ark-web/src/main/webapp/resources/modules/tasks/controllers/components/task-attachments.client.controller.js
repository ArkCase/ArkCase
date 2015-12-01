'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ObjectService', 'Object.LookupService', 'Task.InfoService'
    , function ($scope, $stateParams, $modal, Util, ObjectService, ObjectLookupService, TaskInfoService) {

        $scope.$emit('req-component-config', 'attachments');
        $scope.$on('component-config', function (e, componentId, config) {
            if ('attachments' == componentId) {
                $scope.config = config;
            }
        });


        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.TASK;
        $scope.objectId = $stateParams.id;
        $scope.$on('task-updated', function (e, data) {
            if (TaskInfoService.validateTaskInfo(data)) {
                $scope.taskInfo = data;
            }
        });

    }
]);