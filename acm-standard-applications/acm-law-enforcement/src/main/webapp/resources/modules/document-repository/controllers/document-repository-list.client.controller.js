'use strict';

angular.module('document-repository').controller('DocumentRepositoryListController', ['$scope', '$state', '$stateParams'
    , '$translate', 'UtilService', 'ObjectService', 'Helper.ObjectBrowserService', 'MessageService'
    , "DocumentRepository.ListService", "DocumentRepository.InfoService"
    , function ($scope, $state, $stateParams, $translate, Util, ObjectService, HelperObjectBrowserService
        , MessageService, DocumentRepositoryListService, DocumentRepositoryInfoService) {

        var eventName = "object.inserted";
        $scope.$bus.subscribe(eventName, function (data) {
            MessageService.info(data.objectType + " with ID " + data.objectId + " was created.");
        });

        new HelperObjectBrowserService.Tree({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , resetTreeData: function () {
                return DocumentRepositoryListService.resetDocumentRepositoryTreeData();
            }
            , updateTreeData: function (start, n, sort, filters, query, nodeData) {
                return DocumentRepositoryListService.updateDocumentRepositoryTreeData(start, n, sort, filters, query, nodeData);
            }
            , getTreeData: function (start, n, sort, filters, query) {
                return DocumentRepositoryListService.queryDocumentRepositoryTreeData(start, n, sort, filters, query);
            }
            , getNodeData: function (documentRepositoryId) {
                return DocumentRepositoryInfoService.getDocumentRepositoryInfo(documentRepositoryId);
            }
            , makeTreeNode: function (documentRepositoryInfo) {
                return {
                    nodeId: Util.goodValue(documentRepositoryInfo.id, 0)
                    , nodeType: ObjectService.ObjectTypes.DOC_REPO
                    , nodeTitle: Util.goodValue(documentRepositoryInfo.name)
                    , nodeToolTip: Util.goodValue(documentRepositoryInfo.name)
                };
            }
        });
    }
]);