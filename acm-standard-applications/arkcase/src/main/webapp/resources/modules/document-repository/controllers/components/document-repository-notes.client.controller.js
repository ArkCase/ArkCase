'use strict';

angular.module('document-repository').controller('DocumentRepository.NotesController',
        [ '$scope', '$stateParams', '$translate', 'ObjectService', 'DocumentRepository.InfoService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $translate, ObjectService, DocumentRepositoryInfoService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "document-repository",
                componentId: "notes",
                retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                },
                onTranslateChangeSuccess: function(data) {
                    onTranslateChangeSuccess(data);
                }
            });

            $scope.notesInit = {
                noteTitle: $translate.instant("document-repository.comp.notes.title"),
                objectType: ObjectService.ObjectTypes.DOC_REPO,
                currentObjectId: $stateParams.id,
                parentTitle: "",
                noteType: "GENERAL"
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.parentObjectTitle = objectInfo.name;
                if ($scope.notesInit) {
                    $scope.notesInit.parentTitle = objectInfo.name;
                }
            };

            var onTranslateChangeSuccess = function(data) {
                if ($scope.notesInit) {
                    $scope.notesInit.noteTitle = $translate.instant("document-repository.comp.notes.title");
                }
            };
        } ]);