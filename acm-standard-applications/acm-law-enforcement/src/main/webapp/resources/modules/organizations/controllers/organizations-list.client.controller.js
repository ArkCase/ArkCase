'use strict';

angular.module('organizations').controller('OrganizationsListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Organization.ListService', 'Organization.InfoService', 'Helper.ObjectBrowserService'
    , 'ServCommService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, OrganizationListService, OrganizationInfoService, HelperObjectBrowserService
        , ServCommService, MessageService) {


        //"treeConfig", "treeData", "onLoad", and "onSelect" will be set by Tree Helper
        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "organizations"
            , resetTreeData: function () {
                return OrganizationListService.resetOrganizationsTreeData();
            }
            , updateTreeData: function (start, n, sort, filters, query, nodeData) {
                return OrganizationListService.updateOrganizationsTreeData(start, n, sort, filters, query, nodeData);
            }
            , getTreeData: function (start, n, sort, filters, query) {
                return OrganizationListService.queryOrganizationsTreeData(start, n, sort, filters, query);
            }
            , getNodeData: function (organizationId) {
                return OrganizationInfoService.getOrganizationInfo(organizationId);
            }
            , makeTreeNode: function (organizationInfo) {
                return {
                    nodeId: Util.goodValue(organizationInfo.organizationId, 0)
                    , nodeType: ObjectService.ObjectTypes.ORGANIZATION
                    , nodeTitle: Util.goodValue(organizationInfo.organizationValue)
                    , nodeToolTip: Util.goodValue(organizationInfo.organizationValue)
                };
            }
        });

    }
]);
