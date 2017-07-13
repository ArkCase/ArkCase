'use strict';

angular.module('organizations').controller('Organizations.RelatedController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.LookupService', 'ObjectAssociation.Service', '$timeout'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectLookupService, ObjectAssociationService, $timeout) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        ObjectLookupService.getOrganizationRelationTypes().then(
            function (relationshipTypes) {
                $scope.relationshipTypes = relationshipTypes;
                return relationshipTypes;
            });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "related"
            , retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo
            , validateObjectInfo: OrganizationInfoService.validateOrganizationInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addButton(config, "edit");
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setUserNameFilterToConfig(promiseUsers, config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            refreshGridData(objectInfo.organizationId, objectInfo.objectType);
        };

        function refreshGridData(objectId, objectType) {
            ObjectAssociationService.getObjectAssociations(objectId, objectType, 'ORGANIZATION').then(function (response) {
                $scope.gridOptions.data = response.response.docs;
            });
        }

        $scope.addOrganizationAssociation = function () {
            organizationAssociationModal({});
        };

        $scope.editRow = function (rowEntity) {
            ObjectAssociationService.getAssociationInfo(rowEntity.object_id_s).then(function (association) {
                organizationAssociationModal(association, rowEntity);
            });
        };

        function organizationAssociationModal(association, rowEntity) {
            if (!association) {
                association = {};
            }
            var params = {
                showSetPrimary: false,
                types: $scope.relationshipTypes,
                showDescription: true
            };
            if (rowEntity) {
                angular.extend(params, {
                    organizationId: rowEntity.target_object.object_id_s,
                    organizationValue: rowEntity.target_object.title_parseable,
                    type: rowEntity.association_type_s,
                    description: rowEntity.description_s
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
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                if (data.organization) {
                    if (!data.organization.organizationId) {
                        OrganizationInfoService.saveOrganizationInfo(data.organization).then(function (response) {
                            data['organization'] = response.data;
                            updateAssociation(association, $scope.objectInfo, data.organization, data, rowEntity);
                        });
                    } else {
                        updateAssociation(association, $scope.objectInfo, data.organization, data, rowEntity);
                    }
                } else {
                    OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                        updateAssociation(association, $scope.objectInfo, organization, data, rowEntity);
                    });
                }
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
            ObjectAssociationService.saveObjectAssociation(association).then(function (payload) {
                //success
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
                rowEntity.target_object.object_id_s = target.organizationId;
                rowEntity.target_object.title_parseable = target.organizationValue;
                rowEntity.target_object.value_parseable = target.organizationValue;
                //wait 2.5 sec and refresh because of solr indexing
                //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                // $timeout(function () {
                //     refreshGridData($scope.objectInfo.organizationId, $scope.objectInfo.objectType);
                // }, 2500);
            });
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            ObjectAssociationService.deleteAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid immediately
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
                //refresh grid after 2.5 sec because of solr indexing
                //below functionality is disabled since we are already updating rows, however if in future we need to be refreshed from solr, than just enable code bellow
                // $timeout(function () {
                //     refreshGridData($scope.objectInfo.organizationId, $scope.objectInfo.objectType);
                // }, 2500);
            });
        };
    }
]);