'use strict';

angular.module('organizations').controller(
        'Organizations.InfoController',
        [ '$rootScope', '$scope', '$stateParams', '$translate', '$modal', 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Object.LookupService', 'ObjectAssociation.Service', 'Organization.SearchService', 'UtilService',
                function($rootScope, $scope, $stateParams, $translate, $modal, OrganizationInfoService, HelperObjectBrowserService, ObjectLookupService, ObjectAssociationService, OrganizationSearchService, Util) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "info",
                        retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
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

                    ObjectLookupService.getOrganizationTypes().then(function(organizationTypes) {
                        $scope.organizationTypes = organizationTypes;
                        var defaultOrganizationType = ObjectLookupService.getPrimaryLookup($scope.organizationTypes);
                        if ($scope.organization && $scope.organization.isNew && defaultOrganizationType) {
                            $scope.organization.organizationType = defaultOrganizationType.key;
                        }
                    });

                    ObjectLookupService.getOrganizationIdTypes().then(function(identificationTypes) {
                        $scope.identificationTypes = identificationTypes;
                    });

                    $scope.organizationId = null;
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.organizationId = objectInfo.organizationId;
                        $scope.objectInfo = objectInfo;
                        if ($scope.objectInfo.defaultIdentification) {
                            $scope.objectInfo.defaultIdentification.identificationValue = _.find($scope.identificationTypes, function (identificationType) {
                                if (identificationType.key === $scope.objectInfo.defaultIdentification.identificationType) {
                                    return identificationType.value;
                                }
                            });
                        }
                    };

                    $scope.addParent = function(isSelectedParent, association, rowEntity) {
                        if (Util.isEmpty(association)) {
                            association = {};
                        }

                        var externalSearchParams = {};
                        externalSearchParams.organizationId = $scope.organizationId;

                        var params = {
                            showSetPrimary: false,
                            types: $scope.relationshipTypes,
                            showDescription: true,
                            infoType: true,
                            externalSearchServiceName: "Organization.SearchService",
                            externalSearchParams: externalSearchParams
                        };
                        if (!!isSelectedParent) {
                            params.organization = $scope.objectInfo;
                            params.isSelectedParent = isSelectedParent;
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
                            if (data.isNew) {
                                if (!data.organization.organizationId) {
                                    OrganizationInfoService.saveOrganizationInfo(data.organization).then(function(response) {
                                        data['organization'] = response;
                                        updateOrganization(association, $scope.objectInfo, data.organization, data, rowEntity);
                                    });
                                }
                            } else {
                                OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                                    updateOrganization(association, $scope.objectInfo, organization, data, rowEntity);
                                });
                            }
                        });
                    };

                    $scope.saveOrganization = function() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    function updateOrganization(association, parent, target, associationData, rowEntity) {
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
                        });
                    }

                    $rootScope.$bus.subscribe("object.changed/ORGANIZATION/" + $stateParams.id, function() {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    });
                } ]);
