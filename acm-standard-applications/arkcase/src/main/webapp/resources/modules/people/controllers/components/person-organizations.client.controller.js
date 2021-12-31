'use strict';

angular.module('people').controller(
        'People.OrganizationsController',
        [
                '$scope',
                '$q',
                '$stateParams',
                '$translate',
                '$modal',
                'UtilService',
                'ObjectService',
                'Person.InfoService',
                'Authentication',
                'Organization.InfoService',
                'MessageService',
                'Helper.UiGridService',
                'Helper.ObjectBrowserService',
                'ConfigService',
                'PersonAssociation.Service',
                'Object.LookupService',
                'PermissionsService',
                'Object.ModelService',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, PersonInfoService, Authentication, OrganizationInfoService, MessageService, HelperUiGridService, HelperObjectBrowserService, ConfigService, PersonAssociationService, ObjectLookupService, PermissionsService,
                        ObjectModelService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });
                    $scope.tableData = [];

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.commonConfig = moduleConfig;
                        return moduleConfig;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "people",
                        componentId: "organizations",
                        retrieveObjectInfo: PersonInfoService.getPersonInfo,
                        validateObjectInfo: PersonInfoService.validatePersonInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        },
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var assocTypeLabel = $translate.instant("people.comp.organizations.type.label");

                    var validateOrganizationAssociation = function(data, rowEntity) {
                        var validationResult = {
                            valid: true
                        };

                        $scope.objectInfo.organizationAssociations.filter(function(association) {
                            return (typeof rowEntity === 'undefined') || (!!rowEntity && (association.id !== rowEntity.id));
                        }).forEach(function(association) {
                            if (association.organization.organizationId == data.organizationId) {
                                if (data.inverseType === association.personToOrganizationAssociationType) {
                                    validationResult.valid = false;
                                    validationResult.duplicateOrganizationRoleError = true;
                                }
                            }
                        });

                        return validationResult;
                    }

                    ObjectLookupService.getPersonOrganizationRelationTypes().then(function(organizationTypes) {
                        $scope.organizationTypes = [];
                        for (var i = 0; i < organizationTypes.length; i++) {
                            $scope.organizationTypes.push({
                                "key": organizationTypes[i].inverseKey,
                                "value": organizationTypes[i].inverseValue,
                                "inverseKey": organizationTypes[i].key,
                                "inverseValue": organizationTypes[i].value
                            });
                        }
                        return organizationTypes;
                    });

                    var gridHelper = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    var promiseUsers = gridHelper.getUsers();

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        PermissionsService.getActionPermission('editPerson', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.PERSON
                        }).then(function(result) {
                            if (result) {
                                gridHelper.addButton(config, "edit");
                                gridHelper.addButton(config, "delete", null, null, "isDefault");
                            }
                        });
                        gridHelper.setColumnDefs(config);
                        gridHelper.setBasicOptions(config);
                        gridHelper.disableGridScrolling(config);
                        gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                    };

                    $scope.hasSelected = false;

                    $scope.editRow = function(rowEntity) {
                        var validateEditRow = function(data) {
                            return validateOrganizationAssociation(data, rowEntity);
                        };

                        var params = {
                            showSetPrimary: true,
                            types: $scope.organizationTypes,
                            organizationId: rowEntity.organization.organizationId,
                            organizationValue: rowEntity.organization.organizationValue,
                            type: rowEntity.organizationToPersonAssociationType,
                            isDefault: rowEntity === $scope.objectInfo.defaultOrganization,
                            returnValueValidationFunction: validateEditRow
                        };

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
                                savePersonAssociation(rowEntity, data);
                            } else {
                                OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                                    data['organization'] = organization;
                                    savePersonAssociation(rowEntity, data);
                                });
                            }
                        });
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.gridOptions.data = objectInfo.organizationAssociations;
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

                    $scope.addOrganization = function() {
                        $scope.isFirstOrganization = $scope.gridOptions.data.length == 0 ? true : false;
                        var params = {
                            showSetPrimary: true,
                            isDefault: false,
                            types: $scope.organizationTypes,
                            returnValueValidationFunction: validateOrganizationAssociation,
                            isFirstOrganization: $scope.isFirstOrganization,
                            assocTypeLabel: assocTypeLabel
                        };

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
                                        savePersonAssociation({}, data);
                                    });
                                } else {
                                    savePersonAssociation({}, data);
                                }
                            } else {
                                OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                                    data['organization'] = organization;
                                    savePersonAssociation({}, data);
                                });
                            }
                        });
                    };

                    function savePersonAssociation(association, data) {
                        association.person = {
                            id: $scope.objectInfo.id
                        };
                        association.organization = data.organization;
                        association.personToOrganizationAssociationType = data.inverseType;
                        association.organizationToPersonAssociationType = data.type;

                        if (data.isDefault) {
                            //find and change previously default organization
                            var defaultAssociation = _.find($scope.objectInfo.organizationAssociations, function(object) {
                                return object.defaultOrganization;
                            });
                            if (defaultAssociation) {
                                defaultAssociation.defaultOrganization = false;
                            }
                            $scope.objectInfo.defaultOrganization = association;
                        }
                        association.defaultOrganization = data.isDefault;

                        //if is new created, add it to the organization associations list
                        if (!association.id) {
                            if (!$scope.objectInfo.organizationAssociations) {
                                $scope.objectInfo.organizationAssociations = [];
                            }
                            $scope.objectInfo.organizationAssociations.push(association);
                        }
                        saveObjectInfoAndRefresh();
                    }

                    $scope.deleteRow = function(rowEntity) {
                        _.remove($scope.objectInfo.organizationAssociations, function(item) {
                            return item === rowEntity;
                        });
                        saveObjectInfoAndRefresh();
                    };

                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (PersonInfoService.validatePersonInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = PersonInfoService.savePersonInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                MessageService.errorAction();
                                $scope.$emit('report-object-refreshed', $scope.objectInfo.id);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }
                    $scope.isDefault = function(data) {
                        return ObjectModelService.isObjectReferenceSame($scope.objectInfo, data, "defaultOrganization");
                    }

                } ]);