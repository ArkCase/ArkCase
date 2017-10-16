'use strict';

angular.module('document-repository').controller('DocumentRepository.ParticipantsController', ['$scope', '$translate'
    , 'DocumentRepository.InfoService', 'ObjectService'
    , function ($scope, $translate, DocumentRepositoryInfoService, ObjectService) {

        $scope.participantsInit = {
            moduleId: 'document-repository',
            componentId: 'participants',
            retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
            validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
            saveObjectInfo: DocumentRepositoryInfoService.saveDocumentRepository,
            objectType: ObjectService.ObjectTypes.DOC_REPO,
            participantsTitle: $translate.instant("document-repository.comp.participants.title")
        }
    }
]);

