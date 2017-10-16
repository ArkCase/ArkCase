'use strict';

angular.module('document-repository').controller('DocumentRepositoryController', ['$scope', '$stateParams', '$state'
    , 'DocumentRepository.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, DocumentRepositoryInfoService, ObjectService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , resetObjectInfo: DocumentRepositoryInfoService.resetDocumentRepositoryInfo
            , getObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , updateObjectInfo: DocumentRepositoryInfoService.updateDocumentRepositoryInfo
            , getObjectTypeFromInfo: function () {
                return ObjectService.ObjectTypes.DOC_REPO;
            }
        });
    }
]);
