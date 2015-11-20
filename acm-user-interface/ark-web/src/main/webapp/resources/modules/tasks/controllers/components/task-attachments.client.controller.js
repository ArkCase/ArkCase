'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal', 'UtilService', 'ConstantService', 'Object.LookupService',
    function ($scope, $stateParams, $modal, Util, Constant, ObjectLookupService) {
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


        $scope.objectType = Constant.ObjectTypes.TASK;
        $scope.objectId = $stateParams.id;
        $scope.$on('task-updated', function (e, data) {
            $scope.taskInfo = data;
        });

    }
]);