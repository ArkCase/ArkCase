'use strict';

angular.module('consultations').controller(
    'Consultations.NewConsultationController',
    [ '$scope', '$stateParams', '$q', '$translate', '$modalInstance', 'Consultation.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
        'Profile.UserInfoService', 'Mentions.Service', 'Organization.InfoService', '$location', '$anchorScroll', 'Admin.ObjectTitleConfigurationService',
        function($scope, $stateParams, $q, $translate, $modalInstance, ConsultationInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService, MentionsService, OrganizationInfoService, $location, $anchorScroll, AdminObjectTitleConfigurationService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.selectedFiles = [];
            $scope.userSearchConfig = null;
            $scope.isEdit = $scope.modalParams.isEdit;
            $scope.isPickExistingPerson = false;
            $scope.uploadFiles = [];
            $scope.primaryAddressIndex = 0;
            $scope.config = null;
            $scope.enableTitle = false
            
            var moduleConfig = ConfigService.getModuleConfig("consultations");
            var prefixNewConsultation = ObjectLookupService.getPersonTitles();
            var personTypesLookup = ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CONSULTATION, true);
            var organizationTypeLookup = ObjectLookupService.getPersonOrganizationRelationTypes();
            var getCountries = ObjectLookupService.getCountries();
            var getAddressTypes = ObjectLookupService.getAddressTypes();
            var canadaProvinces = ObjectLookupService.getLookupByLookupName('canadaProvinces');
            var japanStates = ObjectLookupService.getLookupByLookupName('japanStates');
            var states = ObjectLookupService.getStates();
            var commonModuleConfig = ConfigService.getModuleConfig("common");
            var positionLookup = ObjectLookupService.getPersonOrganizationRelationTypes();
            var promiseConfigTitle = AdminObjectTitleConfigurationService.getObjectTitleConfiguration();

            $q.all([moduleConfig, prefixNewConsultation, getCountries, getAddressTypes, canadaProvinces, japanStates, states, personTypesLookup, organizationTypeLookup, commonModuleConfig, positionLookup, promiseConfigTitle]).then(function (data) {

                var moduleConfig = data[0];
                var prefixes = data[1];
                var countries = data[2];
                var addressTypes = data[3];
                var canadaProvinces = data[4];
                var japanStates = data[5];
                var usaStates = data[6];
                var personTypes = data[7];
                var organizationTypes = data[8];
                var configTitle = data[11]
                $scope.commonModuleConfig = data[9];

                $scope.config = moduleConfig;
                $scope.prefixes = prefixes;
                $scope.countries = countries;
                $scope.addressTypes = addressTypes;
                $scope.usStates = usaStates;
                $scope.canadaProvinces = canadaProvinces;
                $scope.japanStates = japanStates;
                $scope.personTypes = personTypes;
                $scope.positions = data[10];

                $scope.config = moduleConfig;
                $scope.organizationTypes = organizationTypes;

                $scope.config.data = {
                    className: $scope.config.className,
                    consultationType: '',
                    title: '',
                    details: '',
                    originator: {
                        person: {
                            title: '',
                            organizationAssociations: [],
                            contactMethods: [],
                            addresses: [{}],
                        }
                    },
                    personAssociations: [],
                    participants: [],
                    externalRequestingAgency: ''
                };

                if (!Util.isEmpty(configTitle)) {
                    $scope.enableTitle = configTitle.data.CONSULTATION.enableTitleField;
                }

                var defaultAddressType = ObjectLookupService.getPrimaryLookup($scope.addressTypes);
                var defaultCountry = ObjectLookupService.getPrimaryLookup($scope.countries);

                $scope.config.data.originator.person.addresses[0].country = defaultCountry ? defaultCountry.key : countries[0].key;
                $scope.config.data.originator.person.addresses[0].type = defaultAddressType ? defaultAddressType.key : addressTypes[0].key;

                $scope.config.data.organizationAssociations = [];
                $scope.minDueDate = moment.utc($scope.config.data.receivedDate).local();
                $scope.maxReceivedDate = moment(new Date());

                $scope.consultationPeopleConfig = _.find(moduleConfig.components, {
                    id: "people"
                });

            });

            var assocTypeLabel = $translate.instant("consultations.comp.people.type.label");
            var assocOrgTypeLabel = $translate.instant("consultations.newConsultation.position.label");

            var newPersonAssociation = function() {
                return {
                    id: null,
                    personType: "",
                    parentId: $scope.config.data.id,
                    parentType: ObjectService.ObjectTypes.CONSULTATION,
                    parentTitle: $scope.config.data.consultationNumber,
                    personDescription: "",
                    notes: "",
                    person: null,
                    className: $scope.consultationPeopleConfig.personAssociationClassName
                };
            };

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

            

            $scope.receivedDateChanged = function (data) {
                if ($scope.config && $scope.config.data &&
                    moment($scope.config.data.receivedDate).isAfter($scope.config.data.dueDate)) {
                    $scope.config.data.dueDate = data.dateInPicker;
                    $scope.dateChangedManually = true;
                }
                $scope.minDueDate = moment.utc($scope.config.data.receivedDate).local();
            };

            $scope.pickExistingUserChange = function () {

                if ($scope.isPickExistingPerson) {
                    $scope.newPerson = angular.copy($scope.config.data.originator.person);
                    $scope.setPerson($scope.existingPerson);

                } else {
                    $scope.existingPerson = angular.copy($scope.config.data.originator.person);
                    $scope.setPerson($scope.newPerson);
                }
            };

            $scope.setPerson = function (person) {
                if (person) {
                    $scope.config.data.originator.person = angular.copy(person);

                    if (!$scope.config.data.originator.person.defaultPhone) {
                        var phone = _.find($scope.config.data.originator.person.contactMethods, {
                            type: 'phone'
                        });
                        $scope.config.data.originator.person.defaultPhone = phone;
                    }

                    if (!$scope.config.data.originator.person.defaultEmail) {
                        var email = _.find($scope.config.data.originator.person.contactMethods, {
                            type: 'email'
                        });
                        $scope.config.data.originator.person.defaultEmail = email;
                    }

                    if (!Util.isArrayEmpty(person.addresses) && person.defaultAddress) {
                        $scope.primaryAddressIndex = person.addresses.indexOf(person.defaultAddress);
                    } else {
                        $scope.primaryAddressIndex = 0;
                    }

                    if (person.addresses[$scope.primaryAddressIndex] && !Util.isEmpty(person.addresses[$scope.primaryAddressIndex].country) && !Util.isEmpty(person.addresses[$scope.primaryAddressIndex].state)) {
                        $scope.changeStates(person.addresses[$scope.primaryAddressIndex].country);
                    }

                    $scope.isExistingPerson = typeof $scope.config.data.originator.person.id !== 'undefined';

                    if ($scope.config.data.originator.person.defaultEmail) {
                        $scope.confirmationEmail = angular.copy($scope.config.data.originator.person.defaultEmail.value);
                    } else {
                        $scope.confirmationEmail = '';
                    }

                    if(person.defaultOrganization != null) {
                        $scope.organizationValue = person.defaultOrganization.organization.organizationValue;
                        $scope.personPosition = person.defaultOrganization.organization.personAssociations[0].personToOrganizationAssociationType;
                    } else {
                        if(person.organizationAssociations[0] != null) {
                            $scope.organizationValue = person.organizationAssociations[0].organization.organizationValue;
                            $scope.personPosition = person.organizationAssociations[0].organization.personAssociations[0].personToOrganizationAssociationType;
                        }
                    }
                }
            };

            $scope.changeStates = function (country) {
                switch (country) {
                    case 'US':
                        $scope.states = $scope.usStates;
                        break;
                    case 'CA':
                        $scope.states = $scope.canadaProvinces;
                        break;
                    case 'JP':
                        $scope.states = $scope.japanStates;
                        break;
                }
            };
            
            // -------------------  people --------------------------------------------------------------------
            $scope.searchPerson = function () {
                var params = {
                    showSetPrimary: false,
                    addNewEnabled: false,
                    isDefault: false,
                    types: $scope.personTypes,
                    type: $scope.personTypes[0].key,
                    typeEnabled: false,
                    assocTypeLabel: assocTypeLabel
                };

                var modalInstance = $modal.open({
                    scope: $scope,
                    animation: true,
                    templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                    controller: 'Common.AddPersonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {
                        $scope.setPerson(person);
                        if(person.defaultOrganization != null) {
                            $scope.organizationValue = person.defaultOrganization.organization.organizationValue;
                            $scope.personPosition = person.defaultOrganization.organization.personAssociations[0].personToOrganizationAssociationType;
                        } else {
                            if(person.organizationAssociations[0] != null) {
                              $scope.organizationValue = person.organizationAssociations[0].organization.organizationValue;
                              $scope.personPosition = person.organizationAssociations[0].organization.personAssociations[0].personToOrganizationAssociationType;
                            }
                        }
                        $scope.existingPerson = angular.copy($scope.config.data.originator.person);
                    });
                });

            };

            function setOrganizationAssociation(association, data) {
                association.person = $scope.config.data.originator.person;
                association.organization = data.organization;
                association.personToOrganizationAssociationType = data.type;
                association.organizationToPersonAssociationType = data.inverseType;

                var organizationAssociation = {};
                organizationAssociation["associationType"] = data.type;
                organizationAssociation["organization"] = data.organization;

                if (data.isDefault) {
                    //find and change previously default organization
                    var defaultAssociation = _.find($scope.config.data.originator.person.organizationAssociations, function (object) {
                        return object.defaultOrganization;
                    });
                    if (defaultAssociation) {
                        defaultAssociation.defaultOrganization = false;
                    }
                }
                association.defaultOrganization = data.isDefault;

                //if is new created, add it to the organization associations list
                if (!$scope.config.data.originator.person.organizationAssociations) {
                    $scope.config.data.originator.person.organizationAssociations = [];
                }

                if (!_.includes($scope.config.data.originator.person.organizationAssociations, association)) {
                    $scope.config.data.originator.person.organizationAssociations.push(association);
                    $scope.config.data.organizationAssociations.push(organizationAssociation);
                }
            }

            $scope.searchOrganization = function () {
                var associationFound = _.find($scope.config.data.originator.person.organizationAssociations, function (item) {
                    return !Util.isEmpty(item) && !Util.isEmpty(item.organization);
                });
                var association = !!$scope.config.data.originator.person.organizationAssociations[0] ? $scope.config.data.originator.person.organizationAssociations[0] : {};
                var params = {
                    showSetPrimary: true,
                    isDefault: false,
                    addNewEnabled: true,
                    types: $scope.organizationTypes,
                    isFirstOrganization: Util.isEmpty(associationFound) ? true : false,
                    assocTypeLabel: assocOrgTypeLabel
                };

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
                    $scope.config.data.originator.person.organizations = [];
                    if (data.organization) {
                        $scope.config.data.originator.person.organizations.push(data.organization);
                        setOrganizationAssociation(association, data);
                        $scope.organizationValue = data.organization.organizationValue;
                        $scope.personPosition = data.type;
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                            data.organization = organization;
                            $scope.organizationValue = data.organization.organizationValue;
                            $scope.config.data.originator.person.organizations.push(data.organization);
                            setOrganizationAssociation(association, data);
                            $scope.personPosition = data.type;
                        });
                    }
                });
            };

            //-----------------------------------------------------------------------------------------------

            $scope.validateForm = function (consultationForm) {

                $scope.formInvalid = false;
                $scope.phoneEmpty = false;
                $scope.phoneInvalid = false;
                $scope.emailEmpty = false;
                $scope.emailInvalid = false;
                $scope.confirmEmailInvalid = false;
                $scope.zipCodeEmpty = false;
                $scope.zipCodeInvalid = false;
                $scope.subjectEmpty = false;

                

                if (consultationForm.phone.$viewValue === undefined || consultationForm.phone.$viewValue === '') {
                    $scope.phoneEmpty = true;
                } else if (consultationForm.phone.$invalid) {
                    $scope.phoneInvalid = true;
                }
                if (consultationForm.email.$viewValue === undefined || consultationForm.email.$viewValue === '') {
                    $scope.emailEmpty = true;
                } else if (consultationForm.email.$invalid) {
                    $scope.emailInvalid = true;
                }
                if (consultationForm.confirmationEmail.$viewValue !== consultationForm.email.$viewValue) {
                    $scope.confirmEmailInvalid = true;
                    $scope.formInvalid = true;
                }
                if (consultationForm.zip.$viewValue === undefined || consultationForm.zip.$viewValue === '') {
                    $scope.zipCodeEmpty = true;
                } else if (consultationForm.zip.$invalid) {
                    $scope.zipCodeInvalid = true;
                }
                if (consultationForm.subject.$invalid) {
                    $scope.subjectEmpty = true;
                }
                if (consultationForm.$valid && !$scope.formInvalid) {
                    $scope.saveNewConsultation();
                } else {
                    $scope.formInvalid = true;
                    $location.hash('topSection1');
                    $anchorScroll();
                }
                
            };

            $scope.saveNewConsultation = function () {
                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                var formdata = new FormData();
                var basicData = {};

                if (Util.isEmpty($scope.config.data.originator.person.defaultPhone) || !$scope.config.data.originator.person.defaultPhone.value) {
                    $scope.config.data.originator.person.defaultPhone = null;
                } else if (!$scope.config.data.originator.person.defaultPhone.type) {
                    $scope.config.data.originator.person.defaultPhone.type = "phone";
                }

                if (Util.isEmpty($scope.config.data.originator.person.defaultEmail) || !$scope.config.data.originator.person.defaultEmail.value) {
                    $scope.config.data.originator.person.defaultEmail = null;
                } else if (!$scope.config.data.originator.person.defaultEmail.type) {
                    $scope.config.data.originator.person.defaultEmail.type = "email";
                }

                if ($scope.config.data.originator.person.defaultPhone && !$scope.config.data.originator.person.defaultPhone.id) {
                    $scope.config.data.originator.person.contactMethods.push($scope.config.data.originator.person.defaultPhone);
                }

                if ($scope.config.data.originator.person.defaultEmail && !$scope.config.data.originator.person.defaultEmail.id) {
                    $scope.config.data.originator.person.contactMethods.push($scope.config.data.originator.person.defaultEmail);
                }
                var association = newPersonAssociation();
                setInitiatorPersonAssociation(association, $scope.config.data.originator.person);

                for (var property in $scope.config.data) {
                    if ($scope.config.data.hasOwnProperty(property)) {
                        basicData[property] = $scope.config.data[property];
                    }
                }
                var data = new Blob([angular.toJson(JSOG.encode(Util.omitNg(basicData)))], {
                    type: 'application/json'
                });
                formdata.append('consultation', data);

                if($scope.uploadFiles.length > 0) {
                    _.forEach($scope.uploadFiles, function (attachment) {
                        formdata.append('file', attachment);
                    });
                }
                saveConsultation(formdata);
            };

            function setInitiatorPersonAssociation(association, person) {
                association.person = person;
                association.personType = 'Initiator';
                association.personDescription = '';
                association.parentTitle = $scope.config.data.consultationNumber;
                $scope.config.data.originator = association;
                $scope.config.data.personAssociations.push(association)
                
            }

            var saveConsultation = function (consultation) {
                ConsultationInfoService.saveConsultationWithFiles(consultation).then(function (response) {
                    $scope.onModalClose();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    ObjectService.showObject(ObjectService.ObjectTypes.CONSULTATION, response.id);
                }, function (error) {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.$emit("report-object-update-failed", error);
                    if (error.data && error.data.message) {
                        if (error.data.field == "duplicateName") {
                            MessageService.error($translate.instant("consultations.newConsultations.duplicateFilesName.error"));
                        } else {
                            MessageService.error(error.data.message);
                        }
                    } else {
                        MessageService.error(error);
                    }
                });
            };
            

            var fileArrayContainsFile = function (fileArray, file) {
                var fileFound = false;
                for (var i = 0; i < fileArray.length; i++) {
                    if (fileArray[i].name === file.name) {
                        fileFound = true;
                        break;
                    }
                }
                return fileFound;
            };

            $scope.addFile = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFiles, file[i]) == false) {
                            $scope.uploadFiles.push(file[i]);
                        }
                    }
                }
            };

            $scope.removeFile = function (index) {
                $scope.uploadFiles.splice(index, 1);
            };

            $scope.cancelModal = function () {
                $modalInstance.dismiss();
            };

            $scope.checkExistingEmail = function () {
                if ($scope.config.data.originator.person.defaultEmail.value === $scope.confirmationEmail) {
                    PersonInfoService.queryByEmail($scope.config.data.originator.person.defaultEmail.value).then(function (result) {
                        if (result.data.response.numFound > 0) {
                            openDuplicatePersonPicker(result);
                        }
                    });
                }
            };

            $scope.checkLocationRules = function (address) {
                return !_.values(address).every(_.isEmpty)
            };

            function openDuplicatePersonPicker(result) {
                $scope.config.data.originator.person.defaultEmail.value = '';
                $scope.confirmationEmail = '';

                var params = {};

                params.people = result.data.response.docs;
                params.config = Util.goodMapValue($scope.commonModuleConfig, "dialogPersonPicker");
                params.isRedirect = false;

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
                        PersonInfoService.getPersonInfo(selected.object_id_s).then(function (person) {
                            $scope.setPerson(person);
                            $scope.existingPerson = angular.copy($scope.config.data.originator.person);
                            $scope.newPerson = angular.copy($scope.blankPerson);
                        });
                    }
                });
            }

        } ]);
