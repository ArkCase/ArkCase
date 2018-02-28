'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.DocumentsController',
        [
                '$scope',
                '$controller',
                '$stateParams',
                '$q',
                'UtilService',
                'Helper.ObjectBrowserService',
                'CostTracking.InfoService',
                'Helper.UiGridService',
                'Object.TaskService',
                'ObjectService',
                'Admin.EmailSenderConfigurationService',
                'Object.LookupService',
                'Config.LocaleService',
                'Admin.CMTemplatesService',
                'Object.ModelService',
                'DocTreeExt.WebDAV',
                'DocTreeExt.Checkin',
                'Helper.DocumentListTreeHelper',
                function($scope, $controller, $stateParams, $q, Util, HelperObjectBrowserService, CostTrackingInfoService,
                        HelperUiGridService, ObjectTaskService, ObjectService, EmailSenderConfigurationService, ObjectLookupService,
                        LocaleService, CorrespondenceService, ObjectModelService, DocTreeExtWebDAV, DocTreeExtCheckin,
                        HelperDocumentListTreeHelper) {

                    var documentTreeComponent = new HelperDocumentListTreeHelper.DocumentTreeComponent({
                        scope : $scope,
                        stateParams : $stateParams,
                        objectType : ObjectService.ObjectTypes.COSTSHEET,
                        moduleId : "cost-tracking",
                        componentId : "documents",
                        retrieveObjectInfo : CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo : CostTrackingInfoService.validateCostsheet,
                        enableSendEmailButton : true
                    });
                    // documentTreeComponent.enableSendEmailButton();
                    documentTreeComponent.enableNewTaskButton({
                        parentId : $scope.objectId
                    });
                    documentTreeComponent.commit();

                } ]);