'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService) {

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

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            $scope.treeConfig = config.docTree;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.COMPLAINT).then(
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


        $scope.objectType = ObjectService.ObjectTypes.COMPLAINT;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.complaintId;
        };

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.onAllowCmd = function (cmd) {
        };

        $scope.onPreCmd = function (cmd, nodes) {
        };

        $scope.onPostCmd = function (cmd, nodes) {
        };
    }
]);