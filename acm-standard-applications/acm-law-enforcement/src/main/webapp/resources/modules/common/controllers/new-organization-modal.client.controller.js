'use strict';

angular.module('common').controller(
        'Common.NewOrganizationModalController',
    ['$scope', '$stateParams', '$translate', 'Organization.InfoService', '$state', 'Object.LookupService', 'UtilService', '$modal', 'ConfigService', 'MessageService', '$timeout', '$modalInstance', 'Person.InfoService', 'PhoneValidationService',
        function ($scope, $stateParams, $translate, OrganizationInfoService, $state, ObjectLookupService, Util, $modal, ConfigService, MessageService, $timeout, $modalInstance, PersonInfoService, PhoneValidationService) {

                    //used for showing/hiding buttons in communication accounts
                    var contactMethodsCounts = {
                        'url': 0,
                        'phone': 0,
                        'email': 0,
                        'fax': 0
                    };
                    $scope.loadingIcon = "fa fa-floppy-o";

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.config = moduleConfig;
                        return moduleConfig;
                    });

                    $scope.accordionSuffix = Math.floor((Math.random() * 1000) + 1);
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
                        }
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
                        params.addNewEnabled = false;

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

                    $scope.searchPerson = function(index) {
                        var associationFound = _.find($scope.organization.personAssociations, function(item) {
                            return !Util.isEmpty(item) && !Util.isEmpty(item.person);
                        });
                        var association = index > -1 ? $scope.organization.personAssociations[index] : {};
                        var params = {
                            showSetPrimary: true,
                            isDefault: false,
                            addNewEnabled: false,
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
                                        data['person'] = response.data;
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

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };

                    $scope.save = function() {
                        $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";

                        $modalInstance.close({
                            organization: clearNotFilledElements(_.cloneDeep($scope.organization))
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

                        $scope.loadingIcon = "fa fa-floppy-o";
                        return organization;
                    }

                    /**
                     * capitalize first Letter in the string
                     * @param input
                     * @returns capitalized string
                     */
                    $scope.capitalizeFirstLetter = function(input) {
                        return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
                    }

                    var regEx = PhoneValidationService.getPhoneRegex().then(function (response) {
                        var regExp = new RegExp(response.data);
                        regEx = regExp;
                    });

                    $scope.validateInput = function (caType) {
                        var inputType = caType;
                        if (inputType == 'phone') {
                        var validateObject = PhoneValidationService.validateInput($scope.organization.defaultPhone.value, regEx)
                        $scope.organization.defaultPhone.value = validateObject.inputValue;
                        $scope.showPhoneError = validateObject.showPhoneError;
                        }
                    }
        }]);
