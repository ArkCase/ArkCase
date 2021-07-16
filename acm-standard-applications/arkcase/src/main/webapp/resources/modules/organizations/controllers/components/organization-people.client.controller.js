'use strict';

angular.module('organizations').controller(
        'Organizations.PeopleController',
        [
                '$scope',
                '$q',
                '$stateParams',
                '$translate',
                '$modal',
                'UtilService',
                'ObjectService',
                'Organization.InfoService',
                'MessageService',
                'Authentication',
                'Person.InfoService',
                'Helper.UiGridService',
                'Helper.ObjectBrowserService',
                'OrganizationAssociation.Service',
                'ConfigService',
                'Object.LookupService',
                'PermissionsService',
                'Object.ModelService',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, OrganizationInfoService, MessageService, Authentication, PersonInfoService, HelperUiGridService, HelperObjectBrowserService, OrganizationAssociationService, ConfigService, ObjectLookupService,
                        PermissionsService, ObjectModelService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.commonConfig = moduleConfig;
                        return moduleConfig;
                    });

                    var assocTypeLabel = $translate.instant("organizations.comp.people.type.label");

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "organizations",
                        componentId: "people",
                        retrieveObjectInfo: OrganizationInfoService.getOrganizationInfo,
                        validateObjectInfo: OrganizationInfoService.validateOrganizationInfo,
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
                        PermissionsService.getActionPermission('editOrganization', $scope.objectInfo, {
                            objectType: ObjectService.ObjectTypes.ORGANIZATION
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

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;
                        $scope.gridOptions.data = objectInfo.personAssociations;
                    };

                    var validatePersonAssociation = function(data, rowEntity) {
                        var validationResult = {
                            valid: true
                        };

                        $scope.objectInfo.personAssociations.filter(function(association) {
                            return (typeof rowEntity === 'undefined') || (!!rowEntity && (association.id !== rowEntity.id));
                        }).forEach(function(association) {
                            if (association.person.id == data.personId) {
                                if (data.key === association.organizationToPersonAssociationType) {
                                    validationResult.valid = false;
                                    validationResult.duplicatePersonRoleError = true;
                                }
                            }
                        });

                        return validationResult;
                    };

                    ObjectLookupService.getPersonOrganizationRelationTypes().then(function(types) {
                        $scope.personAssociationTypes = [];
                        for (var i = 0; i < types.length; i++) {
                            $scope.personAssociationTypes.push({
                                "key": types[i].key,
                                "value": types[i].value,
                                "inverseKey": types[i].inverseKey,
                                "inverseValue": types[i].inverseValue
                            });
                        }
                        return types;
                    });

                    $scope.editRow = function(rowEntity) {
                        var validateEditRow = function(data) {
                            return validatePersonAssociation(data, rowEntity);
                        };

                        var params = {
                            selectExistingEnabled: false,
                            showSetPrimary: true,
                            types: $scope.personAssociationTypes,
                            personId: rowEntity.person.id,
                            person: rowEntity.person,
                            personName: rowEntity.person.givenName + ' ' + rowEntity.person.familyName,
                            type: rowEntity.personToOrganizationAssociationType,
                            isDefault: $scope.isDefault(rowEntity),
                            returnValueValidationFunction: validateEditRow
                        };

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                            controller: 'Common.AddPersonModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.person) {
                                if (!data.person.id) {
                                    PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(savedPerson) {
                                        data['person'] = savedPerson;
                                        savePersonAssociation(rowEntity, data);
                                    });
                                } else {
                                    savePersonAssociation(rowEntity, data);
                                }
                            } else {
                                PersonInfoService.getPersonInfo(data.organizationId).then(function(person) {
                                    data['person'] = person;
                                    savePersonAssociation(rowEntity, data);
                                });
                            }
                        });
                    };

                    $scope.addPerson = function() {
                        var params = {
                            showSetPrimary: true,
                            isDefault: !hasPeople(),
                            types: $scope.personAssociationTypes,
                            returnValueValidationFunction: validatePersonAssociation,
                            hideNoField: !hasPeople(),
                            isOrganizationLocation: true,
                            personLocations: $scope.objectInfo.defaultAddress,
                            assocTypeLabel: assocTypeLabel
                        };

                        var modalInstance = $modal.open({
                            scope: $scope,
                            animation: true,
                            templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                            controller: 'Common.AddPersonModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.person) {
                                if (!data.person.id) {
                                    PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                                        data['person'] = response.data;
                                        savePersonAssociation({}, data);
                                    });
                                } else {
                                    savePersonAssociation({}, data);
                                }
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    data['person'] = person;
                                    savePersonAssociation({}, data);
                                });
                            }
                        });
                    };

                    function hasPeople() {
                        return $scope.gridOptions.data.length > 0 ? true : false;
                    }

                    function savePersonAssociation(association, data) {
                        association.organization = $scope.objectInfo;
                        association.person = data.person;
                        association.organizationToPersonAssociationType = data.inverseType;
                        association.personToOrganizationAssociationType = data.type;

                        if (data.isDefault) {
                            //find and change previously primary contact
                            var defaultAssociation = _.find($scope.objectInfo.personAssociations, function(object) {
                                return object.primaryContact;
                            });
                            if (defaultAssociation) {
                                defaultAssociation.primaryContact = false;
                            }
                        }
                        association.primaryContact = data.isDefault;

                        //if is new created, add it to the organization associations list
                        if (!association.id) {
                            if (!$scope.objectInfo.personAssociations) {
                                $scope.objectInfo.personAssociations = [];
                            }
                            $scope.objectInfo.personAssociations.push(association);
                        }
                        saveObjectInfoAndRefresh();
                    }

                    $scope.deleteRow = function(rowEntity) {
                        _.remove($scope.objectInfo.personAssociations, function(item) {
                            return item === rowEntity;
                        });
                        saveObjectInfoAndRefresh();
                    };

                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (OrganizationInfoService.validateOrganizationInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = OrganizationInfoService.saveOrganizationInfo(objectInfo);
                            promiseSaveInfo.then(function(objectInfo) {
                                $scope.$emit("report-object-updated", objectInfo);
                                return objectInfo;
                            }, function(error) {
                                MessageService.errorAction();
                                $scope.$emit('report-object-refreshed', $scope.objectInfo.organizationId);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    $scope.isDefault = function(data) {
                        return ObjectModelService.isObjectReferenceSame($scope.objectInfo, data, "primaryContact");
                    }
                } ]);