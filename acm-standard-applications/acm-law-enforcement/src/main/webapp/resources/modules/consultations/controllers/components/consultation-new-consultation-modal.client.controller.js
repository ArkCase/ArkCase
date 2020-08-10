'use strict';

angular.module('consultations').controller(
    'Consultations.NewConsultationController',
    [ '$scope', '$stateParams', '$q', '$translate', '$modalInstance', 'Consultation.InfoService', 'Object.LookupService', 'MessageService', '$timeout', 'UtilService', '$modal', 'ConfigService', 'ObjectService', 'modalParams', 'Person.InfoService', 'Object.ModelService', 'Object.ParticipantService',
        'Profile.UserInfoService', 'Mentions.Service', 'Organization.InfoService', '$location', '$anchorScroll',
        function($scope, $stateParams, $q, $translate, $modalInstance, ConsultationInfoService, ObjectLookupService, MessageService, $timeout, Util, $modal, ConfigService, ObjectService, modalParams, PersonInfoService, ObjectModelService, ObjectParticipantService, UserInfoService, MentionsService, OrganizationInfoService, $location, $anchorScroll) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.selectedFiles = [];
            $scope.userSearchConfig = null;
            $scope.isEdit = $scope.modalParams.isEdit;
            $scope.isPickExistingPerson = false;
            $scope.uploadFiles = [];

            $scope.config = null;
            
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

            $q.all([moduleConfig, prefixNewConsultation, getCountries, getAddressTypes, canadaProvinces, japanStates, states, personTypesLookup, organizationTypeLookup, commonModuleConfig]).then(function (data) {

                var moduleConfig = data[0];
                var prefixes = data[1];
                var countries = data[2];
                var addressTypes = data[3];
                var canadaProvinces = data[4];
                var japanStates = data[5];
                var usaStates = data[6];
                var personTypes = data[7];
                var organizationTypes = data[8];
                $scope.commonModuleConfig = data[9];

                $scope.config = moduleConfig;
                $scope.prefixes = prefixes;
                $scope.countries = countries;
                $scope.addressTypes = addressTypes;
                $scope.usStates = usaStates;
                $scope.canadaProvinces = canadaProvinces;
                $scope.japanStates = japanStates;
                $scope.personTypes = personTypes;

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
                            addresses: []
                        }
                    },
                    personAssociations: [],
                    participants: [],
                    componentAgency: ''
                };
                $scope.config.data.organizationAssociations = [];
                $scope.config.data.originator.person.title = $scope.prefixes[0].key;
                $scope.config.data.receivedDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                $scope.config.data.dueDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");

                $scope.consultationPeopleConfig = _.find(moduleConfig.components, {
                    id: "people"
                });
                
            });

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

            

            $scope.receivedDateChanged = function () {
                var todayDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                if (Util.isEmpty($scope.config.data.receivedDate) || moment($scope.config.data.receivedDate).isAfter(todayDate)) {
                    $scope.config.data.receivedDate = todayDate;
                }
            };

            $scope.dueDateChanged = function() {
                var todayDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                if(Util.isEmpty($scope.config.data.dueDate) || moment($scope.config.data.dueDate).isBefore($scope.config.data.receivedDate)) {
                    $scope.config.data.dueDate = todayDate;
                } else {
                    $scope.config.data.dueDate = $scope.config.data.dueDate;
                }
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

                    if (person.addresses[0] && !Util.isEmpty(person.addresses[0].country) && !Util.isEmpty(person.addresses[0].state)) {
                        $scope.changeStates(person.addresses[0].country);
                    }

                    $scope.isExistingPerson = typeof $scope.config.data.originator.person.id !== 'undefined';

                    if ($scope.config.data.originator.person.defaultEmail) {
                        $scope.confirmationEmail = angular.copy($scope.config.data.originator.person.defaultEmail.value);
                    } else {
                        $scope.confirmationEmail = '';
                    }
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
                    typeEnabled: false
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
                    isFirstOrganization: Util.isEmpty(associationFound) ? true : false
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
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                            data.organization = organization;
                            $scope.organizationValue = data.organization.organizationValue;
                            $scope.config.data.originator.person.organizations.push(data.organization);
                            setOrganizationAssociation(association, data);
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
                $scope.confirmEmailEmpty = false;
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
                if (consultationForm.confirmationEmail.$viewValue === undefined || consultationForm.confirmationEmail.$viewValue === '') {
                    $scope.confirmEmailEmpty = true;
                } else if (consultationForm.confirmationEmail.$viewValue !== consultationForm.email.$viewValue) {
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
                    saveConsultation(formdata);
                } else {
                    saveConsultationInfo($scope.config.data);
                }
                
            };

            function setInitiatorPersonAssociation(association, person) {
                association.person = person;
                association.personType = 'Initiator';
                association.personDescription = '';
                association.parentTitle = $scope.config.data.consultationNumber;
                $scope.config.data.originator = association;
                $scope.config.data.personAssociations.push(association)
                
            }

            var saveConsultationInfo = function (consultation) {
                ConsultationInfoService.saveConsultationInfo(consultation).then(function (response) {
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
