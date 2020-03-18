'use strict';

angular.module('organizations').controller(
        'Organizations.NewOrganizationController',
    ['$scope', '$stateParams', '$translate', 'Organization.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Person.InfoService', 'ObjectService', 'modalParams', 'Mentions.Service', 'PhoneValidationService',
        function ($scope, $stateParams, $translate, OrganizationInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, PersonInfoService, ObjectService, modalParams, MentionsService, PhoneValidationService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";

                    //used for showing/hiding buttons in communication accounts
                    var contactMethodsCounts = {
                        'url': 0,
                        'phone': 0,
                        'email': 0,
                        'fax': 0
                    };
                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.config = moduleConfig;
                        return moduleConfig;
                    });

                    //new organization with predefined values
                    $scope.organization = {
                        className: 'com.armedia.acm.plugins.person.model.Organization',
                        contactMethods: [],
                        identifications: [],
                        addresses: [],
                        personAssociations: [ {} ],
                        defaultEmail: {
                            type: 'email'
                        },
                        defaultPhone: {
                            type: 'phone'
                        },
                        defaultUrl: {
                            type: 'url'
                        },
                        defaultFax: {
                            type: 'fax'
                        },
                        details: ''
                    };

                    ObjectLookupService.getOrganizationPersonRelationTypes().then(function(types) {
                        $scope.personAssociationTypes = types;
                        return types;
                    });

                    $scope.searchOrganization = function() {
                        var params = {};
                        params.header = $translate.instant("common.dialogOrganizationPicker.header");
                        params.filter = '"Object Type": ORGANIZATION';
                        params.config = Util.goodMapValue($scope.config, "dialogOrganizationPicker");

                        var modalInstance = $modal.open({
                            templateUrl: "modules/common/views/object-picker-modal.client.view.html",
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.config = params.config;
                            } ],
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {
                                OrganizationInfoService.getOrganizationInfo(selected.object_id_s).then(function(response) {
                                    $scope.organization.parentOrganization = response;
                                });
                            }
                        });
                    };

                    $scope.addNewPerson = function() {
                        $timeout(function() {
                            $scope.searchPerson(-1);
                        }, 0);
                    };

                    $scope.removePerson = function(person) {
                        $timeout(function() {
                            _.remove($scope.organization.personAssociations, function(object) {
                                return object === person;
                            });
                        }, 0);
                    };

                    $scope.searchPerson = function(index, isNewOrganization) {
                        var associationFound = _.find($scope.organization.personAssociations, function(item) {
                            return !Util.isEmpty(item) && !Util.isEmpty(item.organization);
                        });
                        var association = index > -1 ? $scope.organization.personAssociations[index] : {};
                        var params = {
                            showSetPrimary: true,
                            isDefault: false,
                            types: $scope.personAssociationTypes,
                            isFirstPerson: Util.isEmpty(associationFound) ? true : false
                        };

                        //set this params for editing
                        if (association.person) {
                            angular.extend(params, {
                                selectExistingEnabled: false,
                                personId: association.person.id,
                                personName: association.person.givenName + ' ' + association.person.familyName,
                                type: association.organizationToPersonAssociationType,
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
                            if (data.person) {
                                if (!data.person.id) {
                                    PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                                        data.person = response.data;
                                        setPersonAssociation(association, data);
                                    });
                                } else {
                                    setPersonAssociation({}, data);
                                }
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
                        association.organization = $scope.organization;
                        association.organizationToPersonAssociationType = data.type;
                        association.personToOrganizationAssociationType = data.inverseType;

                        if (data.isDefault) {
                            //find and change previously primary contact
                            var defaultAssociation = _.find($scope.organization.personAssociations, function(object) {
                                return object.primaryContact;
                            });
                            if (defaultAssociation) {
                                defaultAssociation.primaryContact = false;
                            }
                        }
                        association.primaryContact = data.isDefault;

                        //those are temporary values for displaying in the input
                        association.personFullName = association.person.givenName + ' ' + association.person.familyName;
                        association.personToOrganizationAssociationTypeName = _.find($scope.personAssociationTypes, function(type) {
                            return type.type == data.type;
                        });

                        //if is new created, add it to the organization associations list
                        if (!$scope.organization.personAssociations) {
                            $scope.organization.personAssociations = [];
                        }

                        if (!_.includes($scope.organization.personAssociations, association)) {
                            $scope.organization.personAssociations.push(association);
                        }
                    }

                    //contact methods subtypes types
                    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
                        $scope.cmTypes = {};
                        _.each(contactMethodTypes, function(cmType) {
                            $scope.cmTypes[cmType.key] = cmType;
                        });

                        //used for generating the view for communication accounts
                        $scope.communicationAccountsTypes = [ 'phone', 'fax', 'email', 'url' ];
                    });

                    ObjectLookupService.getOrganizationIdTypes().then(function(identificationTypes) {
                        $scope.identificationTypes = identificationTypes;
                    });

                    ObjectLookupService.getStates().then(function(states) {
                        $scope.states = states;
                    });

                    ObjectLookupService.getCountries().then(function(countries) {
                        $scope.countries = countries;
                    });

                    ObjectLookupService.getAddressTypes().then(function(addressTypes) {
                        $scope.addressTypes = addressTypes;
                    });

                    ObjectLookupService.getOrganizationTypes().then(function(organizationTypes) {
                        $scope.organizationTypes = organizationTypes;
                    });

                    ObjectLookupService.getPersonOrganizationRelationTypes().then(function(personOrganizationRelationTypes) {
                        $scope.personOrganizationRelationTypes = personOrganizationRelationTypes;
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
                $scope.updateStates();
            };

            $scope.updateStates = function () {
                ObjectLookupService.getLookupByLookupName($scope.state).then(function (states) {
                    $scope.states = states;
                });
            };

            // ---------------------   mention   ---------------------------------
                    $scope.paramsSummernote = {
                        emailAddresses: [],
                        usersMentioned: []
                    };

                    $scope.addContactMethod = function(contactType) {
                        $timeout(function() {
                            contactMethodsCounts[contactType]++;
                            $scope.organization.contactMethods.push({
                                type: contactType
                            });
                        }, 0);
                    };

                    $scope.removeContactMethod = function(contact) {
                        $timeout(function() {
                            contactMethodsCounts[contact.type]--;
                            _.remove($scope.organization.contactMethods, function(object) {
                                return object === contact;
                            });
                        }, 0);
                    };

                    $scope.showAddAnotherContactMethod = function(contactType) {
                        return contactMethodsCounts[contactType] < 1;
                    };

                    $scope.addIdentification = function() {
                        $timeout(function() {
                            //add empty identification
                            $scope.organization.identifications.push({});
                        }, 0);
                    };

                    $scope.removeIdentification = function(identification) {
                        $timeout(function() {
                            _.remove($scope.organization.identifications, function(object) {
                                return object === identification;
                            });
                        }, 0);
                    };

                    $scope.addAddress = function() {
                        $timeout(function() {
                            //add empty address
                            $scope.organization.addresses.push({});
                        }, 0);
                    };

                    $scope.removeAddress = function(address) {
                        $timeout(function() {
                            _.remove($scope.organization.addresses, function(object) {
                                return object === address;
                            });
                        }, 0);
                    };

                    $scope.save = function() {
                        $scope.loading = true;
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                        var promiseSaveOrganization = OrganizationInfoService.saveOrganizationInfo(clearNotFilledElements(_.cloneDeep($scope.organization)));
                        promiseSaveOrganization.then(function(objectInfo) {
                            var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.ORGANIZATION);
                            var organizationCreatedMessage = $translate.instant('organizations.comp.newOrganization.informCreated', {
                                objectType: objectTypeString,
                                organizationValue: objectInfo.organizationValue
                            });
                            MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.ORGANIZATION, "DETAILS", objectInfo.organizationId, objectInfo.details);
                            MessageService.info(organizationCreatedMessage);
                            ObjectService.showObject(ObjectService.ObjectTypes.ORGANIZATION, objectInfo.organizationId);
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

                    function clearNotFilledElements(organization) {

                        //remove opened property added for the datePickers
                        if (organization.identifications && organization.identifications.length) {
                            organization.identifications = _.map(organization.identifications, function(obj) {
                                return _.omit(obj, 'opened');
                            });
                        }

                        //phones
                        if (!organization.defaultPhone.value) {
                            organization.defaultPhone = null;
                        } else {
                            organization.contactMethods.push(organization.defaultPhone);
                        }

                        //faxes
                        if (!organization.defaultFax.value) {
                            organization.defaultFax = null;
                        } else {
                            organization.contactMethods.push(organization.defaultFax);
                        }

                        //emails
                        if (!organization.defaultEmail.value) {
                            organization.defaultEmail = null;
                        } else {
                            organization.contactMethods.push(organization.defaultEmail);
                        }

                        //urls
                        if (!organization.defaultUrl.value) {
                            organization.defaultUrl = null;
                        } else {
                            organization.contactMethods.push(organization.defaultUrl);
                        }

                        //identifications
                        if (organization.defaultIdentification) {
                            organization.identifications.push(organization.defaultIdentification);
                        }

                        //addresses
                        if (organization.defaultAddress) {
                            if (!organization.defaultAddress.streetAddress) {
                                organization.defaultAddress = null;
                            } else {
                                organization.addresses.push(organization.defaultAddress);
                            }
                        }

                        //remove empty organizations before save
                        _.remove(organization.personAssociations, function(association) {
                            if (!association.personFullName) {
                                return true;
                            } else {
                                //remove temporary values
                                delete association['personFullName'];
                                delete association['personToOrganizationAssociationTypeName'];
                                return false;
                            }
                        });

                        return organization;
                    }

                    /**
                     * capitalize first Letter in the string
                     * @param input
                     * @returns capitalized string
                     */
                    $scope.capitalizeFirstLetter = function(input) {
                        return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
                    };
                    
                    $scope.cancelModal = function() {
                        $scope.onModalDismiss();
                    };

            var regEx = PhoneValidationService.getPhoneRegex().then(function (response) {
                var regExp = new RegExp(response.data);
                regEx = regExp;
            });

            $scope.validateInput = function (caType) {
                var inputType = caType;
                if (inputType == 'phone') {
                    var validateObject = PhoneValidationService.validateInput($scope.organization.defaultPhone.value, regEx);
                        $scope.organization.defaultPhone.value = validateObject.inputValue;
                        $scope.showPhoneError = validateObject.showPhoneError;
                }
            }
        }]);
