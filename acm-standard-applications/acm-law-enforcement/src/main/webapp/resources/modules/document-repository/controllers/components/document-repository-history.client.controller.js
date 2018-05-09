'use strict';

angular.module('document-repository').controller('DocumentRepository.HistoryController',
        [ '$scope', '$stateParams', '$q', 'UtilService', 'ObjectService', 'DocumentRepository.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $q, Util, ObjectService, DocumentRepositoryInfoService, HelperUiGridService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "document-repository",
                componentId: "history",
                retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function(config) {
                $scope.config = config;
                //first the filter is set, and after that everything else,
                //so that the data loads with the new filter applied
                gridHelper.setUserNameFilterToConfig(promiseUsers).then(function(updatedConfig) {
                    $scope.config = updatedConfig;
                    if ($scope.gridApi != undefined)
                        $scope.gridApi.core.refresh();
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                    gridHelper.setExternalPaging(updatedConfig, retrieveGridData);
                });
                retrieveGridData();

                function retrieveGridData() {
                    gridHelper.retrieveAuditData(ObjectService.ObjectTypes.DOC_REPO, $stateParams.id);
                }
            };
        } ]);