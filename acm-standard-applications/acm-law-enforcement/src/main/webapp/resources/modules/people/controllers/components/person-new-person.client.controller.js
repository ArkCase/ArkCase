'use strict';

angular.module('people').controller('People.NewPersonController', ['$scope', '$stateParams', '$translate'
    , 'Person.InfoService', '$state', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'Organization.InfoService'
    , function ($scope, $stateParams, $translate, PersonInfoService, $state, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, OrganizationInfoService) {
        //used for showing/hiding buttons in communication accounts
        var contactMethodsCounts = {
            'url': 0,
            'phone': 0,
            'email': 0
        };

        ConfigService.getModuleConfig("people").then(function (moduleConfig) {
            $scope.config = _.find(moduleConfig.components, {id: "newPerson"});
            return moduleConfig;
        });

        //new person with predefined values
        $scope.person = {
            className: 'com.armedia.acm.plugins.person.model.Person',
            contactMethods: [],
            identifications: [],
            addresses: [],
            organizations: [{}],
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

        $scope.save = function () {

            var promiseSavePerson = PersonInfoService.savePersonInfo(clearNotFilledElements(_.cloneDeep($scope.person)));
            promiseSavePerson.then(
                function (objectInfo) {
                    $scope.$emit("report-object-updated", objectInfo);
                    MessageService.info($translate.instant("people.comp.newPerson.informSaved"));
                    $state.go('people');
                    return objectInfo;
                }
                , function (error) {
                    $scope.$emit("report-object-update-failed", error);
                    return error;
                }
            );
        };

        $scope.addNewOrganization = function () {
            $timeout(function () {
                searchOrganization(null);
            }, 0);
        };

        $scope.removeOrganization = function (organization) {
            $timeout(function () {
                _.remove($scope.person.organizations, function (object) {
                    return object === organization;
                });
            }, 0);
        };

        $scope.searchOrganization = function (organization) {
            var params = {};
            params.header = $translate.instant("people.comp.organizations.dialogOrganizationPicker.header");
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
                    OrganizationInfoService.getOrganizationInfo(selected.object_id_s).then(function (selectedOrganization) {
                        // override values of existing organization which is displayed
                        if (organization) {
                            _.merge(organization, selectedOrganization);
                        } else {
                            $person.organizations.push(selectedOrganization);
                        }
                    });
                }
            });

        };

        function clearNotFilledElements(person) {
            if (!person.defaultPhone.value) {
                person.defaultPhone = null;
            } else {
                person.contactMethods.push(person.defaultPhone);
            }
            if (!person.defaultEmail.value) {
                person.defaultEmail = null;
            } else {
                person.contactMethods.push(person.defaultEmail);
            }
            if (!person.defaultUrl.value) {
                person.defaultUrl = null;
            } else {
                person.contactMethods.push(person.defaultUrl);
            }
            if (!person.defaultIdentification.identificationID) {
                person.defaultIdentification = null;
            } else {
                person.identifications.push(person.defaultIdentification);
            }

            //remove empty organizations before save
            _.remove(person.organizations, function (person) {
                if (!person.organizationId) {
                    return true;
                }
                return false;
            });
            //TODO do same for aliases, address... all defaults
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
