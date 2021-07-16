'use strict';

angular.module('people').controller(
    'People.NewPersonController',
    ['$scope', '$stateParams', '$translate', '$q', 'Person.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Organization.InfoService', 'ObjectService', 'modalParams', 'Mentions.Service', 'PhoneValidationService',
        function ($scope, $stateParams, $translate, $q, PersonInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, OrganizationInfoService, ObjectService, modalParams, MentionsService, PhoneValidationService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";

            var assocTypeLabel = $translate.instant("people.comp.organizations.type.label");

            //used for showing/hiding buttons in communication accounts
            var contactMethodsCounts = {
                'url': 0,
                'phone': 0,
                'email': 0
            };

            ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                $scope.config = moduleConfig;
                return moduleConfig;
            });

            $scope.pictures = [{}];
            $scope.userPictures = [];
            $scope.previewImage = [];

            //new person with predefined values
            $scope.person = {
                className: 'gov.foia.model.FOIAPerson',
                contactMethods: [],
                identifications: [],
                addresses: [],
                organizationAssociations: [{}],
                defaultEmail: {
                    type: 'email'
                },
                defaultPhone: {
                    type: 'phone'
                },
                defaultUrl: {
                    type: 'url'
                },
                details: ''
            };

            //contact methods subtypes types
            ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
                $scope.cmTypes = {};
                _.each(contactMethodTypes, function (cmType) {
                    $scope.cmTypes[cmType.key] = cmType;
                });

                //used for generating the view for communication accounts
                $scope.communicationAccountsTypes = ['phone', 'email', 'url'];
            });

            ObjectLookupService.getIdentificationTypes().then(function (identificationTypes) {
                $scope.identificationTypes = identificationTypes;
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

            var promiseGetCountries = ObjectLookupService.getCountries();

            var promiseGetAdressTypes = ObjectLookupService.getAddressTypes();

            $q.all([promiseGetAdressTypes, promiseGetCountries]).then(function (data) {
                $scope.countries = data[1];
                $scope.addressTypes = data[0];
            });

            ObjectLookupService.getPersonOrganizationRelationTypes().then(function (organizationTypes) {
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

            ObjectLookupService.getPersonTitles().then(function (prefixes) {
                $scope.prefixes = prefixes;
                return prefixes;
            });

            // ---------------------   mention   ---------------------------------
            $scope.paramsSummernote = {
                emailAddresses: [],
                usersMentioned: []
            };

            $scope.addContactMethod = function (contactType) {
                $timeout(function () {
                    contactMethodsCounts[contactType]++;
                    $scope.person.contactMethods.push({
                        type: contactType
                    });
                }, 0);
            };

            $scope.removeContactMethod = function (contact) {
                $timeout(function () {
                    contactMethodsCounts[contact.type]--;
                    _.remove($scope.person.contactMethods, function (object) {
                        return object === contact;
                    });
                }, 0);
            };

            $scope.showAddAnotherContactMethod = function (contactType) {
                return contactMethodsCounts[contactType] < 1;
            };

            $scope.addIdentification = function () {
                $timeout(function () {
                    //add empty identification
                    $scope.person.identifications.push({});
                }, 0);
            };

            $scope.removeIdentification = function (identification) {
                $timeout(function () {
                    _.remove($scope.person.identifications, function (object) {
                        return object === identification;
                    });
                }, 0);
            };

            $scope.addAddress = function () {
                $timeout(function () {
                    var defaultAddressType = ObjectLookupService.getPrimaryLookup($scope.addressTypes);
                    var defaultCountry = ObjectLookupService.getPrimaryLookup($scope.countries);

                    //add empty address
                    $scope.person.addresses.push({
                        type: defaultAddressType ? defaultAddressType.key : null,
                        country: defaultCountry ? defaultCountry.key : null
                    });
                }, 0);
            };

            $scope.removeAddress = function (address) {
                $timeout(function () {
                    _.remove($scope.person.addresses, function (object) {
                        return object === address;
                    });
                }, 0);
            };

            $scope.showImage = function ($files, index) {
                var file = $files;
                var reader = new FileReader();
                reader.onload = function (e) {
                    $scope.previewImage[e.target.index] = e.target.result;
                    $scope.$apply();
                };
                reader.index = index;
                reader.readAsDataURL(file);
            };

            $scope.addEmptyPicture = function () {
                $scope.pictures.push({});
                $timeout(function () {
                    //add empty object
                }, 0);
            };

            $scope.removePicture = function (index) {
                $timeout(function () {
                    $scope.pictures.splice(index, 1);
                    $scope.userPictures.splice(index, 1);
                    $scope.previewImage.splice(index, 1);
                    if ($scope.pictures.length < 1) {
                        $scope.pictures.push({});
                    }
                }, 0);
            };

            $scope.save = function () {
                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                var clearedPersonInfo = clearNotFilledElements(_.cloneDeep($scope.person));
                var promiseSavePerson = PersonInfoService.savePersonInfoWithPictures(clearedPersonInfo, $scope.userPictures);
                promiseSavePerson.then(function (objectInfo) {
                    var objectTypeString = $translate.instant('common.objectTypes.' + ObjectService.ObjectTypes.PERSON);
                    var personWasCreatedMessage = $translate.instant('people.comp.editPerson.informCreated', {
                        personType: objectTypeString,
                        firstName: objectInfo.data.givenName,
                        lastName: objectInfo.data.familyName
                    });
                    MentionsService.sendEmailToMentionedUsers($scope.paramsSummernote.emailAddresses, $scope.paramsSummernote.usersMentioned, ObjectService.ObjectTypes.PERSON, "DETAILS", objectInfo.data.id, objectInfo.data.details);
                    MessageService.info(personWasCreatedMessage);
                    $timeout(function () {
                        ObjectService.showObject(ObjectService.ObjectTypes.PERSON, objectInfo.data.id);
                    }, 1500);
                    $scope.onModalClose();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                }, function (error) {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    if (error.data && error.data.message) {
                        $scope.error = error.data.message;
                    } else {
                        MessageService.error(error);
                    }
                });
            };

            $scope.addNewOrganization = function () {
                $timeout(function () {
                    $scope.searchOrganization(-1);
                }, 0);
            };

            $scope.removeOrganization = function (association) {
                $timeout(function () {
                    _.remove($scope.person.organizationAssociations, function (object) {
                        return object === association;
                    });
                }, 0);
            };

            $scope.searchOrganization = function (index) {
                var associationFound = _.find($scope.person.organizationAssociations, function (item) {
                    return !Util.isEmpty(item) && !Util.isEmpty(item.organization);
                });
                var association = index > -1 ? $scope.person.organizationAssociations[index] : {};
                var params = {
                    showSetPrimary: true,
                    isDefault: false,
                    types: $scope.organizationTypes,
                    isFirstOrganization: Util.isEmpty(associationFound) ? true : false,
                    assocTypeLabel: assocTypeLabel
                };
                //set this params for editing
                if (association.organization) {
                    angular.extend(params, {
                        organizationId: association.organization.organizationId,
                        organizationValue: association.organization.organizationValue,
                        type: association.personToOrganizationAssociationType,
                        isDefault: Util.isEmpty(association.defaultOrganization) ? true : false
                    });
                }

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                    controller: 'Common.AddOrganizationModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    if (data.organization) {
                        setOrganizationAssociation(association, data);
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                            data.organization = organization;
                            setOrganizationAssociation(association, data);
                        });
                    }
                });
            };

            function setOrganizationAssociation(association, data) {
                association.person = {
                    id: $scope.person.id
                };
                association.organization = data.organization;
                association.personToOrganizationAssociationType = data.inverseType;
                association.organizationToPersonAssociationType = data.type;

                if (data.isDefault) {
                    //find and change previously default organization
                    var defaultAssociation = _.find($scope.person.organizationAssociations, function (object) {
                        return object.defaultOrganization;
                    });
                    if (defaultAssociation) {
                        defaultAssociation.defaultOrganization = false;
                    }
                }
                association.defaultOrganization = data.isDefault;

                //if is new created, add it to the organization associations list
                if (!$scope.person.organizationAssociations) {
                    $scope.person.organizationAssociations = [];
                }

                if (!_.includes($scope.person.organizationAssociations, association)) {
                    $scope.person.organizationAssociations.push(association);
                }
            }

            $scope.selectPicture = function () {
                $timeout(function () {
                    if ($scope.userPicture) {
                        $scope.pictures.push($scope.userPicture);
                        $scope.userPicture = null;
                    }
                }, 0);
            };

            function clearNotFilledElements(person) {

                //remove opened property added for the datePickers
                if (person.identifications && person.identifications.length) {
                    person.identifications = _.map(person.identifications, function (obj) {
                        return _.omit(obj, 'opened');
                    });
                }

                //phones
                if (!person.defaultPhone.value) {
                    person.defaultPhone = null;
                } else {
                    person.contactMethods.push(person.defaultPhone);
                }
                //emails
                if (!person.defaultEmail.value) {
                    person.defaultEmail = null;
                } else {
                    person.contactMethods.push(person.defaultEmail);
                }
                //urls
                if (!person.defaultUrl.value) {
                    person.defaultUrl = null;
                } else {
                    person.contactMethods.push(person.defaultUrl);
                }
                //identifications
                if (person.defaultIdentification) {
                    person.identifications.push(person.defaultIdentification);
                }

                //remove empty organizations before save
                _.remove(person.organizationAssociations, function (association) {
                    if (!association.organization) {
                        return true;
                    } else {
                        return false;
                    }
                });

                //addresses
                if (person.defaultAddress && !person.defaultAddress.streetAddress) {
                    person.defaultAddress = null;
                } else {
                    person.addresses.push(person.defaultAddress);
                }
                //aliases
                if (person.defaultAlias) {
                    if (person.defaultAlias.aliasValue) {
                        person.defaultAlias = null;
                    } else {
                        person.personAliases.push(person.defaultAlias);
                    }
                }

                return person;
            }

            /**
             * capitalize first Letter in the string
             * @param input
             * @returns capitalized string
             */
            $scope.capitalizeFirstLetter = function (input) {
                return (!!input) ? input.charAt(0).toUpperCase() + input.substr(1).toLowerCase() : '';
            };

            $scope.cancelModal = function () {
                $scope.onModalDismiss();
            };


            var regEx = PhoneValidationService.getPhoneRegex().then(function (response) {
                var regExp = new RegExp(response.data);
                regEx = regExp;
            });

            function openDuplicatePersonPicker(result) {

                var params = {};

                params.people = result.data.response.docs;
                params.config = Util.goodMapValue($scope.config, "dialogPersonPicker");
                params.isRedirect = true;

                var modalInstance = $modal.open({
                    templateUrl: "modules/common/views/duplicate-person-picker-modal.client.view.html",
                    controller: "Common.DuplicatePersonPickerController",
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
                        $state.go('people.main', {
                            id: selected.object_id_s,
                        }, true);
                    }
                });
            }

            function resetEmail(email) {
                if (_.find($scope.person.contactMethods, {type: "email", value: email})) {
                    _.find($scope.person.contactMethods, {type: "email", value: email}).value = '';
                }
                if ($scope.person.defaultEmail.value === email) {
                    $scope.person.defaultEmail.value = '';
                }
            }

            $scope.checkExistingEmail = function (email) {
                PersonInfoService.queryByEmail(email).then(function (result) {
                    if (result.data.response.numFound > 0) {
                        openDuplicatePersonPicker(result);
                        resetEmail(email);
                    }
                });
            };

            $scope.validateInput = function (caType, caValue) {
                var inputType = caType;
                if (inputType === 'phone') {
                    var validateObject = PhoneValidationService.validateInput(caValue, regEx);
                    $scope.person.defaultPhone.value = validateObject.inputValue;
                    $scope.showPhoneError = validateObject.showPhoneError;
                } else if (inputType === 'email' && caValue) {
                    $scope.checkExistingEmail(caValue);
                }
            };

            $scope.checkLocationRules = function (location) {
                return !_.values(location).every(_.isEmpty)
            }
        }])
;
