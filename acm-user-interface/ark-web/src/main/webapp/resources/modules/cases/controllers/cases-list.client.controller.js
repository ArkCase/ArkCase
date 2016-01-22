'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Case.ListService', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ServCommService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, CaseListService, CaseInfoService, HelperObjectBrowserService, ServCommService) {

        //
        // Check to see if complaint page is shown as a result returned by Frevvo
        // Reset the tree cache so that new entry created by Frevvo can be shown.
        // This is a temporary solution until UI and backend communication is implemented
        //
        var topics = ["new-case", "edit-case", "change-case-status", "reinvestigate"];
        _.each(topics, function (topic) {
            var data = ServCommService.popRequest("frevvo", topic);
            if (data) {
                CaseListService.resetCasesTreeData();
                if ("change-case-status" == topic) {

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