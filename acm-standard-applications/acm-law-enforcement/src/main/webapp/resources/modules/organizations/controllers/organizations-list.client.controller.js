'use strict';

angular.module('organizations').controller('OrganizationsListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ObjectService', 'Organization.ListService', 'Organization.InfoService', 'Helper.ObjectBrowserService'
    , 'ServCommService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ObjectService, OrganizationListService, OrganizationInfoService, HelperObjectBrowserService
        , ServCommService, MessageService) {

        // maybe optional listener for "close-complaint"?
        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            if (data.objectType === ObjectService.ObjectTypes.ORGANIZATION) {

                var objectTypeString = $translate.instant('common.objectTypes.' + data.objectType);
                var objectWasCreatedMessage = $translate.instant('common.objects.objectWasCreatedMessage ', {
                    objectTypeString: objectTypeString,
                    objectId: data.objectId
                });

                MessageService.info(objectWasCreatedMessage);
            }
        });

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
            , makeTreeNode: function (organizationnInfo) {
                return {
                    nodeId: Util.goodValue(organizationnInfo.organizationId, 0)
                    , nodeType: ObjectService.ObjectTypes.ORGANIZATION
                    , nodeTitle: Util.goodValue(organizationnInfo.organizationTitle)
                    , nodeToolTip: Util.goodValue(organizationnInfo.organizationTitle)
                };
            }
        });

    }
]);
