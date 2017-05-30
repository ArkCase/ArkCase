'use strict';

angular.module('people').controller('People.NewPersonController', ['$scope', '$stateParams', '$translate'
    , 'Person.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Organization.InfoService', 'ObjectService'
    , function ($scope, $stateParams, $translate, PersonInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, OrganizationInfoService, ObjectService) {
        
        $scope.loading = false;
        
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
        $scope.userPicture = null;

        //new person with predefined values
        $scope.person = {
            className: 'com.armedia.acm.plugins.person.model.Person',
            contactMethods: [],
            identifications: [],
            addresses: [],
            associationsFromObjects: [{
                parentType: 'ORGANIZATION'
            }],
            defaultEmail: {
                type: 'email'
            },
            defaultPhone: {
                type: 'phone'
            },
            defaultUrl: {
                type: 'url'
            }
        };

        //contact methods subtypes types
        ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
            $scope.cmTypes = {};
            _.each(contactMethodTypes, function (cmType) {
                $scope.cmTypes[cmType.type] = cmType;
            });

            //used for generating the view for communication accounts
            $scope.communicationAccountsTypes = ['phone', 'email', 'url'];
        });

        ObjectLookupService.getIdentificationTypes().then(function (identificationTypes) {
            $scope.identificationTypes = identificationTypes;
        });

        ObjectLookupService.getCountries().then(function (countries) {
            $scope.countries = countries;
        });

        ObjectLookupService.getAddressTypes().then(function (addressTypes) {
            $scope.addressTypes = addressTypes;
        });

        ObjectLookupService.getPersonOrganizationRelationTypes().then(function (personOrganizationRelationTypes) {
            $scope.personOrganizationRelationTypes = personOrganizationRelationTypes;
        });

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
                //add empty address
                $scope.person.addresses.push({});
            }, 0);
        };

        $scope.removeAddress = function (address) {
            $timeout(function () {
                _.remove($scope.person.addresses, function (object) {
                    return object === address;
                });
            }, 0);
        };

        $scope.addEmptyPerson = function () {
            $scope.pictures.push({});
            $timeout(function () {
                //add empty object
            }, 0);
        };

        $scope.removePerson = function (toBeRemoved) {
            $timeout(function () {
                _.remove($scope.pictures, function (object) {
                    return object === toBeRemoved;
                });
                if ($scope.pictures.length < 1) {
                    $scope.pictures.push({});
                }
            }, 0);
        };


        $scope.save = function () {
            $scope.loading = true;
            var clearedPersonInfo = clearNotFilledElements(_.cloneDeep($scope.person));
            var promiseSavePerson = PersonInfoService.savePersonInfoWithPictures(clearedPersonInfo, $scope.pictures);
            promiseSavePerson.then(
                function (objectInfo) {
                    ObjectService.showObject(ObjectService.ObjectTypes.PERSON, objectInfo.data.id);
                    $scope.loading = false;
                }
                , function (error) {
                    $scope.loading = false;
                    if (error.data && error.data.message) {
                        $scope.error = error.data.message;
                    } else {
                        MessageService.error(error);
                    }
                }
            );
        };

        $scope.addNewOrganization = function () {
            $timeout(function () {
                $scope.searchOrganization(-1);
            }, 0);
        };

        $scope.removeOrganization = function (organization) {
            $timeout(function () {
                _.remove($scope.person.associationsFromObjects, function (object) {
                    return object === organization;
                });
            }, 0);
        };


        $scope.searchOrganization = function (index) {
            var params = {};
            params.header = $translate.instant("common.dialogOrganizationPicker.header");
            params.filter = '"Object Type": ORGANIZATION';
            params.config = Util.goodMapValue($scope.config, "dialogOrganizationPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/common/views/object-picker-modal.client.view.html",
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                    $scope.modalInstance = $modalInstance;
                    $scope.header = params.header;
                    $scope.filter = params.filter;
                    $scope.config = params.config;
                }],
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
                    // override values of existing organization which is displayed
                    $timeout(function () {
                        var personAssocation = {
                            person: $scope.person,
                            parentId: selected.object_id_s,
                            parentType: selected.object_type_s,
                            parentTitle: selected.value_parseable
                        };
                        if (index > -1) {
                            $scope.person.associationsFromObjects[index] = personAssocation;
                        } else {
                            $scope.person.associationsFromObjects.push(personAssocation);
                        }
                    }, 0);
                }
            });
        };

        $scope.selectPicture = function () {
            $timeout(function () {
                if ($scope.userPicture) {
                    $scope.pictures.push($scope.userPicture);
                    $scope.userPicture = null;
                }
            }, 0);
        };

        function clearNotFilledElements(person) {
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
            _.remove(person.associationsFromObjects, function (organization) {
                if (!organization.parentId) {
                    return true;
                }
                return false;
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
                }
                else {
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
        }
    }
]);
