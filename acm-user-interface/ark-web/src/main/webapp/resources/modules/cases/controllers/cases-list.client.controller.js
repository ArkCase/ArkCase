'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Case.ListService', 'Case.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, CaseListService, CaseInfoService, HelperObjectBrowserService) {


        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "cases"
            , getTreeData: function (start, n, sort, filters) {
                return CaseListService.queryCasesTreeData(start, n, sort, filters);
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