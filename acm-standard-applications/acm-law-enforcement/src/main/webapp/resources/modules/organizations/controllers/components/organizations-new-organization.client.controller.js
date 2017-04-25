'use strict';

angular.module('organizations').controller('Organizations.NewOrganizationController', ['$scope', '$stateParams', '$translate'
    , 'Organization.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout'
    , function ($scope, $stateParams, $translate, OrganizationInfoService, $state, ObjectLookupService, MessageService, $timeout) {
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

        $scope.save = function () {

            var promiseSaveOrganization = OrganizationInfoService.saveOrganizationInfo(clearNotFilledElements(_.cloneDeep($scope.organization)));
            promiseSaveOrganization.then(
                function (objectInfo) {
                    $scope.$emit("report-object-updated", objectInfo);
                    MessageService.info($translate.instant("organization.comp.newOrganization.informSaved"));
                    $state.go('organizations');
                    return objectInfo;
                }
                , function (error) {
                    $scope.$emit("report-object-update-failed", error);
                    return error;
                }
            );
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
