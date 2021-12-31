'use strict';

angular.module('people').controller(
        'People.CasesController',
        [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Person.InfoService', 'Authentication', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.PersonService', 'PersonAssociation.Service', 'Object.LookupService', 'Mentions.Service',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, PersonInfoService, Authentication, HelperUiGridService, HelperObjectBrowserService, ObjectPersonService, PersonAssociationService, ObjectLookupService, MentionsService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE).then(function(personTypes) {
                        $scope.personTypes = personTypes;
                        return personTypes;
                    });

                    var assocTypeLabel = $translate.instant("people.comp.cases.type.label");

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "people",
                        componentId: "cases",
                        retrieveObjectInfo: PersonInfoService.getPersonInfo,
                        validateObjectInfo: PersonInfoService.validatePersonInfo,
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

                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;

                        gridHelper.setUserNameFilterToConfig(promiseUsers, config).then(function(updatedConfig) {
                            $scope.config = updatedConfig;
                            if ($scope.gridApi != undefined)
                                $scope.gridApi.core.refresh();
                            gridHelper.addButton(updatedConfig, "edit");
                            gridHelper.addButton(updatedConfig, "delete");
                            gridHelper.setColumnDefs(updatedConfig);
                            gridHelper.setBasicOptions(updatedConfig);
                            gridHelper.disableGridScrolling(updatedConfig);
                        });
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        var currentObjectId = Util.goodMapValue($scope.objectInfo, "id");
                        if (Util.goodPositive(currentObjectId, false)) {
                            PersonAssociationService.getPersonAssociations(currentObjectId, ObjectService.ObjectTypes.CASE_FILE).then(function(data) {
                                $scope.gridOptions.data = data.response.docs;
                                $scope.gridOptions.totalItems = data.response.numFound;
                                return data;
                            });
                        }
                    };

                    $scope.addCaseAssociation = function() {
                        pickCase();
                    };

                    $scope.editRow = function(rowEntity) {
                        PersonAssociationService.getPersonAssociation(rowEntity.object_id_s).then(function(association) {
                            pickCase(association, rowEntity);
                        });
                    };

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
                        PersonAssociationService.deletePersonAssociationInfo(id).then(function(data) {
                            //success
                            //remove it from the grid immediately
                            _.remove($scope.gridOptions.data, function(row) {
                                return row === rowEntity;
                            });
                        });
                    };

                    function pickCase(association, rowEntity) {

                        var params = {};
                        params.types = $scope.personTypes;
                        params.showDescription = true;
                        params.customFilter = '"Object Type": ' + ObjectService.ObjectTypes.CASE_FILE;
                        params.objectTypeLabel = $translate.instant("people.comp.cases.objectType.label");
                        params.assocTypeLabel = assocTypeLabel;

                        if (rowEntity) {
                            angular.extend(params, {
                                objectId: rowEntity.parent_object.object_id_s,
                                objectName: rowEntity.parent_object.name,
                                type: rowEntity.type_lcs,
                                description: association.personDescription
                            });
                        } else {
                            association = new newPersonAssociation();
                        }

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-object-association-modal.client.view.html',
                            controller: 'Common.AddObjectAssociationModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            updatePersonAssociationData(association, data, rowEntity);
                        });
                    }

                    function updatePersonAssociationData(association, data, rowEntity) {
                        if (!rowEntity) {
                            association.person = $scope.objectInfo;
                            association.parentId = data.solrDocument.object_id_s;
                        }
                        association.personType = data.type;
                        association.personDescription = data.description;

                        PersonAssociationService.savePersonAssociation(association).then(function(response) {
                            if (rowEntity) {
                                //update current row
                                rowEntity.type_lcs = response.personType;
                            } else {
                                //add row to the grid
                                rowEntity = {
                                    object_id_s: response.id,
                                    type_lcs: response.personType,
                                    parent_object: data.solrDocument
                                };
                                $scope.gridOptions.data.push(rowEntity);
                            }
                            MentionsService.sendEmailToMentionedUsers(data.emailAddresses, data.usersMentioned,
                                ObjectService.ObjectTypes.PERSON, ObjectService.ObjectTypes.CASE_FILE, $scope.objectInfo.id, data.description);
                        });
                    }

                    var newPersonAssociation = function() {
                        return {
                            id: null,
                            personType: "",
                            parentId: $scope.objectInfo.id,
                            parentType: "CASE_FILE",
                            parentTitle: $scope.objectInfo.caseNumber,
                            personDescription: "",
                            notes: "",
                            person: null,
                            className: "com.armedia.acm.plugins.person.model.PersonAssociation"
                        };
                    };
                } ]);