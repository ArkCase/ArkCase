'use strict';

angular.module('complaints').controller(
        'Complaints.PeopleController',
        [ '$scope', '$q', '$stateParams', '$translate', '$modal', 'UtilService', 'ObjectService', 'Complaint.InfoService', 'Authentication', 'Object.LookupService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Person.InfoService',
                function($scope, $q, $stateParams, $translate, $modal, Util, ObjectService, ComplaintInfoService, Authentication, ObjectLookupService, HelperUiGridService, HelperObjectBrowserService, PersonInfoService) {

                    Authentication.queryUserInfo().then(function(userInfo) {
                        $scope.userId = userInfo.userId;
                        return userInfo;
                    });

                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT).then(function(personTypes) {
                        $scope.personTypes = personTypes;
                        return personTypes;
                    });
                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT, true).then(function(personTypes) {
                        $scope.personTypesInitiator = personTypes;
                        $scope.initiatorType = ObjectLookupService.getPrimaryLookup($scope.personTypesInitiator)
                        return personTypes;
                    });

                    var assocTypeLabel = $translate.instant("complaints.comp.people.type.label");

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "complaints",
                        componentId: "people",
                        retrieveObjectInfo: ComplaintInfoService.getComplaintInfo,
                        validateObjectInfo: ComplaintInfoService.validateComplaintInfo,
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
                            gridHelper.addButton(updatedConfig, "edit", null, null, "isEditDisabled");
                            gridHelper.addButton(updatedConfig, "delete", null, null, "isDeleteDisabled");
                            gridHelper.setColumnDefs(updatedConfig);
                            gridHelper.setBasicOptions(updatedConfig);
                            gridHelper.disableGridScrolling(updatedConfig);
                        });
                    };

                    var onObjectInfoRetrieved = function(objectInfo) {
                        PersonInfoService.getPersons().then(function(persons) {
                            $scope.gridOptions.data = HelperUiGridService.filterRestricted(persons.data.response.docs, $scope.objectInfo.personAssociations);
                        });
                    };

                    var newPersonAssociation = function() {
                        return {
                            id: null,
                            personType: "",
                            parentId: $scope.objectInfo.complaintId,
                            parentType: ObjectService.ObjectTypes.COMPLAINT,
                            parentTitle: $scope.objectInfo.complaintNumber,
                            personDescription: "",
                            notes: "",
                            person: null,
                            className: "com.armedia.acm.plugins.person.model.PersonAssociation"
                        };
                    };

                    $scope.addPerson = function() {
                        pickPerson(null);
                    };

                    function pickPerson(association) {

                        var params = {};
                        params.types = $scope.personTypes;
                        params.assocTypeLabel = assocTypeLabel;

                        if (association) {
                            if (association.personType == $scope.initiatorType) {
                                //change the types only for initiator
                                params.types = $scope.personTypesInitiator;
                            }
                            angular.extend(params, {
                                personId: association.person.id,
                                personName: association.person.givenName + ' ' + association.person.familyName,
                                type: association.personType,
                                selectExistingEnabled: association.personType == $scope.initiatorType ? true : false,
                                typeEnabled: association.personType == $scope.initiatorType ? false : true,
                                description: association.personDescription
                            });
                        } else {
                            association = new newPersonAssociation();
                        }

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
                            if (data.isNew) {
                                PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                                    data.person = response.data;
                                    updatePersonAssociationData(association, data.person, data);
                                });
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    updatePersonAssociationData(association, person, data);
                                })
                            }
                        });
                    }

                    function updatePersonAssociationData(association, person, data) {
                        association.person = person;
                        association.personType = data.type;
                        association.personDescription = data.description;
                        if (!association.id) {
                            $scope.objectInfo.personAssociations.push(association);
                        }
                        saveObjectInfoAndRefresh();
                    }

                    $scope.deleteRow = function(rowEntity) {
                        var id = Util.goodMapValue(rowEntity, "id", 0);
                        _.remove($scope.objectInfo.personAssociations, function(item) {
                            return item === rowEntity;
                        });
                        if (rowEntity.id) {
                            saveObjectInfoAndRefresh();
                        }
                    };

                    $scope.editRow = function(rowEntity) {
                        pickPerson(rowEntity);
                    };

                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (ComplaintInfoService.validateComplaintInfo($scope.objectInfo)) {
                            var objectInfo = Util.omitNg($scope.objectInfo);
                            promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(objectInfo);
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

                    $scope.isDeleteDisabled = function(rowEntity) {
                        return rowEntity.personType == $scope.initiatorType.key;
                    };

                    $scope.isEditDisabled = function(rowEntity) {
                        return rowEntity.personType == $scope.initiatorType.key;
                    };

                } ]);