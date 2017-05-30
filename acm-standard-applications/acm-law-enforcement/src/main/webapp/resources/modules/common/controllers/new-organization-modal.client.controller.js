'use strict';

angular.module('common').controller('Common.NewOrganizationModalController', ['$scope', '$stateParams', '$translate'
    , 'Organization.InfoService', '$state', 'Object.LookupService', 'UtilService', '$modal', 'ConfigService', 'MessageService', '$timeout', '$modalInstance', 'Person.InfoService'
    , function ($scope, $stateParams, $translate, OrganizationInfoService, $state, ObjectLookupService, Util, $modal, ConfigService, MessageService
        , $timeout, $modalInstance, PersonInfoService) {

        //used for showing/hiding buttons in communication accounts
        var contactMethodsCounts = {
            'url': 0,
            'phone': 0,
            'email': 0,
            'fax': 0
        };
        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            $scope.config = moduleConfig;
            return moduleConfig;
        });

        //new organization with predefined values
        $scope.organization = {
            className: 'com.armedia.acm.plugins.person.model.Organization',
            contactMethods: [],
            identifications: [],
            addresses: [],
            associationsFromObjects: [{
                parentType: 'PERSON'
            }],
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

        $scope.searchOrganization = function () {
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
                    OrganizationInfoService.getOrganizationInfo(selected.object_id_s).then(function (response) {
                        //FIXME ugly hack - saving organization fails because those properties are not removed when angular converts to JSON
                        delete response.$promise;
                        delete response.$resolved;
                        $scope.organization.parentOrganization = response;
                    });
                }
            });
        };


        $scope.addNewPerson = function () {
            $timeout(function () {
                $scope.searchPerson(-1);
            }, 0);
        };

        $scope.removePerson = function (person) {
            $timeout(function () {
                _.remove($scope.organization.associationsFromObjects, function (object) {
                    return object === person;
                });
            }, 0);
        };

        $scope.searchPerson = function (index) {
            var params = {};
            params.header = $translate.instant("common.dialogPersonPicker.header");
            params.filter = '"Object Type": PERSON';
            params.config = Util.goodMapValue($scope.config, "dialogPersonPicker");

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
                    $timeout(function () {
                        var organizationAssocation = {
                            organization: $scope.organization,
                            parentId: selected.object_id_s,
                            parentType: selected.object_type_s,
                            parentTitle: selected.full_name_lcs
                        };
                        if (index > -1) {
                            $scope.organization.associationsFromObjects[index] = organizationAssocation;

                        } else {
                            $scope.organization.associationsFromObjects.push(organizationAssocation);
                        }
                    }, 0);
                }
            });
        };

        //contact methods subtypes types
        ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
            $scope.cmTypes = {};
            _.each(contactMethodTypes, function (cmType) {
                $scope.cmTypes[cmType.type] = cmType;
            });

            //used for generating the view for communication accounts
            $scope.communicationAccountsTypes = ['phone', 'fax', 'email', 'url'];
        });

        ObjectLookupService.getOrganizationIdTypes().then(function (identificationTypes) {
            $scope.identificationTypes = identificationTypes;
        });

        ObjectLookupService.getCountries().then(function (countries) {
            $scope.countries = countries;
        });

        ObjectLookupService.getAddressTypes().then(function (addressTypes) {
            $scope.addressTypes = addressTypes;
        });

        ObjectLookupService.getOrganizationTypes().then(function (organizationTypes) {
            $scope.organizationTypes = organizationTypes;
        });

        ObjectLookupService.getPersonOrganizationRelationTypes().then(function (personOrganizationRelationTypes) {
            $scope.personOrganizationRelationTypes = personOrganizationRelationTypes;
        });

        $scope.addContactMethod = function (contactType) {
            $timeout(function () {
                contactMethodsCounts[contactType]++;
                $scope.organization.contactMethods.push({
                    type: contactType
                });
            }, 0);
        };

        $scope.removeContactMethod = function (contact) {
            $timeout(function () {
                contactMethodsCounts[contact.type]--;
                _.remove($scope.organization.contactMethods, function (object) {
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
                $scope.organization.identifications.push({});
            }, 0);
        };

        $scope.removeIdentification = function (identification) {
            $timeout(function () {
                _.remove($scope.organization.identifications, function (object) {
                    return object === identification;
                });
            }, 0);
        };

        $scope.addAddress = function () {
            $timeout(function () {
                //add empty address
                $scope.organization.addresses.push({});
            }, 0);
        };

        $scope.removeAddress = function (address) {
            $timeout(function () {
                _.remove($scope.organization.addresses, function (object) {
                    return object === address;
                });
            }, 0);
        };

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.save = function () {
            $modalInstance.close({
                organization: clearNotFilledElements(_.cloneDeep($scope.organization))
            });
        };

        function clearNotFilledElements(organization) {

            //remove opened property added for the datePickers
            if (organization.identifications && organization.identifications.length) {
                organization.identifications = _.map(organization.identifications, function (obj) {
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
                if (!organization.defaultIdentification.identificationID) {
                    organization.defaultIdentification = null;
                } else {
                    organization.identifications.push(organization.defaultIdentification);
                }
            }

            //addresses
            if (organization.defaultAddress && !organization.defaultAddress.streetAddress) {
                organization.defaultAddress = null;
            } else {
                organization.addresses.push(organization.defaultAddress);
            }

            //remove empty organizations before save
            _.remove(organization.associationsFromObjects, function (person) {
                if (!person.parentId) {
                    return true;
                }
                return false;
            });

            return organization;
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
