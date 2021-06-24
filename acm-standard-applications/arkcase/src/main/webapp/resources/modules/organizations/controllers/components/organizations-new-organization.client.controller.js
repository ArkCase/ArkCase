'use strict';

angular.module('organizations').controller(
        'Organizations.NewOrganizationController',
    ['$scope', '$stateParams', '$translate', '$q', 'Organization.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Person.InfoService', 'ObjectService', 'modalParams', 'Mentions.Service', 'PhoneValidationService', 'SimilarOrganizationService',
        function ($scope, $stateParams, $translate, $q, OrganizationInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, PersonInfoService, ObjectService, modalParams, MentionsService, PhoneValidationService, SimilarOrganizationService) {

                    $scope.modalParams = modalParams;
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.showPhoneError = false;
                    $scope.showFaxError = false;

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

                    ObjectLookupService.getPersonOrganizationRelationTypes().then(function(types) {
                        $scope.personAssociationTypes = types;
                        return types;
                    });

            $scope.organizationExists = function() {
                SimilarOrganizationService.getSimilarOrganizationsByName($scope.organization.organizationValue).then(function (result){
                    var organizationExists = result.data.response.numFound;
                    if (organizationExists > 0) {
                        var params = {};
                        params.header = $translate.instant("common.dialogOrganizationPicker.header2");
                        params.config = Util.goodMapValue($scope.config, "dialogOrganizationPicker");
                        params.organizations = result.data.response.docs;
                        params.isFromNewOrganizationModal = true;

                        var modalInstance = $modal.open({
                            templateUrl: "modules/common/views/organization-exists-modal.client.view.html",
                            controller:"Common.OrganizationExistsModalController",
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function () {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function (selected) {
                            if (!Util.isEmpty(selected)) {
                                $state.go('organizations.main', {
                                    id: selected.object_id_s,
                                }, true);
                            }
                        });
                    }
                });
            };

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
                        association.organizationToPersonAssociationType = data.inverseType;
                        association.personToOrganizationAssociationType = data.type;

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

            var promiseGetStates = ObjectLookupService.getStates();

            var promiseGetCountries = ObjectLookupService.getCountries();

            var promiseGetAdressTypes = ObjectLookupService.getAddressTypes();

            $q.all([promiseGetAdressTypes, promiseGetCountries, promiseGetStates]).then(function (data) {
                $scope.states = data[2];
                $scope.countries = data[1];
                $scope.addressTypes = data[0];
            });

            ObjectLookupService.getOrganizationTypes().then(function (organizationTypes) {
                $scope.organizationTypes = organizationTypes;
                var defaultOrganizationType = ObjectLookupService.getPrimaryLookup($scope.organizationTypes);
                if (defaultOrganizationType) {
                    $scope.organization.organizationType = defaultOrganizationType.key;
                }
            });

            ObjectLookupService.getPersonOrganizationRelationTypes().then(function (personOrganizationRelationTypes) {
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
                $scope.updateStates($scope.state);
            };

            $scope.updateStates = function (state) {
                if (!Util.isEmpty(state)) {
                    ObjectLookupService.getLookupByLookupName($scope.state).then(function (states) {
                        $scope.states = states;
                    });
                }
            };

            $scope.checkLocationRules = function (address) {
                return !_.values(address).every(_.isEmpty)
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
                            var defaultAddressType = ObjectLookupService.getPrimaryLookup($scope.addressTypes);
                            var defaultCountry = ObjectLookupService.getPrimaryLookup($scope.countries);

                            //add empty address
                            $scope.organization.addresses.push({
                                type: defaultAddressType ? defaultAddressType.key : null,
                                country: defaultCountry ? defaultCountry.key : null
                            });
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
                            // this is rare scenario in identifications when user choose only issuer date for example and then remove this date
                            // we need to delete all properties that are null cause otherwise backend will throw error
                            organization.defaultIdentification = _.pick(organization.defaultIdentification, _.identity);
                            if (_.isEmpty(organization.defaultIdentification)) {
                                organization = _.omit(organization, ['defaultIdentification']);
                            } else {
                                organization.identifications.push(organization.defaultIdentification);
                            }
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

            $scope.validatePhone = function (type, data, isDefaultPhone) {
                if (type === 'phone' || type === 'fax') {
                    var validateObject = PhoneValidationService.validateInput(data.value, regEx);
                    data.value = validateObject.inputValue;
                    if (isDefaultPhone) {
                        $scope['show' + $scope.capitalizeFirstLetter(type) + 'Error'] = validateObject.showPhoneError;
                    } else {
                        data['show' + $scope.capitalizeFirstLetter(type) + 'Error'] = validateObject.showPhoneError;
                    }
                }
            }

        }]);
