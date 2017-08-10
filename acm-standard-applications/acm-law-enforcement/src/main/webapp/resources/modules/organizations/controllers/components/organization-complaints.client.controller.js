'use strict';

angular.module('organizations').controller('Organizations.ComplaintsController', ['$scope', '$q', '$stateParams', '$translate', '$modal'
    , 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication'
    , 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.OrganizationService', 'OrganizationAssociation.Service', 'Object.LookupService'
    , function ($scope, $q, $stateParams, $translate, $modal
        , Util, ObjectService, OrganizationInfoService, Authentication
        , HelperUiGridService, HelperObjectBrowserService, ObjectOrganizationService, OrganizationAssociationService, ObjectLookupService) {


        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            }
        );

        //we are using person types because there are not specified organization types
        ObjectLookupService.getPersonTypes().then(
            function (organizationTypes) {
                var options = [];
                _.forEach(organizationTypes, function (v, k) {
                    options.push({type: v, name: v});
                });
                $scope.organizationTypes = options;
                return organizationTypes;
            });

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "organizations"
            , componentId: "complaints"
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
            var currentObjectId = Util.goodMapValue($scope.objectInfo, "organizationId");
            OrganizationAssociationService.getOrganizationAssociations(currentObjectId, ObjectService.ObjectTypes.COMPLAINT).then(function (data) {
                $scope.gridOptions.data = data.response.docs;
                $scope.gridOptions.totalItems = data.response.numFound;
                return data;
            });
        };


        $scope.addComplaintAssociation = function () {
            pickComplaint();
        };

        $scope.editRow = function (rowEntity) {
            OrganizationAssociationService.getOrganizationAssociation(rowEntity.object_id_s).then(function (association) {
                pickComplaint(association, rowEntity);
            });
        };

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            OrganizationAssociationService.deleteOrganizationAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid immediately
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
            });
        };

        function pickComplaint(association, rowEntity) {

            var params = {};
            params.types = $scope.organizationTypes;
            params.showDescription = true;
            params.customFilter = '"Object Type": COMPLAINT';
            params.objectTypeLabel = $translate.instant("organizations.comp.complaints.objectType.label");

            if (rowEntity) {
                angular.extend(params, {
                    objectId: rowEntity.parent_object.object_id_s,
                    objectName: rowEntity.parent_object.name,
                    type: rowEntity.type_lcs,
                    description: association.description
                });
            } else {
                association = new newOrganizationAssociation();
            }

            var modalInstance = $modal.open({
                scope: $scope,
                animation: true,
                templateUrl: 'modules/common/views/add-object-association-modal.client.view.html',
                controller: 'Common.AddObjectAssociationModalController',
                size: 'md',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                updateOrganizationAssociationData(association, data, rowEntity);
            });
        }

        function updateOrganizationAssociationData(association, data, rowEntity) {
            if (!rowEntity) {
                association.organization = $scope.objectInfo;
                association.parentId = data.solrDocument.object_id_s;
                association.parentType = data.solrDocument.object_type_s;
            }
            association.associationType = data.type;
            association.description = data.description;

            OrganizationAssociationService.saveOrganizationAssociation(association).then(function (response) {
                if (rowEntity) {
                    //update current row
                    rowEntity.type_lcs = response.associationType;
                } else {
                    //add row to the grid
                    rowEntity = {
                        object_id_s: response.id,
                        type_lcs: response.associationType,
                        parent_object: data.solrDocument
                    };
                    $scope.gridOptions.data.push(rowEntity);
                }
            });
        }

        var newOrganizationAssociation = function () {
            return {
                id: null
                , associationType: ""
                , parentId: $scope.objectInfo.id
                , parentType: $scope.objectInfo.objectType
                , parentTitle: $scope.objectInfo.complaintNumber
                , description: ""
                , organization: null
                , className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
            };
        };
    }
]);