'use strict';

angular.module('cases').controller('Cases.SplitController', ['$scope', '$stateParams', '$modal', '$modalInstance'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $modal, $modalInstance
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, HelperObjectBrowserService) {

        $scope.modalInstance = $modalInstance;
        $scope.selectedItem = null;
        
        ConfigService.getComponentConfig("cases", "documents").then(function (componentConfig) {
            $scope.config = componentConfig;
            return componentConfig;
        });
        
        $scope.close = function() {
            $scope.modalInstance.dismiss('cancel');
        };
        $scope.splitCase = function() {
            $scope.selectedItem = HelperObjectBrowserService.getCurrentObjectId();
            
            var attachments = [];
            var selNodes = $scope.treeControl.getSelectedNodes();
            if (!Util.isEmptyArray(selNodes)) {
                for (var i = 0; i < selNodes.length; i++) {
                    if (DocTree.View.isFolderNode(selNodes[i])) {
                        attachments.push({"id": selNodes[i].data.objectId, "type": "folder"});
                    } else if (DocTree.View.isFileNode(selNodes[i])) {
                        attachments.push({"id": selNodes[i].data.objectId, "type": "document"});
                    }
                }
            }
            
            var summary = {};
            var attachments = this.getAttachments();
            summary.caseFileId =  $scope.selectedItem;
            summary.attachments = attachments;
            summary.preserveFolderStructure = true;
            
            $scope.modalInstance.close(summary);
        };
    
        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE).then(
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


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = $stateParams.id;

        var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
        if (Util.goodPositive(currentObjectId, false)) {
            CaseInfoService.getCaseInfo(currentObjectId).then(function (caseInfo) {
                $scope.caseInfo = caseInfo;
                $scope.objectId = caseInfo.id;
                return caseInfo;
            });
        }

	}
]);