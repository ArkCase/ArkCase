'use strict';

angular.module('cases').controller('Common.MultiCorrespondenceModalController', [ '$scope', '$modalInstance', 'ObjectService', 'Admin.CMTemplatesService', function($scope, $modalInstance, ObjectService, CorrespondenceService) {

    $scope.correspondenceTemplates = [];
    $scope.selectedTemplates = [];
    $scope.modalResult = {};

    CorrespondenceService.getActivatedTemplatesData(ObjectService.ObjectTypes.CASE_FILE).then(function (result){
        $scope.correspondenceTemplates = result.data;
        for(var i=0; i<$scope.correspondenceTemplates.length; i++){
            if($scope.correspondenceTemplates[i].documentType === "Multi Correspondence"){
                $scope.correspondenceTemplates.splice(i, 1);
            }
        }
    });

    $scope.templateExist = function(templateId) {
            var result = {
                found: false,
                index: -1
            };
            for(var i=0; i<$scope.selectedTemplates.length; i++){
                if($scope.selectedTemplates[i].templateId === templateId){
                    result.found = true;
                    result.index = i;
                    break;
                }
            }
            return result;
    };

    $scope.onTemplateSelected = function(template) {
        if (template) {
            var templateFound = $scope.templateExist(template.templateId);
            if(templateFound.found == true){
                $scope.selectedTemplates.splice(templateFound.index, 1);
            }
            else {
                $scope.selectedTemplates.push(template);
            }
        }
    };

    $scope.onClickOk = function() {
        $scope.modalResult.selectedTemplates = $scope.selectedTemplates;
        $scope.modalResult.multiCorrespondenceDocumentName = $scope.multiCorrespondenceDocumentName;

        $modalInstance.close($scope.modalResult);
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);