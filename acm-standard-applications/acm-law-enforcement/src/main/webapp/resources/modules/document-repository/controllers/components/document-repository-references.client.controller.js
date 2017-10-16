'use strict';

angular.module('document-repository').controller('DocumentRepository.ReferencesController', ['$scope', '$stateParams'
    , '$modal', 'UtilService', 'DocumentRepository.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , 'Object.ReferenceService', 'ObjectService', 'ConfigService'
    , function ($scope, $stateParams, $modal, Util, DocumentRepositoryInfoService, HelperUiGridService
        , HelperObjectBrowserService, ReferenceService, ObjectService, ConfigService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "document-repository"
            , componentId: "references"
            , retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo
            , validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.gridOptions = $scope.gridOptions || {};
            $scope.gridOptions.data = Util.goodArray(objectInfo.references);
        };

        ConfigService.getModuleConfig("document-repository").then(function (moduleConfig) {
            $scope.modalConfig = _.find(moduleConfig.components, {id: "referenceSearchGrid"});
            return moduleConfig;
        });

        $scope.onClickObjLink = function (event, rowEntity, targetNameColumnClicked) {
            event.preventDefault();
            var targetType = Util.goodMapValue(rowEntity, "targetType");
            var targetId = Util.goodMapValue(rowEntity, "targetId");
            var parentId = Util.goodMapValue(rowEntity, "parentId");
            var parentType = Util.goodMapValue(rowEntity, "parentType");
            var fileName = Util.goodMapValue(rowEntity, "targetName");

            if (targetType == ObjectService.ObjectTypes.FILE && targetNameColumnClicked) {
                gridHelper.openObject(targetId, parentId, parentType, fileName);
            } else {
                gridHelper.showObject(targetType, targetId);
            }

            if (ObjectService.ObjectTypes.DOC_REPO == targetType) {
                $scope.$emit('request-show-object', {objectId: targetId, objectType: targetType});
            }
        };

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        // open add reference modal
        $scope.addReference = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/document-repository/views/components/document-repository-reference-modal.client.view.html',
                controller: 'DocumentRepository.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.objectInfo.id + "-"
                            + ObjectService.ObjectTypes.DOC_REPO;
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        filter += "&-parent_ref_s:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.DOC_REPO;
                        return filter.replace(/&/gi, '%26');
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            modalInstance.result.then(function (chosenReference) {
                if (chosenReference) {
                    var reference = {};
                    reference.referenceId = chosenReference.object_id_s;
                    reference.referenceTitle = chosenReference.title_parseable;
                    reference.referenceType = chosenReference.object_type_s;
                    reference.referenceNumber = chosenReference.name;
                    reference.referenceStatus = chosenReference.status_lcs;
                    reference.parentId = $stateParams.id;
                    reference.parentType = ObjectService.ObjectTypes.DOC_REPO;
                    ReferenceService.addReference(reference).then(
                        function (objectSaved) {
                            $scope.refresh();
                            return objectSaved;
                        },
                        function (error) {
                            return error;
                        }
                    );
                    return;
                }
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

    }
]);