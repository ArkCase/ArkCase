'use strict';

angular.module('common').controller('Common.NewOrganizationModalController', ['$scope', '$stateParams', '$translate'
    , 'Organization.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', '$modalInstance'
    , function ($scope, $stateParams, $translate, OrganizationInfoService, $state, ObjectLookupService, MessageService
        , $timeout, $modalInstance) {
        //used for showing/hiding buttons in communication accounts
        var contactMethodsCounts = {
            'url': 0,
            'phone': 0,
            'email': 0,
            'fax': 0
        };

        //new organization with predefined values
        $scope.organization = {
            className: 'com.armedia.acm.plugins.person.model.Organization',
            contactMethods: [],
            identifications: [],
            addresses: [],
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

        //contact methods subtypes types
        ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
            $scope.cmTypes = {};
            _.each(contactMethodTypes, function (cmType) {
                $scope.cmTypes[cmType.type] = cmType;
            });

            //used for generating the view for communication accounts
            $scope.communicationAccountsTypes = ['phone', 'fax', 'email', 'url'];
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

        ObjectLookupService.getOrganizationTypes().then(function (organizationTypes) {
            $scope.organizationTypes = organizationTypes;
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
            if (!organization.defaultPhone.value) {
                organization.defaultPhone = null;
            }
            if (!organization.defaultEmail.value) {
                organization.defaultEmail = null;
            }
            if (!organization.defaultUrl.value) {
                organization.defaultUrl = null;
            }
            //TODO do same for aliases, address... all defaults
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
