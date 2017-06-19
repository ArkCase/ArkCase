'use strict';

angular.module('my-documents').controller('MyDocumentsController', ['$scope', '$stateParams', '$state'
    , 'DocumentRepository.InfoService', 'ObjectService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, DocumentRepositoryInfoService, ObjectService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "my-documents"
            , resetObjectInfo: DocumentRepositoryInfoService.resetDocumentRepositoryInfo
            , getObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , updateObjectInfo: DocumentRepositoryInfoService.updateDocumentRepositoryInfo
            , getObjectTypeFromInfo: function () {
                return ObjectService.ObjectTypes.DOC_REPO;
            }
        });
    }
]);
