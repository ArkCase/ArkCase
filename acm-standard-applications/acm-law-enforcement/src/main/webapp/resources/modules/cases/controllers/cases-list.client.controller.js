'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Case.ListService', 'Case.InfoService', 'Helper.ObjectBrowserService'
    , 'ServCommService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, CaseListService, CaseInfoService, HelperObjectBrowserService
        , ServCommService, MessageService) {

        /*//
         // Check to see if complaint page is shown as a result returned by Frevvo
         // Reset the tree cache so that new entry created by Frevvo can be shown.
         // This is a temporary solution until UI and backend communication is implemented
         //
         var topics = ["new-case", "edit-case", "change-case-status", "reinvestigate"];
         _.each(topics, function (topic) {
         var data = ServCommService.popRequest("frevvo", topic);
         if (data) {
         CaseListService.resetCasesTreeData();
         }
         });*/
        //we will ignore previous implementation with ServCommService
        //but we will use it to see if the changes were made from current user and apply something different

        //one solution is to wait for object.inserted messages
        //when we will get a callback for them we will check the ServCommService if it is current user
        //subscribe to the bus for the object
        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            if (data.objectType === ObjectService.ObjectTypes.CASE_FILE) {
                var frevvoRequest = ServCommService.popRequest("frevvo", "new-case");
                if (frevvoRequest) {
                    ObjectService.gotoUrl(ObjectService.ObjectTypes.CASE_FILE, data.objectId);
                }
                else {
                    var objectTypeString = $translate.instant('common.objectTypes.' + data.objectType);
                    if (!objectTypeString) {
                        objectTypeString = data.objectType;
                    }
                    MessageService.info(objectTypeString + " with ID " + data.objectId + " was created.");
                }
            }
        });


        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cases"
            , resetTreeData: function () {
                return CaseListService.resetCasesTreeData();
            }
            , updateTreeData: function (start, n, sort, filters, query, nodeData) {
                return CaseListService.updateCasesTreeData(start, n, sort, filters, query, nodeData);
            }
            , getTreeData: function (start, n, sort, filters, query) {
                return CaseListService.queryCasesTreeData(start, n, sort, filters, query);
            }
            , getNodeData: function (caseId) {
                return CaseInfoService.getCaseInfo(caseId);
            }
            , makeTreeNode: function (caseInfo) {
                return {
                    nodeId: Util.goodValue(caseInfo.id, 0)
                    , nodeType: ObjectService.ObjectTypes.CASE_FILE
                    , nodeTitle: Util.goodValue(caseInfo.title)
                    , nodeToolTip: Util.goodValue(caseInfo.title)
                };
            }
        });

    }
]);