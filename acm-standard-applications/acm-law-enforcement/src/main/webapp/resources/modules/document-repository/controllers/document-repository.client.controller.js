'use strict';

angular.module('document-repository').controller('DocumentRepositoryController', ['$scope', '$stateParams', '$state'
    , '$translate', 'UtilService', 'ConfigService', 'DocumentRepository.InfoService', 'ObjectService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $state, $translate
        , Util, ConfigService, DocumentRepositoryInfoService, ObjectService, HelperObjectBrowserService) {

       /* new HelperObjectBrowserService.Content({
            scope: $scope
            , state: $state
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , resetObjectInfo: DocumentRepositoryInfoService.resetCaseInfo
            , getObjectInfo: DocumentRepositoryInfoService.getCaseInfo
            , updateObjectInfo: DocumentRepositoryInfoService.updateCaseInfo
            , getObjectTypeFromInfo: function () {
                return ObjectService.ObjectTypes.DOC_REPO;
            }
        });*/

    }
]);
