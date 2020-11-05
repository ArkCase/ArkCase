"use strict";

angular.module('cost-tracking').controller('CostTracking.TasksController', [ '$scope', '$stateParams', 'CostTracking.InfoService', 'ObjectService', 'Helper.TaskListParentNode', function($scope, $stateParams, CostTrackingInfoService, ObjectService, HelperTaskListParentNode) {

    new HelperTaskListParentNode.TaskTableComponent({
        scope: $scope,
        objectType: ObjectService.ObjectTypes.COSTSHEET,
        stateParams: $stateParams,
        moduleId: "cost-tracking",
        componentId: "tasks",
        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
        enableNewTaskButton: true
    }).commit();
} ]);