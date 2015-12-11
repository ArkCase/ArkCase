'use strict';

angular.module('cases').controller('CasesListController', ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ObjectService', 'Case.ListService', 'Case.InfoService', 'ConfigService', 'Helper.ObjectTreeService',
    function ($scope, $state, $stateParams, $translate, Util, ObjectService, CaseListService, CaseInfoService, ConfigService, HelperObjectTreeService) {
        ConfigService.getModuleConfig("cases").then(function (config) {
            $scope.treeConfig = config.tree;
            $scope.componentsConfig = config.components;
            return config;
        });

        var treeHelper = new HelperObjectTreeService.Tree({
            scope: $scope
            , nodeId: $stateParams.id
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
        $scope.onLoad = function (start, n, sort, filters) {
            treeHelper.onLoad(start, n, sort, filters);
        };

        $scope.onSelect = function (selectedCase) {
            $scope.$emit('req-select-case', selectedCase);
            var components = Util.goodArray(selectedCase.components);
            var componentType = (1 == components.length) ? components[0] : "main";
            $state.go('cases.' + componentType, {
                id: selectedCase.nodeId
            });
        };
    }
]);