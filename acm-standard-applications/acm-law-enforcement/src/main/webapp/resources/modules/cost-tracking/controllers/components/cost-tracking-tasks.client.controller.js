"use strict";

angular.module('cost-tracking').controller(
        'CostTracking.TasksController',
        [
                '$scope',
                '$controller',
                '$stateParams',
                'Helper.ObjectBrowserService',
                'CostTracking.InfoService',
                'Helper.UiGridService',
                'Object.TaskService',
                'ObjectService',
                'Helper.TaskListParentNode',
                function($scope, $controller, $stateParams, HelperObjectBrowserService, CostTrackingInfoService, HelperUiGridService,
                        ObjectTaskService, ObjectService, HelperTaskListParentNode) {

                    new HelperTaskListParentNode.TaskTableComponent({
                        scope : $scope,
                        objectType : ObjectService.ObjectTypes.COSTSHEET,
                        stateParams : $stateParams,
                        moduleId : "cost-tracking",
                        componentId : "tasks",
                        retrieveObjectInfo : CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo : CostTrackingInfoService.validateCostsheet
                    }).commit();
                } ]);