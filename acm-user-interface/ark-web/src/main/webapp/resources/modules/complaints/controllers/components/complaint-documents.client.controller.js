'use strict';

angular.module('complaints').controller('Complaints.DocumentsController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Complaint.InfoService'
    , 'Helper.ObjectBrowserService', 'DocTreeService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, ObjectService, ObjectLookupService, ComplaintInfoService
        , HelperObjectBrowserService, DocTreeService) {

        ConfigService.getComponentConfig("complaints", "documents").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });

        ConfigService.getModuleConfig("complaints").then(function (config) {
            $scope.treeConfig = config.docTree;
            return config;
        });

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
        $scope.objectId = $stateParams.id;

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            ComplaintInfoService.getComplaintInfo(currentObjectId).then(function (complaintInfo) {
                $scope.complaintInfo = complaintInfo;
                $scope.objectInfo = complaintInfo;
                $scope.objectId = complaintInfo.complaintId;
                return complaintInfo;
            });
        }

        $scope.$on('object-refreshed', function (e, complaintInfo) {
            $scope.complaintInfo = complaintInfo;
            $scope.objectId = complaintInfo.complaintId;
        });

        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.complaintInfo, $scope.fileTypes);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

    }
]);