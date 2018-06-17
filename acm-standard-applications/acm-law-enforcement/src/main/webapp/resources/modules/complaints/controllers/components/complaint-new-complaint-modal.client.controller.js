'use strict';

angular.module('complaints').controller(
        'Complaints.NewComplaintController',
        [ '$scope', '$stateParams', '$translate', 'Complaint.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Organization.InfoService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Profile.UserInfoService',
                function($scope, $stateParams, $translate, ComplaintInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, OrganizationInfoService, ObjectService, modalParams, PersonInfoService, UserInfoService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.selectedFiles = [];

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.config = moduleConfig;
                        return moduleConfig;
                    });

                    //new complaint with predefined values
                    $scope.complaint = {
                        className: 'com.armedia.acm.plugins.complaint.model.Complaint',
                        complaintType: '',
                        complaintTitle: '',
                        details: '',
                        priority: '',
                        incidentDate: new Date(),
                        tag: '',
                        frequency: '',
                        defaultAddress: {
                            created: new Date()
                        },
                        initiator: '',
                        addresses: [],
                        personAssociations: [ {} ]
                    };
                    var initiatorType = 'Initiator';

                    ObjectLookupService.getComplaintTypes().then(function(complaintTypes) {
                        $scope.incidentCategory = complaintTypes;
                    });

                    ObjectLookupService.getPriorities().then(function(priorities) {
                        $scope.priorities = priorities;
                    });

                    ObjectLookupService.getFrequencies().then(function(frequencies) {
                        $scope.frequencies = frequencies;
                    });

                    ObjectLookupService.getAddressTypes().then(function(addressTypes) {
                        $scope.addressTypes = addressTypes;
                    });

                    ObjectLookupService.getCountries().then(function(countries) {
                        $scope.countries = countries;
                    });

                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT).then(function(personTypes) {
                        $scope.personTypes = personTypes;
                        return personTypes;
                    });
                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT, true).then(function(personTypes) {
                        $scope.personTypesInitiator = personTypes;
                        return personTypes;
                    });

                    UserInfoService.getUserInfo().then(function(data) {
                        $scope.complaint.defaultAddress.creator = data.fullName;
                    });

                    // ---------------------------            initiator         --------------------------------------
                    var newPersonAssociation = function() {
                        return {
                            id: null,
                            personType: "",
                            parentId: $scope.complaint.complaintId,
                            parentType: ObjectService.ObjectTypes.COMPLAINT,
                            parentTitle: $scope.complaint.complaintNumber,
                            personDescription: "",
                            notes: "",
                            person: null,
                            className: "com.armedia.acm.plugins.person.model.PersonAssociation"
                        };
                    };

                    $scope.addPersonInitiator = function() {
                        pickPerson(null);
                    };

                    function pickPerson(association) {

                        var params = {};
                        // params.types = $scope.personTypes;
                        params.types = $scope.personTypesInitiator;
                        // if (association) {
                        //     if (association.personType == initiatorType) {
                        //         //change the types only for initiator
                        //         params.types = $scope.personTypesInitiator;
                        //     }
                        //     angular.extend(params, {
                        //         personId: association.person.id,
                        //         personName: association.person.givenName + ' ' + association.person.familyName,
                        //         type: association.personType,
                        //         selectExistingEnabled: association.personType == initiatorType ? true : false,
                        //         typeEnabled: association.personType == initiatorType ? false : true,
                        //         description: association.personDescription
                        //     });
                        // } else {
                        association = new newPersonAssociation();
                        // }

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
                                    $scope.complaint.initiator = person.givenName + " " + person.familyName;
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
                            $scope.complaint.personAssociations.push(association);
                        }
                        // saveObjectInfoAndRefresh();
                    }

                    // -------------------  people --------------------------------------------------------------------
                    $scope.addPerson = function() {
                        $timeout(function() {
                            $scope.searchPerson(-1);
                        }, 0);
                    };

                    $scope.removePerson = function(person) {
                        $timeout(function() {
                            _.remove($scope.complaint.personAssociations, function(object) {
                                return object === person;
                            });
                        }, 0);
                    };

                    $scope.searchPerson = function(index, isNewComplaint) {
                        var associationFound = _.find($scope.complaint.personAssociations, function(item) {
                            return !Util.isEmpty(item) && !Util.isEmpty(item.complaint);
                        });
                        var association = index > -1 ? $scope.complaint.personAssociations[index] : {};
                        var params = {
                            showSetPrimary: true,
                            isDefault: false,
                            types: $scope.personTypes,
                            isFirstPerson: Util.isEmpty(associationFound) ? true : false
                        };

                        //set this params for editing
                        if (association.person) {
                            angular.extend(params, {
                                selectExistingEnabled: false,
                                personId: association.person.id,
                                personName: association.person.givenName + ' ' + association.person.familyName,
                                type: association.personType,
                                isDefault: Util.isEmpty(association.primaryContact) ? true : false
                            });
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
                            //new added person
                            if (data.person) {
                                if (!data.person.id) {
                                    PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                                        data.person = response.data;
                                        setPersonAssociation(association, data);
                                    });
                                } else {
                                    setPersonAssociation({}, data);
                                }
                                //selected from existing people
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    data.person = person;
                                    setPersonAssociation(association, data);
                                });
                            }
                        });
                    };

                    function setPersonAssociation(association, data) {
                        association.person = data.person;
                        // association.complaint = $scope.complaint;
                        association.personType = data.type;
                        association.parentId = $scope.complaint.complaintId;
                        association.parentType = ObjectService.ObjectTypes.COMPLAINT;
                        association.parentTitle = $scope.complaint.complaintNumber;

                        if (data.isDefault) {
                            //find and change previously primary contact
                            var defaultAssociation = _.find($scope.complaint.personAssociations, function(object) {
                                return object.primaryContact;
                            });
                            if (defaultAssociation) {
                                defaultAssociation.primaryContact = false;
                            }
                        }
                        // association.primaryContact = data.isDefault;

                        //those are temporary values for displaying in the input
                        association.personFullName = association.person.givenName + ' ' + association.person.familyName;

                        //if is new created, add it to the organization associations list
                        if (!$scope.complaint.personAssociations) {
                            $scope.complaint.personAssociations = [];
                        }

                        if (!_.includes($scope.complaint.personAssociations, association)) {
                            $scope.complaint.personAssociations.push(association);
                        }
                    }

                    //--------------------------------        attachments        ---------------------------------------
                    $scope.upload = function upload(files) {
                        $scope.selectedFiles = files;
                    };

                    // ----------------------------    save complaint  ------------------------------------------------
                    function saveObjectInfoAndRefresh() {
                        var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                        if (ComplaintInfoService.validateComplaintInfo($scope.complaint)) {
                            var complaint = Util.omitNg($scope.complaint);
                            promiseSaveInfo = ComplaintInfoService.saveComplaintInfo(complaint);
                            promiseSaveInfo.then(function(complaint) {
                                $scope.$emit("report-object-updated", complaint);
                                return complaint;
                            }, function(error) {
                                $scope.$emit("report-object-update-failed", error);
                                return error;
                            });
                        }
                        return promiseSaveInfo;
                    }

                    //-----------------------------------------------------------------------------------------------

                    $scope.save = function() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        var promiseSaveComplaint = ComplaintInfoService.saveComplaintInfoNewComplaint(clearNotFilledElements(_.cloneDeep($scope.complaint)));
                        promiseSaveComplaint.then(function(objectInfo) {
                            var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COMPLAINT);
                            var complaintCreatedMessage = $translate.instant('{{objectType}} {{complaintTitle}} was created.', {
                                objectType: objectTypeString,
                                complaintTitle: objectInfo.complaintTitle
                            });
                            MessageService.info(complaintCreatedMessage);
                            ObjectService.showObject(ObjectService.ObjectTypes.COMPLAINT, objectInfo.complaintId);
                            $scope.onModalClose();
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-floppy-o";
                        }, function(error) {
                            $scope.loading = false;
                            $scope.loadingIcon = "fa fa-floppy-o";
                            if (error.data && error.data.message) {
                                $scope.error = error.data.message;
                            } else {
                                MessageService.error(error);
                            }
                        });
                    };

                    function clearNotFilledElements(complaint) {

                        //addresses
                        if (complaint.defaultAddress) {
                            if (!complaint.defaultAddress.streetAddress) {
                                complaint.defaultAddress = null;
                            } else {
                                complaint.addresses.push(complaint.defaultAddress);
                            }
                        }

                        if (Util.isEmpty(complaint.details)) {
                            complaint.details = null;
                        }
                        //priority: 'Low'
                        //incidentDate: new Date(),
                        if (Util.isEmpty(complaint.tag)) {
                            complaint.tag = null;
                        }

                        if (Util.isEmpty(complaint.frequency)) {
                            complaint.frequency = null;
                        }

                        // initiator: '',
                        // addresses: [],
                        // personAssociations: [ {} ]

                        //remove empty complaint before save
                        _.remove(complaint.personAssociations, function(association) {
                            if (!association.personFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete association['personFullName'];
                                return false;
                            }
                        });

                        return complaint;
                    }

                    $scope.cancelModal = function() {
                        $scope.onModalDismiss();
                    };
                } ]);
