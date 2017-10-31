'use strict';

angular.module('document-repository').controller('DocumentRepository.DocumentsController', ['$scope', '$stateParams'
    , '$modal', '$translate', '$q', '$timeout', 'UtilService', 'Config.LocaleService', 'ObjectService', 'Object.LookupService'
    , 'DocumentRepository.InfoService', 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication'
    , 'PermissionsService', 'Object.ModelService', 'DocTreeExt.WebDAV', 'DocTreeExt.Checkin', 'DocTreeExt.Email', 'ModalDialogService'
    , function ($scope, $stateParams, $modal, $translate, $q, $timeout, Util, LocaleService, ObjectService, ObjectLookupService
        , DocumentRepositoryInfoService, HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService
        , ObjectModelService, DocTreeExtWebDAV, DocTreeExtCheckin, DocTreeExtEmail, ModalDialogService) {

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
        var promiseFileLanguages = LocaleService.getSettings();
        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

            $q.all([promiseFormTypes, promiseFileTypes, promiseFileLanguages]).then(
                function (data) {
                    $scope.treeConfig.formTypes = data[0];
                    $scope.treeConfig.fileTypes=[];
                    for(var i = 0; i < data[1].length; i++){
                        $scope.treeConfig.fileTypes.push({"key":data[1][i].key, "value": $translate.instant(data[1][i].value)});
                    }
                    $scope.treeConfig.fileLanguages = data[2];
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

        $scope.sendEmail = function() {
            var nodes = $scope.treeControl.getSelectedNodes();
            var DocTree = $scope.treeControl.getDocTreeObject();
            DocTreeExtEmail.openModal(DocTree, nodes);
        };

        $scope.createNewTask = function() {
            var modalMetadata = {
                moduleName: 'tasks',
                templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                controllerName: 'Tasks.NewTaskController',
                params: {
                    selectedDocumentNodes: $scope.treeControl.getSelectedNodes()
                }
            };
            ModalDialogService.showModal(modalMetadata);
        };

        $scope.onFilter = function () {
            $scope.$bus.publish('onFilterDocTree', {filter: $scope.filter});
        };

        $scope.onSearch = function () {
            $scope.$bus.publish('onSearchDocTree', {searchFilter: $scope.searchFilter});
        };

        $scope.$bus.subscribe('removeSearchFilter', function () {
            $scope.searchFilter = null;
        });
    }
]);