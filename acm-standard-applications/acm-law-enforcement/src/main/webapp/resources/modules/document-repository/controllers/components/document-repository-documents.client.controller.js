'use strict';

angular.module('document-repository').controller('DocumentRepository.DocumentsController', ['$scope', '$stateParams'
    , '$modal', '$q', '$timeout', 'UtilService', 'ObjectService', 'Object.LookupService'
    , 'DocumentRepository.InfoService', 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication'
    , 'PermissionsService', 'Object.ModelService', 'DocTreeExt.WebDAV', 'DocTreeExt.Checkin'
    , function ($scope, $stateParams, $modal, $q, $timeout, Util, ObjectService, ObjectLookupService
        , DocumentRepositoryInfoService, HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService
        , ObjectModelService, DocTreeExtWebDAV, DocTreeExtCheckin) {

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
            fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
        };

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "documents"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.DOC_REPO);
        var promiseFileTypes = ObjectLookupService.getFileTypes();
        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

            $q.all([promiseFormTypes, promiseFileTypes]).then(
                function (data) {
                    $scope.treeConfig.formTypes = data[0];
                    $scope.treeConfig.fileTypes = data[1];
                });
        };


        $scope.objectType = ObjectService.ObjectTypes.DOC_REPO;
        $scope.objectId = componentHelper.currentObjectId;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };

        $scope.onInitTree = function (treeControl) {
            $scope.treeControl = treeControl;
            DocTreeExtCheckin.handleCheckout(treeControl, $scope);
            DocTreeExtCheckin.handleCheckin(treeControl, $scope);
            DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
            DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, $scope);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

    }
]);