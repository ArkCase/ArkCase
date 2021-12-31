'use strict';

angular.module('organizations').controller(
        'Organizations.RelatedController',
        [
                '$rootScope',
                '$scope',
                '$q',
                '$stateParams',
                '$translate',
                '$modal',
                'UtilService',
                'ObjectService',
                'Organization.InfoService',
                'Authentication',
                'Helper.UiGridService',
                'Helper.ObjectBrowserService',
                'Object.LookupService',
                'Organization.SearchService',
                'ObjectAssociation.Service',
                '$timeout',
                'PermissionsService',
                'MessageService',
                'Mentions.Service',
                function($rootScope, $scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService, Authentication, HelperUiGridService, HelperObjectBrowserService, ObjectLookupService, OrganizationSearchService, ObjectAssociationService, $timeout, PermissionsService,
                        MessageService, MentionsService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    $scope.relationshipTypes = [];
                    ObjectLookupService.getOrganizationRelationTypes().then(function(relationshipTypes) {
                        for (var i = 0; i < relationshipTypes.length; i++) {
                            $scope.relationshipTypes.push({
                                "key": relationshipTypes[i].inverseKey,
                                "value": relationshipTypes[i].inverseValue,
                                "inverseKey": relationshipTypes[i].key,
                                "inverseValue": relationshipTypes[i].value
                            });
                        }

                        return relationshipTypes;
                    });

                    var assocTypeLabel = $translate.instant("organizations.comp.related.type.label");

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "related",
                        retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.organizationId = null;
                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.ORGANIZATION
                        }).then(function(result) {
                            if (result) {
                                gridHelper.addButton(config, "edit");
                                gridHelper.addButton(config, "delete");
                            }
                        });
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.organizationId = objectInfo.organizationId;
                        refreshGridData(objectInfo.organizationId, objectInfo.objectType);
                    };

                    function refreshGridData(objectId, objectType) {
                        ObjectAssociationService.getObjectAssociations(objectId, objectType, 'ORGANIZATION').then(function(response) {
                            $scope.gridOptions.data = response.response.docs;
                        });
                    }

                    $scope.addOrganizationAssociation = function() {
                        organizationAssociationModal({});
                    };

                    $scope.editRow = function(rowEntity) {
                        ObjectAssociationService.getAssociationInfo(rowEntity.object_id_s).then(function(association) {
                            organizationAssociationModal(association, rowEntity);
                        });
                    };

                    function organizationAssociationModal(association, rowEntity) {
                        if (!association) {
                            association = {};
                        }

                        var externalSearchParams = {};
                        externalSearchParams.organizationId = $scope.organizationId;

                        var params = {
                            showSetPrimary: false,
                            types: $scope.relationshipTypes,
                            showDescription: true,
                            externalSearchServiceName: "Organization.SearchService",
                            externalSearchParams: externalSearchParams,
                            assocTypeLabel: assocTypeLabel
                        };
                        if (rowEntity) {
                            angular.extend(params, {
                                organizationId: rowEntity.target_object.object_id_s,
                                organizationValue: rowEntity.target_object.title_parseable,
                                type: rowEntity.association_type_s,
                                description: rowEntity.description_s,
                                isEditOrganization: true
                            });
                        }

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                            controller: 'Common.AddOrganizationModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.organization) {
                                if (!data.organization.organizationId) {
                                    OrganizationInfoService.saveOrganizationInfo(data.organization).then(function(response) {
                                        data['organization'] = response;
                                        updateAssociation(association, $scope.objectInfo, data.organization, data, rowEntity);
                                    });
                                } else {
                                    updateAssociation(association, $scope.objectInfo, data.organization, data, rowEntity);
                                }
                            } else {
                                OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                                    updateAssociation(association, $scope.objectInfo, organization, data, rowEntity);
                                });
                            }
                            MentionsService.sendEmailToMentionedUsers(data.emailAddresses, data.usersMentioned,
                                ObjectService.ObjectTypes.ORGANIZATION, "RELATED", $scope.objectInfo.organizationId, data.description);
                        });
                    }

                    function updateAssociation(association, parent, target, associationData, rowEntity) {
                        association.parentId = parent.organizationId;
                        association.parentType = parent.objectType;

                        association.targetId = target.organizationId;
                        association.targetType = target.objectType;

                        association.associationType = associationData.type;

                        if (associationData.inverseType) {
                            var inverseAssociation = association.inverseAssociation;
                            if (!inverseAssociation) {
                                inverseAssociation = {};
                                association.inverseAssociation = inverseAssociation;
                            }
                            if (inverseAssociation.inverseAssociation != association) {
                                inverseAssociation.inverseAssociation = association;
                            }
                            //switch parent and target because of inverse association
                            inverseAssociation.parentId = target.organizationId;
                            inverseAssociation.parentType = target.objectType;

                            inverseAssociation.targetId = parent.organizationId;
                            inverseAssociation.targetType = parent.objectType;

                            inverseAssociation.associationType = associationData.inverseType;
                            inverseAssociation.description = associationData.description;
                        }
                        association.description = associationData.description;
                        ObjectAssociationService.saveObjectAssociation(association).then(function(payload) {
                            //success
                            if (payload.associationType.toLowerCase() !== "parentcompany") {
                                if (!rowEntity) {
                                    //append new entity as last item in the grid
                                    rowEntity = {
                                        target_object: {}
                                    };
                                    $scope.gridOptions.data.push(rowEntity);
                                }

                                //update row immediately
                                rowEntity.object_id_s = payload.associationId;
                                rowEntity.association_type_s = payload.associationType;
                                rowEntity.target_object.type_lcs = target.organizationType;
                                if (!Util.isEmpty(target.defaultIdentification)) {
                                    if (!Util.isEmpty(target.defaultIdentification.identificationType)) {
                                        rowEntity.target_object.default_identification_s = target.defaultIdentification.identificationNumber + " " + target.defaultIdentification.identificationType;
                                    } else {
                                        rowEntity.target_object.default_identification_s = target.defaultIdentification.identificationNumber;
                                    }
                                }
                                rowEntity.target_object.title_parseable = target.organizationValue;
                                rowEntity.target_object.value_parseable = target.organizationValue;
                                if (!Util.isEmpty(target.primaryContact)) {
                                    if (!Util.isEmpty(target.primaryContact.person.familyName)) {
                                        rowEntity.target_object.primary_contact_s = target.primaryContact.person.givenName + " " + target.primaryContact.person.familyName;
                                    } else {
                                        rowEntity.target_object.primary_contact_s = target.primaryContact.person.givenName;
                                    }

                                }
                                if (!Util.isEmpty(target.defaultPhone)) {
                                    rowEntity.target_object.default_phone_s = target.defaultPhone.value + " [" + target.defaultPhone.subType + "]";
                                } else {
                                    rowEntity.target_object.default_phone_s = "";
                                }

                                if (!Util.isEmpty(target.defaultAddress)) {
                                    if (!Util.isEmpty(target.defaultAddress.state)) {
                                        rowEntity.target_object.default_location_s = target.defaultAddress.city + ", " + target.defaultAddress.state;
                                    } else {
                                        rowEntity.target_object.default_location_s = target.defaultAddress.city;
                                    }
                                }
                            }
                            //wait 2.5 sec and refresh because of solr indexing
                            //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                            // $timeout(function () {
                            //     refreshGridData($scope.objectInfo.organizationId, $scope.objectInfo.objectType);
                            // }, 2500);
                        }, function(errorResponse) {
                            MessageService.error(errorResponse.data);
                        });
                    }

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
                        ObjectAssociationService.deleteAssociationInfo(id).then(function(data) {
                            //success
                            //remove it from the grid immediately
                            _.remove($scope.gridOptions.data, function(row) {
                                return row === rowEntity;
                            });

                            //refresh grid after 2.5 sec because of solr indexing
                            //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                            // $timeout(function () {
                            //     refreshGridData($scope.objectInfo.organizationId, $scope.objectInfo.objectType);
                            // }, 2500);
                        });
                    };

                    $scope.onClickObjLink = function(event, rowEntity) {
                        event.preventDefault();
                        var targetType = 'ORGANIZATION';
                        var targetId = Util.goodMapValue(rowEntity, "target_object.object_id_s");
                        gridHelper.showObject(targetType, targetId);
                    };

                    $rootScope.$bus.subscribe("object.changed/ORGANIZATION/" + $stateParams.id, function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    });
                } ]);