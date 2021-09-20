'use strict';

angular.module('document-repository').controller(
        'DocumentRepository.ReferencesController',
        [ '$scope', '$stateParams', '$modal', 'UtilService', 'DocumentRepository.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ObjectAssociation.Service', 'ObjectService', 'ConfigService',
                function($scope, $stateParams, $modal, Util, DocumentRepositoryInfoService, HelperUiGridService, HelperObjectBrowserService, ObjectAssociationService, ObjectService, ConfigService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "document-repository",
                        componentId: "references",
                        retrieveObjectInfo: DocumentRepositoryInfoService.getDocumentRepositoryInfo,
                        validateObjectInfo: DocumentRepositoryInfoService.validateDocumentRepositoryInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        gridHelper.addButton(config, "delete");
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.gridOptions = $scope.gridOptions || {};
                        changeNameOfObjectType(objectInfo.references);
                        $scope.gridOptions.data = Util.goodArray(objectInfo.references);
                    };

                    ConfigService.getModuleConfig("document-repository").then(function(moduleConfig) {
                        $scope.modalConfig = _.find(moduleConfig.components, {
                            id: "referenceSearchGrid"
                        });
                        return moduleConfig;
                    });

                    $scope.onClickObjLink = function(event, rowEntity, targetNameColumnClicked) {
                        event.preventDefault();
                        var targetType = Util.goodMapValue(rowEntity, "targetType");
                        var targetId = Util.goodMapValue(rowEntity, "targetId");
                        var parentId = Util.goodMapValue(rowEntity, "parentId");
                        var parentType = Util.goodMapValue(rowEntity, "parentType");
                        var fileName = Util.goodMapValue(rowEntity, "targetName");

                        var targetTypeChanged = false;
                        var originalTargetType = targetType;
                        if(targetType == "REQUEST") {
                            targetType = ObjectService.ObjectTypes.CASE_FILE;
                            targetTypeChanged = true;
                        }

                        if (targetType == ObjectService.ObjectTypes.FILE && targetNameColumnClicked) {
                            gridHelper.openObject(targetId, parentId, parentType, fileName);
                        } else {
                            gridHelper.showObject(targetType, targetId);
                        }

                        if (ObjectService.ObjectTypes.DOC_REPO == targetType) {
                            $scope.$emit('request-show-object', {
                                objectId: targetId,
                                objectType: targetType
                            });
                        }

                        if (targetTypeChanged) {
                            targetType = originalTargetType;
                        }
                    };

                    // open add reference modal
                    $scope.addReference = function() {
                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'modules/document-repository/views/components/document-repository-reference-modal.client.view.html',
                            controller: 'DocumentRepository.ReferenceModalController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $filter: function() {
                                    var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.DOC_REPO;
                                    if ($scope.gridOptions.data.length > 0) {
                                        for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                            var data = $scope.gridOptions.data[i];
                                            filter += "&-id:" + data.targetId + "-" + data.targetType;
                                        }
                                    }
                                    filter += "&-parent_ref_s:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.DOC_REPO;
                                    return filter.replace(/&/gi, '%26');
                                },
                                $config: function() {
                                    return $scope.modalConfig;
                                }
                            }
                        });

                        modalInstance.result.then(function(chosenReference) {
                            if (chosenReference) {

                                var parent = $scope.objectInfo;
                                var target = chosenReference;
                                if (target) {
                                    var association = ObjectAssociationService.createAssociationInfo(parent.id, ObjectService.ObjectTypes.DOC_REPO, parent.title, parent.caseNumber, target.object_id_s, target.object_type_s, target.title_parseable, target.name, 'REFERENCE', 'REFERENCE');
                                }

                                ObjectAssociationService.saveObjectAssociation(association).then(function(payload) {
                                    refresh();

                                    return payload;
                                }, function(errorResponse) {
                                    MessageService.error(errorResponse.data);
                                });
                            }
                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });

                    };

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "associationId", 0);
                        ObjectAssociationService.deleteAssociationInfo(id).then(function(data) {
                            //success
                            refresh();

                            //remove it from the grid
                            _.remove($scope.gridOptions.data, function(row) {
                                return row === rowEntity;
                            });
                        });
                    };

                    var refresh = function() {
                        $scope.$emit('report-object-refreshed', $scope.objectInfo.id ? $scope.objectInfo.id : $stateParams.id);
                    };

                    //if the name is case_file change it to request
                    function changeNameOfObjectType(references) {
                        var i;
                        for (i in references) {
                            var targetType = references[i].targetType;
                            if (targetType == ObjectService.ObjectTypes.CASE_FILE) {
                                references[i].targetType = "REQUEST";
                            }
                        }
                    }

                } ]);