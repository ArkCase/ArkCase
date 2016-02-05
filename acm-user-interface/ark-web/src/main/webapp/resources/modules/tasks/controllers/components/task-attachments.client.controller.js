'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService', 'Helper.ObjectBrowserService', 'DocTreeService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService, DocTreeService) {

        new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "attachments"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onObjectInfoRetrieved: function (taskInfo) {
                $scope.taskInfo = taskInfo;
            }
        });


        ConfigService.getModuleConfig("tasks").then(function (config) {
            $scope.treeConfig = config.docTree;
            return config;
        });

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.TASK).then(
            function (formTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                return formTypes;
            }
        );

        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );

        $scope.objectType = ObjectService.ObjectTypes.TASK;
        $scope.objectId = $stateParams.id;
        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            TaskInfoService.getTaskInfo(currentObjectId).then(function (taskInfo) {
                $scope.taskInfo = taskInfo;
                $scope.objectInfo = taskInfo;
                $scope.objectId = taskInfo.taskId;
                return taskInfo;
            });
        }

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.taskInfo, $scope.fileTypes);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

    }
]);