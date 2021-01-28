'use strict';

angular.module('organizations').controller(
    'Organizations.ConsultationsController',
    ['$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Organization.InfoService', 'Authentication', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Object.OrganizationService', 'OrganizationAssociation.Service', 'Object.LookupService', 'Mentions.Service',
        function ($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService, Authentication, HelperUiGridService, HelperObjectBrowserService, ObjectOrganizationService, OrganizationAssociationService, ObjectLookupService, MentionsService) {

            Authentication.queryUserInfo().then(function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            });

            //we are using person types because there are not specified organization types
            ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CONSULTATION).then(function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            });

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "organizations",
                componentId: "consultations",
                retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
                onConfigRetrieved: function (componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });

            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function (config) {
                $scope.config = config;

                gridHelper.setUserNameFilterToConfig(promiseUsers, config).then(function (updatedConfig) {
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

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.objectInfo = objectInfo;
                var currentObjectId = Util.goodMapValue($scope.objectInfo, "organizationId");
                OrganizationAssociationService.getOrganizationAssociations(currentObjectId, ObjectService.ObjectTypes.CONSULTATION).then(function (data) {
                    $scope.gridOptions.data = data.response.docs;
                    $scope.gridOptions.totalItems = data.response.numFound;
                    return data;
                });
            };

            $scope.addConsultationAssociation = function () {
                pickConsultation();
            };

            $scope.editRow = function (rowEntity) {
                OrganizationAssociationService.getOrganizationAssociation(rowEntity.object_id_s).then(function (association) {
                    pickConsultation(association, rowEntity);
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

            function pickConsultation(association, rowEntity) {

                var params = {};
                params.types = $scope.organizationTypes;
                params.showDescription = true;
                params.customFilter = '"Object Type": CONSULTATION';
                params.objectTypeLabel = $translate.instant("organizations.comp.consultations.objectType.label");

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
                            parent_object: data.solrDocument,
                            modified_date_tdt: response.modified
                        };
                        $scope.gridOptions.data.push(rowEntity);
                    }
                    MentionsService.sendEmailToMentionedUsers(data.emailAddresses, data.usersMentioned,
                        ObjectService.ObjectTypes.ORGANIZATION, ObjectService.ObjectTypes.CONSULTATION, $scope.objectInfo.organizationId, data.description);
                });
            }

            var newOrganizationAssociation = function () {
                return {
                    id: null,
                    associationType: "",
                    parentId: $scope.objectInfo.id,
                    parentType: $scope.objectInfo.objectType,
                    parentTitle: $scope.objectInfo.consultationNumber,
                    description: "",
                    organization: null,
                    className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
                };
            };
        }]);