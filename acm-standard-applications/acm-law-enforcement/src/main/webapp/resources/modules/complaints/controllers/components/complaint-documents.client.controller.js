'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal', '$q', '$timeout', '$translate'
    , 'UtilService', 'Config.LocaleService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , 'DocTreeExt.WebDAV', 'DocTreeExt.Checkin', 'Admin.CMTemplatesService', 'DocTreeExt.Email', 'Admin.EmailSenderConfigurationService'
    , function ($scope, $stateParams, $modal, $q, $timeout, $translate
        , Util, LocaleService, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService, Authentication, PermissionsService, ObjectModelService
        , DocTreeExtWebDAV, DocTreeExtCheckin, CorrespondenceService, DocTreeExtEmail, EmailSenderConfigurationService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        EmailSenderConfigurationService.getEmailSenderConfiguration().then(function (emailData) {
            $scope.sendEmailEnabled = emailData.data.allowDocuments;
        });

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            var fileTypes = Util.goodArray($scope.treeConfig.fileTypes);
            fileTypes = fileTypes.concat(Util.goodArray($scope.treeConfig.formTypes));
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, fileTypes);
        };

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "documents"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.COMPLAINT);
        var promiseFileTypes = ObjectLookupService.getFileTypes();
        var promiseCorrespondenceForms = CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.COMPLAINT);
        var promiseFileLanguages = LocaleService.getSettings();

        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
            $scope.allowParentOwnerToCancel = config.docTree.allowParentOwnerToCancel;

            $q.all([promiseFormTypes, promiseFileTypes, promiseCorrespondenceForms, promiseFileLanguages]).then(
                function (data) {
                    $scope.treeConfig.formTypes = data[0];
                    $scope.treeConfig.fileTypes=[];
                    for(var i = 0 ; i < data[1].length; i++){
                        $scope.treeConfig.fileTypes.push({"key": data[1][i].key, "value": $translate.instant(data[1][i].value)});
                    }
                    $scope.treeConfig.correspondenceForms = data[2];
                    $scope.treeConfig.fileLanguages = data[3];
                    $scope.treeControl.refreshTree();
                });
        };


        $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.complaintId;
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