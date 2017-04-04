'use strict';

angular.module('document-repository').controller('DocumentRepository.DetailsController', ['$scope', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService', 'DocumentRepository.InfoService', 'MessageService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $translate
        , Util, ConfigService, DocumentRepositoryInfoService, MessageService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "details"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
        });

        $scope.options = {
            focus: true,
            dialogsInBody: true
        };

        $scope.saveDetails = function () {
            var docRepoInfo = Util.omitNg($scope.objectInfo);
            DocumentRepositoryInfoService.saveDocumentRepository(docRepoInfo).then(
                function (docRepoInfo) {
                    MessageService.info($translate.instant("document-repository.comp.details.informSaved"));
                    return docRepoInfo;
                }
            );
        };
    }
]);