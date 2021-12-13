'use strict';

angular.module('complaints').controller(
        'Complaints.NewComplaintController',
        [
                '$scope',
                '$stateParams',
                '$translate',
                '$modalInstance',
                'Complaint.InfoService',
                '$state',
                'Object.LookupService',
                'MessageService',
                '$timeout',
                'UtilService',
                '$modal',
                'ConfigService',
                'Organization.InfoService',
                'ObjectService',
                'modalParams',
                'Person.InfoService',
                'Profile.UserInfoService',
                'Object.ModelService',
                'Object.ParticipantService',
                'Mentions.Service',
                'Admin.ObjectTitleConfigurationService',
                function($scope, $stateParams, $translate, $modalInstance, ComplaintInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, OrganizationInfoService, ObjectService, modalParams, PersonInfoService, UserInfoService, ObjectModelService,
                        ObjectParticipantService, MentionsService, AdminObjectTitleConfigurationService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.selectedFiles = [];
                    $scope.userSearchConfig = null;
                    $scope.enableTitle = false

                    AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function(data){
                        $scope.enableTitle = data.data.COMPLAINT.enableTitleField;
                    });

                    ConfigService.getModuleConfig("complaints").then(function(moduleConfig) {
                        $scope.config = moduleConfig;

                        //new complaint with predefined values
                        $scope.complaint = {
                            className: $scope.config.className,
                            complaintType: '',
                            complaintTitle: '',
                            details: '',
                            priority: 'Low',
                            incidentDate: new Date(),
                            tag: '',
                            frequency: '',
                            initiator: '',
                            addresses: [],
                            personAssociations: [ {} ],
                            participants: []
                        };

                        $scope.userSearchConfig = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });
                        $scope.complaintParticipantConfig = _.find(moduleConfig.components, {
                            id: "participants"
                        });
                        $scope.complaintPeopleConfig = _.find(moduleConfig.components, {
                            id: "people"
                        });

                        return moduleConfig;
                    });

                    $scope.isAddressTypeSelected = false;
                    $scope.selectChanged = function() {
                        if ($scope.complaint.defaultAddress.type)
                            $scope.isAddressTypeSelected = true;
                        else
                            $scope.isAddressTypeSelected = false;
                    };
                    var assocTypeLabel = $translate.instant("complaints.comp.people.type.label");

                    ObjectLookupService.getComplaintTypes().then(function(complaintTypes) {
                        $scope.incidentCategory = complaintTypes;
                        var defaultComplaintType = ObjectLookupService.getPrimaryLookup(complaintTypes);
                        if (defaultComplaintType) {
                            $scope.complaint.complaintType = defaultComplaintType.key;
                        }
                    });

                    ObjectLookupService.getPriorities().then(function(priorities) {
                        $scope.priorities = priorities;
                        var defaultPriority = ObjectLookupService.getPrimaryLookup($scope.priorities);
                        if (defaultPriority) {
                            $scope.complaint.priority = defaultPriority.key;
                        }
                    });

                    ObjectLookupService.getFrequencies().then(function(frequencies) {
                        $scope.frequencies = frequencies;
                        var defaultFrequency = ObjectLookupService.getPrimaryLookup($scope.frequencies);
                        if (defaultFrequency) {
                            $scope.complaint.frequency = defaultPriority.key;
                        }
                    });

                    ObjectLookupService.getAddressTypes().then(function (addressTypes) {
                        $scope.addressTypes = addressTypes;
                    });

                    ObjectLookupService.getStates().then(function (states) {
                        $scope.states = states;
                    });

                    ObjectLookupService.getCountries().then(function (countries) {
                        $scope.countries = countries;
                    });

                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT).then(function (personTypes) {
                        $scope.personTypes = personTypes;
                        return personTypes;
                    });
                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.COMPLAINT, true).then(function(personTypes) {
                        $scope.personTypesInitiator = personTypes;
                        $scope.initiatorType = ObjectLookupService.getPrimaryLookup($scope.personTypesInitiator);
                        return personTypes;
                    });

                    ObjectLookupService.getComplaintParticipantTypes().then(function(complaintParticipantTypes) {
                        $scope.complaintParticipantTypes = complaintParticipantTypes;
                    });

                    $scope.changeStates = function (country) {
                        $scope.state = "";
                        if (country == 'US') {
                            $scope.state = 'states';
                        } else if (country == 'CA') {
                            $scope.state = 'canadaProvinces';
                        } else if (country == 'JP') {
                            $scope.state = 'japanStates';
                        }
                        $scope.updateStates($scope.state);
                    };

                    $scope.updateStates = function (state) {
                        if (!Util.isEmpty(state)) {
                            ObjectLookupService.getLookupByLookupName($scope.state).then(function (states) {
                                $scope.states = states;
                            });
                        }
                    };

                    // --------------  mention --------------
                    $scope.params = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

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
                            className: $scope.complaintPeopleConfig.personAssociationClassName
                        };
                    };

                    $scope.addPersonInitiator = function() {
                        pickPerson(null);
                    };

                    function pickPerson(association) {

                        var params = {};
                        params.types = $scope.personTypesInitiator;
                        params.type = $scope.initiatorType;
                        params.typeEnabled = false;
                        association = new newPersonAssociation();
                        params.assocTypeLabel = assocTypeLabel;

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
                                    $scope.complaint.initiator = data.person.givenName + " " + data.person.familyName;
                                    updatePersonAssociationData(association, data.person, data);
                                });
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    $scope.complaint.initiator = person.givenName + " " + person.familyName;
                                    updatePersonAssociationData(association, person, data);
                                });
                            }
                        });
                    }

                    function updatePersonAssociationData(association, person, data) {
                        association.person = person;
                        association.personType = data.type;
                        association.personDescription = data.description;
                        association.parentTitle = $scope.complaint.complaintNumber;
                        if (!association.id) {
                            $scope.complaint.originator = association;
                        }
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
                            isFirstPerson: Util.isEmpty(associationFound) ? true : false,
                            assocTypeLabel: assocTypeLabel
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

                    //-----------------------------------   participants    -------------------------------------------
                    $scope.participantsWithoutOwningGroup = [ {} ];
                    $scope.addParticipant = function() {
                        $timeout(function() {
                            $scope.userOrGroupSearch(-1);
                        }, 0);
                    };

                    $scope.removeParticipant = function(participant) {
                        if (participant.participantType == "owning group") {
                            $scope.owningGroup = null;
                        }
                        $timeout(function() {
                            _.remove($scope.complaint.participants, function(object) {
                                return object === participant;
                            });

                            _.remove($scope.participantsWithoutOwningGroup, function(object) {
                                return object === participant;
                            });
                        }, 0);
                    };

                    $scope.userOrGroupSearch = function(index) {
                        var participant = {}; //index > -1 ? $scope.complaint.participants[index] : {};
                        var typeOwningGroup = "owning group";
                        var typeAssignee = "assignee";
                        var typeOwner = "owner";

                        //only one assignee, if there is already one added, no option to add another one
                        for ( var i in $scope.complaint.participants) {
                            if ($scope.complaint.participants[i].participantType == typeAssignee) {
                                for ( var j in $scope.complaintParticipantTypes) {
                                    if ($scope.complaintParticipantTypes[j].key == typeAssignee) {
                                        $scope.complaintParticipantTypes.splice(j, 1);
                                    }
                                }
                            } else {
                                ObjectLookupService.getComplaintParticipantTypes().then(function(complaintParticipantTypes) {
                                    $scope.complaintParticipantTypes = complaintParticipantTypes;
                                });
                            }
                        }

                        participant.participantTypes = $scope.complaintParticipantTypes;
                        participant.replaceChildrenParticipant = true;

                        var modalScope = $scope.$new();
                        modalScope.participant = participant;
                        modalScope.isEdit = false;
                        modalScope.showReplaceChildrenParticipants = true;
                        modalScope.selectedType = participant.selectedType ? participant.selectedType : "";
                        modalScope.config = $scope.complaintParticipantConfig;

                        var params = {};

                        var modalInstance = $modal.open({
                            scope: modalScope,
                            animation: true,
                            templateUrl: "directives/core-participants/core-participants-create-new-object-modal.client.view.html",
                            controller: "Directives.CoreParticipantsCreateNewObjectFormModalController",
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (ObjectParticipantService.validateType(data.participant, data.selectedType)) {

                                var assignee = ObjectModelService.getParticipantByType($scope.complaint, typeAssignee);
                                var typeNoAccess = 'No Access';
                                participant.participantLdapId = data.participant.participantLdapId;

                                if (data.participant.participantType == typeNoAccess && assignee == data.participant.participantLdapId) {
                                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.noAccessCombo"));
                                } else {
                                    if (data.owningGroup != null) {
                                        $scope.owningGroup = data.owningGroup.participantFullName;
                                        var participantOwningGroup = data.owningGroup;
                                        participantOwningGroup.participantType = typeOwningGroup;
                                        participantOwningGroup.className = $scope.complaintParticipantConfig.className;

                                        if (ObjectParticipantService.validateParticipants([ participantOwningGroup ], true) && !_.includes($scope.complaint.participants, participantOwningGroup)) {
                                            $scope.complaint.participants.push(participantOwningGroup);
                                        }
                                    }
                                    participant.id = data.participant.id;
                                    participant.participantType = data.participant.participantType;
                                    participant.className = $scope.complaintParticipantConfig.className;
                                    participant.replaceChildrenParticipant = data.participant.replaceChildrenParticipant;

                                    if (ObjectParticipantService.validateParticipants([ participant ], true) && !_.includes($scope.complaint.participants, participant)) {
                                        $scope.complaint.participants.push(participant);

                                        if (participant.participantType == typeAssignee) {
                                            participant.participantTypeFormatted = typeOwner;
                                        } else {
                                            participant.participantTypeFormatted = participant.participantType;
                                        }
                                        if ($scope.participantsWithoutOwningGroup.length > 0) {
                                            if ($scope.participantsWithoutOwningGroup[0].participantLdapId == undefined)
                                                $scope.participantsWithoutOwningGroup.splice(0);
                                        }
                                        $scope.participantsWithoutOwningGroup.push(participant);
                                    }
                                }
                            }
                        });
                    };

                    //-----------------------------------------------------------------------------------------------

                    $scope.addAddress = function () {
                        $timeout(function () {
                            //add empty address
                            var defaultAddressType = ObjectLookupService.getPrimaryLookup($scope.addressTypes);
                            var defaultCountry = ObjectLookupService.getPrimaryLookup($scope.countries);

                            $scope.complaint.addresses.push({
                                type: defaultAddressType ? defaultAddressType.key : null,
                                country: defaultCountry ? defaultCountry.key : null
                            });
                        }, 0);
                    };

                    $scope.removeAddress = function (address) {
                        $timeout(function () {
                            _.remove($scope.complaint.addresses, function (object) {
                                return object === address;
                            });
                        }, 0);
                    };


                    $scope.save = function() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        ComplaintInfoService.saveComplaintInfoNewComplaint(clearNotFilledElements(_.cloneDeep($scope.complaint))).then(function(objectInfo) {
                            var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.COMPLAINT);
                            MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned, ObjectService.ObjectTypes.COMPLAINT, ObjectService.ObjectTypes.COMPLAINT, objectInfo.complaintId, objectInfo.complaintTitle);
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.COMPLAINT, "DETAILS", objectInfo.complaintId, objectInfo.details);
                            var complaintCreatedMessage = $translate.instant('{{objectType}} {{complaintTitle}} was created.', {
                                objectType: objectTypeString,
                                complaintTitle: objectInfo.complaintTitle
                            });
                            MessageService.info(complaintCreatedMessage);
                            ObjectService.showObject(ObjectService.ObjectTypes.COMPLAINT, objectInfo.complaintId);
                            $modalInstance.dismiss();
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
                                delete complaint.defaultAddress['creatorFullName'];
                                complaint.addresses.push(complaint.defaultAddress);
                            }
                        }

                        if (Util.isEmpty(complaint.details)) {
                            complaint.details = null;
                        }

                        if (Util.isEmpty(complaint.tag)) {
                            complaint.tag = null;
                        }

                        if (Util.isEmpty(complaint.frequency)) {
                            complaint.frequency = null;
                        }

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

                        _.remove(complaint.participants, function(participant) {
                            if (!participant.participantFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete participant['participantFullName'];
                                return false;
                            }
                        });

                        return complaint;
                    }

                    $scope.cancelModal = function() {
                        $modalInstance.dismiss();
                    };

                    $scope.checkLocationRules = function (address) {
                        return !_.values(address).every(_.isEmpty)
                    };

                } ]);
