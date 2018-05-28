'use strict';

angular.module('cost-tracking').controller('CostTracking.DocumentsController', [ '$scope', '$stateParams', 'CostTracking.InfoService', 'ObjectService', 'Helper.DocumentListTreeHelper', function($scope, $stateParams, CostTrackingInfoService, ObjectService, HelperDocumentListTreeHelper) {

    var documentTreeComponent = new HelperDocumentListTreeHelper.DocumentTreeComponent({
        scope: $scope,
        stateParams: $stateParams,
        objectType: ObjectService.ObjectTypes.COSTSHEET,
        moduleId: "cost-tracking",
        componentId: "documents",
        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
        enableSendEmailButton: true
    });
    // documentTreeComponent.enableSendEmailButton();
    documentTreeComponent.enableNewTaskButton({
        parentId: $scope.objectId
    });
    documentTreeComponent.commit();

} ]);