'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', '$q', '$timeout'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication', 'PermissionsService', 'Object.ModelService', 'DocTreeExt.Core'
    , function ($scope, $stateParams, $modal, $q, $timeout
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService, DocTreeExtCore) {

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


        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE).then(
            function (formTypes) {
                $timeout(function() {
                    $scope.fileTypes = $scope.fileTypes || [];
                    $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                }, 0);
                return formTypes;
            }
        );
        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $timeout(function() {
                    $scope.fileTypes = $scope.fileTypes || [];
                    $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                }, 0);
                return fileTypes;
            }
        );

        ObjectLookupService.getCaseFileCorrespondenceForms().then(
            function (correspondenceForms) {
                $timeout(function() {
                    $scope.correspondenceForms = Util.goodArray(correspondenceForms);
                }, 0);
                return correspondenceForms;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onInitTree = function(treeControl) {
            $scope.treeControl = treeControl;
            DocTreeExtCore.handleCheckout(treeControl, $scope);
            DocTreeExtCore.handleCheckin(treeControl, $scope);
            DocTreeExtCore.handleEditWithWord(treeControl, $scope);
            DocTreeExtCore.handleCancelEditing(treeControl, $scope);

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

            //if there is subscription from other object we want to unsubscribe
            //we want to have only one subscription from the current object
            if ($scope.subscription) {
                $scope.$bus.unsubscribe($scope.subscription);
            }
            var eventName = "object.changed/" + $scope.objectType + "/" + $scope.objectId;
            $scope.subscription = $scope.$bus.subscribe(eventName, function (data) {
                if (data.objectType == 'FILE') {
                    $scope.treeControl.refreshTree();
                }
            });
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

    }

]);
