'use strict';

angular.module('document-repository').controller(
        'DocumentRepository.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'DocumentRepository.InfoService', 'MessageService', 'Helper.ObjectBrowserService', 'ObjectService', 'Mentions.Service',
                function($scope, $stateParams, $translate, Util, ConfigService, DocumentRepositoryInfoService, MessageService, HelperObjectBrowserService, ObjectService, MentionsService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "document-repository",
                        componentId: "details",
                        retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
                    });

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.saveDetails = function() {
                        var docRepoInfo = Util.omitNg($scope.objectInfo);
                        DocumentRepositoryInfoService.saveDocumentRepository(docRepoInfo).then(function(docRepoInfo) {
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.DOC_REPO, "DETAILS", docRepoInfo.id, docRepoInfo.details);
                            MessageService.info($translate.instant("document-repository.comp.details.informSaved"));
                            return docRepoInfo;
                        });
                    };
                } ]);