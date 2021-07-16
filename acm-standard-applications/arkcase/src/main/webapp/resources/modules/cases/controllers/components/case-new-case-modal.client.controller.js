'use strict';

angular.module('cases').controller(
        'Cases.NewCaseController',
        [ '$scope', '$stateParams', '$translate', '$modalInstance', 'Case.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
                'Profile.UserInfoService', 'Mentions.Service',
                function($scope, $stateParams, $translate, $modalInstance, CaseInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService, MentionsService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.selectedFiles = [];
                    $scope.userSearchConfig = null;
                    $scope.isEdit = $scope.modalParams.isEdit;

                    ConfigService.getModuleConfig("cases").then(function(moduleConfig) {
                        $scope.config = moduleConfig;

                        if (!$scope.isEdit) {
                            //new casefile with predefined values
                            $scope.casefile = {
                                className: $scope.config.className,
                                caseType: '',
                                title: '',
                                details: '',
                                initiator: '',
                                personAssociations: [ {} ],
                                participants: []
                            };
                        }

                        $scope.userSearchConfig = _.find(moduleConfig.components, {
                            id: "userSearch"
                        });
                        $scope.caseParticipantConfig = _.find(moduleConfig.components, {
                            id: "participants"
                        });
                        $scope.casePeopleConfig = _.find(moduleConfig.components, {
                            id: "people"
                        });

                        return moduleConfig;
                    });

                    var assocTypeLabel = $translate.instant("cases.comp.people.type.label");

                    // --------------  mention --------------
                    $scope.params = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    if ($scope.isEdit) {

                        $scope.objectInfo = $scope.modalParams.casefile;
                        var tmpCasefile = $scope.modalParams.casefile;

                        if (tmpCasefile.personAssociations != undefined) {
                            _.forEach(tmpCasefile.personAssociations, function(personAssociation) {
                                personAssociation.personFullName = personAssociation.person.givenName + " " + personAssociation.person.familyName;
                            });
                        }

                        if (tmpCasefile.participants != undefined) {
                            _.forEach(tmpCasefile.participants, function(participant) {
                                if (participant.participantType == "*" || participant.participantType == "owning group") {
                                    participant.participantFullName = participant.participantLdapId;
                                } else {
                                    UserInfoService.getUserInfoById(participant.participantLdapId).then(function(userInfo) {
                                        participant.participantFullName = userInfo.fullName;
                                    }, function(err) {
                                        participant.participantFullName = participant.participantLdapId;
                                    })
                                }
                            });
                        }

                        $scope.casefile = {
                            caseType: $scope.objectInfo.caseType,
                            title: $scope.objectInfo.title,
                            details: $scope.objectInfo.details,
                            initiator: $scope.objectInfo.originator.person.givenName + " " + $scope.objectInfo.originator.person.familyName,
                            personAssociations: tmpCasefile.personAssociations,
                            participants: tmpCasefile.participants
                        };
                    }

                    ObjectLookupService.getCaseFileTypes().then(function(caseTypes) {
                        $scope.caseCategory = caseTypes;
                        $scope.casefile.caseType = ObjectLookupService.getPrimaryLookup($scope.caseCategory);
                    });

                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE).then(function(personTypes) {
                        $scope.personTypes = personTypes;
                        return personTypes;
                    });
                    ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE, true).then(function(personTypes) {
                        $scope.personTypesInitiator = personTypes;
                        $scope.initiatorType = ObjectLookupService.getPrimaryLookup($scope.personTypesInitiator);
                        return personTypes;
                    });

                    ObjectLookupService.getCaseFileParticipantTypes().then(function(caseFileParticipantTypes) {
                        $scope.caseFileParticipantTypes = caseFileParticipantTypes;
                    });

                    // ---------------------------            initiator         --------------------------------------
                    var newPersonAssociation = function() {
                        return {
                            id: null,
                            personType: "",
                            parentId: $scope.casefile.id,
                            parentType: ObjectService.ObjectTypes.CASE_FILE,
                            parentTitle: $scope.casefile.caseNumber,
                            personDescription: "",
                            notes: "",
                            person: null,
                            className: $scope.casePeopleConfig.personAssociationClassName
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
                                    $scope.casefile.initiator = data.person.givenName + " " + data.person.familyName;
                                    updatePersonAssociationData(association, data.person, data);
                                });
                            } else {
                                PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                                    $scope.casefile.initiator = person.givenName + " " + person.familyName;
                                    updatePersonAssociationData(association, person, data);
                                });
                            }
                        });
                    }

                    function updatePersonAssociationData(association, person, data) {
                        association.person = person;
                        association.personType = data.type;
                        association.personDescription = data.description;
                        association.parentTitle = $scope.casefile.caseNumber;
                        if (!association.id) {
                            $scope.casefile.originator = association;
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
                            _.remove($scope.casefile.personAssociations, function(object) {
                                return object === person;
                            });
                        }, 0);
                    };

                    $scope.searchPerson = function(index, isNewCase) {
                        var associationFound = _.find($scope.casefile.personAssociations, function(item) {
                            return !Util.isEmpty(item) && !Util.isEmpty(item.casefile);
                        });
                        var association = index > -1 ? $scope.casefile.personAssociations[index] : {};
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
                        association.parentId = $scope.casefile.id;
                        association.parentType = ObjectService.ObjectTypes.CASE_FILE;
                        association.parentTitle = $scope.casefile.caseNumber;

                        if (data.isDefault) {
                            //find and change previously primary contact
                            var defaultAssociation = _.find($scope.casefile.personAssociations, function(object) {
                                return object.primaryContact;
                            });
                            if (defaultAssociation) {
                                defaultAssociation.primaryContact = false;
                            }
                        }

                        //those are temporary values for displaying in the input
                        association.personFullName = association.person.givenName + ' ' + association.person.familyName;

                        //if is new created, add it to the organization associations list
                        if (!$scope.casefile.personAssociations) {
                            $scope.casefile.personAssociations = [];
                        }

                        if (!_.includes($scope.casefile.personAssociations, association)) {
                            $scope.casefile.personAssociations.push(association);
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
                            _.remove($scope.casefile.participants, function(object) {
                                return object === participant;
                            });

                            _.remove($scope.participantsWithoutOwningGroup, function(object) {
                                return object === participant;
                            });
                        }, 0);
                    };

                    $scope.userOrGroupSearch = function(index) {
                        var participant = {};
                        var typeOwningGroup = "owning group";
                        var typeAssignee = "assignee";
                        var typeOwner = "owner";

                        //only one assignee(owner), if there is already one added, no option to add another one
                        for ( var i in $scope.casefile.participants) {
                            if ($scope.casefile.participants[i].participantType == typeAssignee) {
                                for ( var j in $scope.caseFileParticipantTypes) {
                                    if ($scope.caseFileParticipantTypes[j].key == typeAssignee) {
                                        $scope.caseFileParticipantTypes.splice(j, 1);
                                    }
                                }
                            } else {
                                ObjectLookupService.getCaseFileParticipantTypes().then(function(caseFileParticipantTypes) {
                                    $scope.caseFileParticipantTypes = caseFileParticipantTypes;
                                });
                            }
                        }

                        participant.participantTypes = $scope.caseFileParticipantTypes;
                        participant.replaceChildrenParticipant = true;

                        var modalScope = $scope.$new();
                        modalScope.participant = participant;
                        modalScope.isEdit = false;
                        modalScope.showReplaceChildrenParticipants = true;
                        modalScope.selectedType = participant.selectedType ? participant.selectedType : "";
                        modalScope.config = $scope.caseParticipantConfig;

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

                                var assignee = ObjectModelService.getParticipantByType($scope.casefile, typeAssignee);
                                var typeNoAccess = 'No Access';

                                participant.participantLdapId = data.participant.participantLdapId;

                                if (data.participant.participantType == typeNoAccess && assignee == data.participant.participantLdapId) {
                                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.noAccessCombo"));
                                } else {
                                    if (data.owningGroup != null) {
                                        $scope.owningGroup = data.owningGroup.participantFullName;
                                        var participantOwningGroup = data.owningGroup;
                                        participantOwningGroup.participantType = typeOwningGroup;
                                        participantOwningGroup.className = $scope.caseParticipantConfig.className;

                                        if (ObjectParticipantService.validateParticipants([ participantOwningGroup ], true) && !_.includes($scope.casefile.participants, participantOwningGroup)) {
                                            $scope.casefile.participants.push(participantOwningGroup);
                                        }
                                    }

                                    participant.id = data.participant.id;
                                    participant.participantType = data.participant.participantType;
                                    participant.className = $scope.caseParticipantConfig.className;
                                    participant.replaceChildrenParticipant = data.participant.replaceChildrenParticipant;

                                    if (ObjectParticipantService.validateParticipants([ participant ], true) && !_.includes($scope.casefile.participants, participant)) {
                                        $scope.casefile.participants.push(participant);

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

                    $scope.save = function() {

                        if (!$scope.isEdit) {
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            CaseInfoService.saveCaseInfoNewCase(clearNotFilledElements(_.cloneDeep($scope.casefile))).then(function(objectInfo) {
                                var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.CASE_FILE);
                                MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned, ObjectService.ObjectTypes.CASE_FILE, ObjectService.ObjectTypes.CASE_FILE, objectInfo.id, objectInfo.title);
                                MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.CASE_FILE, "DETAILS", objectInfo.id, objectInfo.details);
                                var caseCreatedMessage = $translate.instant('{{objectType}} {{caseTitle}} was created.', {
                                    objectType: objectTypeString,
                                    caseTitle: objectInfo.title
                                });
                                MessageService.info(caseCreatedMessage);
                                ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, objectInfo.id);
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
                        } else {
                            // Updates the ArkCase database when the user changes a case attribute
                            // from the form accessed by clicking 'Edit' and then 'update case file' button
                            $scope.loading = true;
                            $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                            var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                            checkForChanges($scope.objectInfo);
                            if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                                var objectInfo = Util.omitNg($scope.objectInfo);
                                promiseSaveInfo = CaseInfoService.saveCaseInfo(objectInfo);
                                promiseSaveInfo.then(function(caseInfo) {
                                    $scope.$emit("report-object-updated", caseInfo);
                                    var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.CASE_FILE);
                                    MentionsService.sendEmailToMentionedUsers($scope.params.emailAddresses, $scope.params.usersMentioned, ObjectService.ObjectTypes.CASE_FILE, ObjectService.ObjectTypes.CASE_FILE, caseInfo.id, caseInfo.title);
                                    MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.CASE_FILE, "DETAILS", caseInfo.id, caseInfo.details);
                                    var caseUpdatedMessage = $translate.instant('{{objectType}} {{caseTitle}} was updated.', {
                                        objectType: objectTypeString,
                                        caseTitle: caseInfo.title
                                    });
                                    MessageService.info(caseUpdatedMessage);
                                    $modalInstance.dismiss();
                                    $scope.loading = false;
                                    $scope.loadingIcon = "fa fa-floppy-o";
                                }, function(error) {
                                    $scope.$emit("report-object-update-failed", error);
                                    $scope.loading = false;
                                    $scope.loadingIcon = "fa fa-floppy-o";
                                    if (error.data && error.data.message) {
                                        $scope.error = error.data.message;
                                    } else {
                                        MessageService.error(error);
                                    }
                                });
                            }
                            return promiseSaveInfo;
                        }
                    };

                    function checkForChanges(objectInfo) {
                        if (objectInfo.title != $scope.casefile.title) {
                            objectInfo.title = $scope.casefile.title
                        }
                        if (objectInfo.caseType != $scope.casefile.caseType) {
                            objectInfo.caseType = $scope.casefile.caseType
                        }
                        if ($scope.casefile.originator != undefined && objectInfo.originator.person != $scope.casefile.originator.person) {
                            objectInfo.originator.person = $scope.casefile.originator.person
                        }
                        if (objectInfo.details != $scope.casefile.details) {
                            objectInfo.details = $scope.casefile.details
                        }
                        return objectInfo;
                    }

                    function clearNotFilledElements(casefile) {

                        if (Util.isEmpty(casefile.details)) {
                            casefile.details = null;
                        }

                        //remove empty casefile before save
                        _.remove(casefile.personAssociations, function(association) {
                            if (!association.personFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete association['personFullName'];
                                return false;
                            }
                        });

                        _.remove(casefile.participants, function(participant) {
                            if (!participant.participantFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete participant['participantFullName'];
                                return false;
                            }
                        });

                        return casefile;
                    }

                    $scope.cancelModal = function() {
                        $modalInstance.dismiss();
                    };

                } ]);
