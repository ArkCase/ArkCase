'use strict';

angular.module('organizations').controller('Organizations.InfoController', ['$scope', '$stateParams', '$translate', '$modal'
    , 'Organization.InfoService', 'Helper.ObjectBrowserService', 'Object.LookupService', 'ObjectAssociation.Service', 'UtilService'
    , function ($scope, $stateParams, $translate, $modal
        , OrganizationInfoService, HelperObjectBrowserService, ObjectLookupService, ObjectAssociationService, Util) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "info"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        $scope.relationshipTypes = [];
        ObjectLookupService.getOrganizationRelationTypes().then(
            function (relationshipTypes) {
                for (var i = 0; i < relationshipTypes.length; i++) {
                    $scope.relationshipTypes.push({"key": relationshipTypes[i].inverseKey, "value" : relationshipTypes[i].inverseValue, "inverseKey": relationshipTypes[i].key, "inverseValue": relationshipTypes[i].value});
                }

                return relationshipTypes;
            });

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
        };

        $scope.addParent = function (isSelectedParent, association, rowEntity) {
            if (Util.isEmpty(association)) {
                association = {};
            }
            var params = {
                showSetPrimary: false,
                types: $scope.relationshipTypes,
                showDescription: true,
                infoType: true
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
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.isNew) {
                    if (!data.organization.organizationId) {
                        OrganizationInfoService.saveOrganizationInfo(data.organization).then(function (response) {
                            data['organization'] = response;
                            updateOrganization(association, $scope.objectInfo, data.organization, data, rowEntity);
                        });
                    }
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        updateOrganization(association, $scope.objectInfo, organization, data, rowEntity);
                    });
                }
            });
        };


        $scope.saveOrganization = function () {
            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
            if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                var objectInfo = Util.omitNg($scope.objectInfo);
                promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                promiseSaveInfo.then(
                    function (objectInfo) {
                        $scope.$emit("report-object-updated", objectInfo);
                        return objectInfo;
                    }
                    , function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    }
                );
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
            ObjectAssociationService.saveObjectAssociation(association).then(function (payload) {
                //success
                /*if (!rowEntity) {
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
                }*/
            });
        }
    }
]);