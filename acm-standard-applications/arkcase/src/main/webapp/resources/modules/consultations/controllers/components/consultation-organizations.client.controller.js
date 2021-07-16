'use strict';

angular.module('consultations').controller(
    'Consultations.OrganizationsController',
    [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Consultation.InfoService', 'Authentication', 'Object.LookupService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Organization.InfoService',
        function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, ConsultationInfoService, Authentication, ObjectLookupService, HelperUiGridService, HelperObjectBrowserService, OrganizationInfoService) {

            Authentication.queryUserInfo().then(function (userInfo) {
                $scope.userId = userInfo.userId;
                return userInfo;
            });

            // TODO: this is only changed for caseFileOrganizationTypes because there are not specified organization types for other objects
            ObjectLookupService.getObjectOrganizationTypes(ObjectService.ObjectTypes.CONSULTATION).then(function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                return organizationTypes;
            });

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "organizations",
                retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
                validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var assocTypeLabel = $translate.instant("consultations.comp.organizations.type.label");

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
                    gridHelper.addButton(updatedConfig, "edit", null, null, "isEditDisabled");
                    gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                    gridHelper.setColumnDefs(updatedConfig);
                    gridHelper.setBasicOptions(updatedConfig);
                    gridHelper.disableGridScrolling(updatedConfig);
                });
            };

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectInfo = objectInfo;
                $scope.gridOptions.data = $scope.objectInfo.organizationAssociations;
            };

            $scope.getPrimaryContact = function(organizationAssiciation) {
                var primaryContact = organizationAssiciation.organization.primaryContact;
                if (!!primaryContact) {
                    var getPrimaryConactGivenName = Util.goodValue(primaryContact.person.givenName);
                    var getPrimaryConactFamilyName = Util.goodValue(primaryContact.person.familyName);
                    return (getPrimaryConactGivenName.trim() + ' ' + getPrimaryConactFamilyName.trim()).trim();
                }
                return '';
            };

            var newOrganizationAssociation = function() {
                return {
                    id: null,
                    associationType: "",
                    parentId: $scope.objectInfo.id,
                    parentType: ObjectService.ObjectTypes.CONSULTATION,
                    parentTitle: $scope.objectInfo.consultationNumber,
                    organization: null,
                    className: "com.armedia.acm.plugins.person.model.OrganizationAssociation"
                };
            };

            $scope.addOrganization = function() {
                pickOrganization(null);
            };

            function pickOrganization(association) {
                var params = {};
                params.types = $scope.organizationTypes;
                params.assocTypeLabel = assocTypeLabel;

                if (association) {
                    angular.extend(params, {
                        organizationId: association.organization.organizationId,
                        organizationValue: association.organization.organizationValue,
                        type: association.associationType,
                        description: association.description
                    });
                } else {
                    association = new newOrganizationAssociation();
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
                        updateOrganizationAssociationData(association, data.organization, data);
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                            updateOrganizationAssociationData(association, organization, data);
                        })
                    }
                });
            }

            function updateOrganizationAssociationData(association, organization, data) {
                association.organization = organization;
                association.associationType = data.type;
                if (!association.id) {
                    $scope.objectInfo.organizationAssociations.push(association);
                }
                saveObjectInfoAndRefresh();
            }

            $scope.deleteRow = function(rowEntity) {
                var id = Util.goodMapValue(rowEntity, "id", 0);
                _.remove($scope.objectInfo.organizationAssociations, function(item) {
                    return item === rowEntity;
                });
                if (rowEntity.id) {
                    saveObjectInfoAndRefresh();
                }
            };

            $scope.editRow = function(rowEntity) {
                pickOrganization(rowEntity);
            };

            function saveObjectInfoAndRefresh() {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (ConsultationInfoService.validateConsultationInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);
                    promiseSaveInfo = ConsultationInfoService.saveConsultationInfo(objectInfo);
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

            $scope.isEditDisabled = function(rowEntity) {
                //add conditions if edit button shouldn't be visible
                return false;
            };

            $scope.isDeleteDisabled = function(rowEntity) {
                //add conditions if delete button shouldn't be visible
                return false;
            };
        } ]);
