'use strict';

angular.module('tasks').controller('Tasks.AttachmentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Task.InfoService', 'Helper.ObjectBrowserService'
    , 'Authentication', 'DocTreeService', 'PermissionsService', 'DocTreeExt.Core', 'DocTreeExt.Checkin'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, TaskInfoService, HelperObjectBrowserService
        , Authentication, DocTreeService, PermissionsService, DocTreeExtCore, DocTreeExtCheckin) {

		Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );
	
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

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.treeConfig = config.docTree;
        };

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
            DocTreeExtCheckin.handleCheckout(treeControl, $scope);
            DocTreeExtCheckin.handleCheckin(treeControl, $scope);
            DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
            DocTreeExtCore.handleEditWithWebDAV(treeControl, $scope);
            
            treeControl.addCommandHandler({
                name: "remove"
                , onAllowCmd: function(nodes) {
                    var len = 0;
                    if (Util.isArray(nodes[0].children)) {
                        len = nodes[0].children.length;
                    }
                    if (0 != len) {
                        return 'disable';
                    } else {
                        return $scope.objectInfo.isReadOnly ? 'disable' : '';
                    }
                }
            });
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.correspondenceForms = {};
    }
]);