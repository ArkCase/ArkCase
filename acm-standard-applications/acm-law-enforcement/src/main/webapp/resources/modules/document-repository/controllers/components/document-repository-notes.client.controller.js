'use strict';

angular.module('document-repository').controller('DocumentRepository.NotesController', ['$scope', '$stateParams'
    , 'ObjectService', 'DocumentRepository.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, ObjectService, DocumentRepositoryInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component(
            {
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "document-repository",
                componentId: "notes",
                retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

        $scope.notesInit = {
            noteTitle: "Notes",
            objectType: ObjectService.ObjectTypes.DOC_REPO,
            currentObjectId: $stateParams.id,
            parentTitle: "",
            noteType: "GENERAL"
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.parentObjectTitle = objectInfo.name;
            $scope.notesInit.parentTitle = objectInfo.name;
        };
    }
]);