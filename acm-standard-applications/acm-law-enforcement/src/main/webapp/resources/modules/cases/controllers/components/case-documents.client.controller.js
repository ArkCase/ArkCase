'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', '$q', '$timeout'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , 'DocTreeExt.WebDAV', 'DocTreeExt.Checkin', 'Admin.CMTemplatesService', 'DocTreeExt.Email'
    , function ($scope, $stateParams, $modal, $q, $timeout
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService
        , DocTreeExtWebDAV, DocTreeExtCheckin, CorrespondenceService, DocTreeExtEmail) {

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "documents"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE);
        var promiseFileTypes = ObjectLookupService.getFileTypes();
        var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.CASE_FILE);
        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

            $q.all([promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms]).then(
                function (data) {
                    $scope.treeConfig.formTypes = data[0];
                    $scope.treeConfig.fileTypes = data[1];
                    $scope.treeConfig.correspondenceForms = data[2];
                    $scope.treeControl.refreshTree();
                });
        };


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
            fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
        };

        $scope.onInitTree = function (treeControl) {
            $scope.treeControl = treeControl;
            DocTreeExtCheckin.handleCheckout(treeControl, $scope);
            DocTreeExtCheckin.handleCheckin(treeControl, $scope);
            DocTreeExtCheckin.handleCancelEditing(treeControl, $scope);
            DocTreeExtWebDAV.handleEditWithWebDAV(treeControl, $scope);

            //$scope.treeControl.addCommandHandler({
            //    name: "sample"
            //    , onAllowCmd: function(nodes) {
            //        return "disable";
            //    }
            //    , onPreCmd: function(nodes, args) {
            //        console.log("onPreCmd of sample command");
            //        return false;
            //    }
            //    , onPostCmd: function(nodes, args) {
            //        console.log("onPostCmd of sample command");
            //    }
            //    , execute: function(nodes, args) {
            //        console.log("Possible to add onPreCmd code here");
            //        var promise = this.prevHandler.execute(nodes, args);
            //        $q.when(promise).then(function () {
            //            console.log("Possible to add onPostCmd code here, too");
            //        });
            //        console.log("Possible to add onPostCmd code here");
            //    }
            //});
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.sendEmail = function() {
            var nodes = $scope.treeControl.getSelectedNodes();
            var DocTree = $scope.treeControl.getDocTreeObject();
            DocTreeExtEmail.openModal(DocTree, nodes);
        };

    }

]);
