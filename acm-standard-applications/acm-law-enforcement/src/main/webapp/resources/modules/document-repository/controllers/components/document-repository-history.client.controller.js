'use strict';

angular.module('document-repository').controller('DocumentRepository.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ObjectService', 'DocumentRepository.InfoService', 'Helper.UiGridService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $q, Util, ObjectService, DocumentRepositoryInfoService, HelperUiGridService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "history"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, retrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);
            retrieveGridData();

            function retrieveGridData() {
                gridHelper.retrieveAuditData(ObjectService.ObjectTypes.DOC_REPO, $stateParams.id);
            }
        };
    }
]);