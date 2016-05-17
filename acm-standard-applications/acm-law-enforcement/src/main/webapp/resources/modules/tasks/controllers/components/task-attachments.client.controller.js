'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService', 'Helper.ObjectBrowserService', 'DocTreeService', 'PermissionsService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService, DocTreeService, PermissionsService) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "attachments"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });


        //ConfigService.getModuleConfig("tasks").then(function (config) {
        //    $scope.treeConfig = config.docTree;
        //    return config;
        //});
        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.treeConfig = config.docTree;
        };

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
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.taskId;
            PermissionsService.getActionPermission('editAttachments', objectInfo).then(function(result) {

                objectInfo.isReadOnly = !result;

            });
        };

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onInitTree = function(treeControl) {
            $scope.treeControl = treeControl;
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.onAllowCmd = function (cmd, nodes) {
            if (cmd === 'remove' && nodes[0].children.length !== 0) {
                return 'disable';
            } else {
                return $scope.objectInfo.isReadOnly ? 'disable' : '';
            }
        };

        $scope.onPreCmd = function (cmd, nodes) {
        };

        $scope.onPostCmd = function (cmd, nodes) {
        };

    }
]);