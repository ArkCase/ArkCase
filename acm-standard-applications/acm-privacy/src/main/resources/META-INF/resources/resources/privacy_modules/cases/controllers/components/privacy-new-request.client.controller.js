'use strict';

angular.module('cases').controller(
    'Cases.NewRequestController',
    ['$scope', '$sce', '$q', '$modal', '$translate', 'ConfigService', 'SAR.Data', 'Request.InfoService', 'ObjectService', 'modalParams',
        'Object.LookupService', 'Util.DateService', 'MessageService', 'UtilService', 'Requests.RequestsService', 'Dialog.BootboxService',
        'Organization.InfoService', '$location', '$anchorScroll', 'Admin.ObjectTitleConfigurationService', 'Person.InfoService',
        'Admin.PortalConfigurationService',
        function ($scope, $sce, $q, $modal, $translate, ConfigService, Data, RequestInfoService, ObjectService, modalParams,
                  ObjectLookupService, UtilDateService, MessageService, Util, RequestsService, DialogService, OrganizationInfoService,
                  $location, $anchorScroll, AdminObjectTitleConfigurationService, PersonInfoService, AdminPortalConfigurationService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.formInvalid = false;
            $scope.enableTitle = false;
            $scope.isPickExistingOriginator = false;
            $scope.isPickExistingSubject = false;
            $scope.subjectSameAsRequester = false;
            $scope.maxDate = moment.utc(new Date());

            $scope.receivedDate = new Date();

            var subjectProofOfIdentityDocumentType = "Subject Proof of Identity";
            var originatorProofOfIdentityDocumentType = "Requester Proof of Identity";

            $scope.uploadFilesDescription = {};
            $scope.uploadFilesDescription[subjectProofOfIdentityDocumentType] = [];
            $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType] = [];

            $scope.requestExpedite = false;
            $scope.config = null;

            $scope.objectSearchConfig = null;
            $scope.minDate = moment(new Date());

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

            $scope.addOriginatorFileProofOfIdentity = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFilesDescription[originatorProofOfIdentityDocumentType], file[i]) == false) {
                            $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType].push(file[i]);
                        }
                    }
                }
            };

            $scope.removeOriginatorFileProofOfIdentity = function (index) {
                $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType].splice(index, 1);
            };

            $scope.addSubjectFileProofOfIdentity = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFilesDescription[subjectProofOfIdentityDocumentType], file[i]) == false) {
                            $scope.uploadFilesDescription[subjectProofOfIdentityDocumentType].push(file[i]);
                        }
                    }
                }
            };

            $scope.removeSubjectFileProofOfIdentity = function (index) {
                $scope.uploadFilesDescription[subjectProofOfIdentityDocumentType].splice(index, 1);
            };

            var stateRequest = ObjectLookupService.getStates();
            var requestCategories = ObjectLookupService.getRequestCategories();
            var payFeesRequest = ObjectLookupService.getPayFees();
            var deliveryMethodOfResponsesRequest = ObjectLookupService.getDeliveryMethodOfResponses();
            var newRequestTypes = ObjectLookupService.getRequestTypes();
            var prefixNewRequest = ObjectLookupService.getPersonTitles();
            var requestConfig = ConfigService.getModuleConfig("cases");
            var componentsAgenciesPromise = ObjectLookupService.getLookupByLookupName("componentsAgencies");
            var organizationTypeLookup = ObjectLookupService.getPersonOrganizationRelationTypes();
            var promiseConfigTitle = AdminObjectTitleConfigurationService.getObjectTitleConfiguration();
            var personTypesLookup = ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE, true);
            var getPortal = AdminPortalConfigurationService.getPortalConfig();
            var getCountries = ObjectLookupService.getCountries();
            var getAddressTypes = ObjectLookupService.getAddressTypes();
            var canadaProvinces = ObjectLookupService.getLookupByLookupName('canadaProvinces');
            var japanStates = ObjectLookupService.getLookupByLookupName('japanStates');
            var commonModuleConfig = ConfigService.getModuleConfig("common");

            $q.all([requestConfig, componentsAgenciesPromise, organizationTypeLookup, prefixNewRequest, newRequestTypes,
                deliveryMethodOfResponsesRequest, payFeesRequest, requestCategories, stateRequest, promiseConfigTitle, personTypesLookup,
                getPortal, getCountries, getAddressTypes, canadaProvinces, japanStates, commonModuleConfig]).then(function (data) {

                var moduleConfig = data[0];
                var componentsAgencies = data[1];
                var organizationTypes = data[2];
                var prefixesRequest = data[3];
                var requestTypes = data[4];
                var deliveryMethodOfResponses = data[5];
                var payFees = data[6];
                var categories = data[7];
                var states = data[8];
                var configTitle = data[9];
                var personTypes = data[10];
                var portalConfig = data[11].data;
                var countries = data[12];
                var addressTypes = data[13];
                var canadaProvinces = data[14];
                var japanStates = data[15];
                $scope.commonModuleConfig = data[16];

                if (!Util.isEmpty(configTitle)) {
                    $scope.enableTitle = configTitle.data.CASE_FILE.enableTitleField;
                }
                $scope.organizationTypes = organizationTypes;

                $scope.personTypes = personTypes;

                $scope.config = _.find(moduleConfig.components, {
                    id: "requests"
                });

                $scope.objectSearchConfig = _.find(moduleConfig.components, {
                    id: "objectSearch"
                });

                $scope.configRequest = _.find(moduleConfig.components, {
                    id: "new-subject-access-request"
                });

                $scope.config.data = {};

                $scope.requestTypes = requestTypes;
                $scope.requestSubTypes = $scope.config.requestSubTypes;
                $scope.categories = categories;
                $scope.componentsAgencies = componentsAgencies;
                $scope.dispositionTypes = $scope.config.dispositionTypes;
                $scope.dispositionSubTypes = $scope.config.dispositionSubTypes;
                $scope.deliveryMethodOfResponses = deliveryMethodOfResponses;
                $scope.payFees = payFees;
                $scope.prefixes = prefixesRequest;
                $scope.countries = countries;
                $scope.addressTypes = addressTypes;
                $scope.usStates = states;
                $scope.canadaProvinces = canadaProvinces;
                $scope.japanStates = japanStates;

                //get json data for new Subject Access Request
                angular.copy(Data.getData(), $scope.config.data);

                var defaultRequestType = ObjectLookupService.getPrimaryLookup($scope.requestTypes);
                var defaultRequestCategory = ObjectLookupService.getPrimaryLookup($scope.categories);
                var defaultComponentAgencyFound = ObjectLookupService.getPrimaryLookup($scope.componentsAgencies);
                var defaultAddressType = ObjectLookupService.getPrimaryLookup($scope.addressTypes);
                var defaultCountry = ObjectLookupService.getPrimaryLookup($scope.countries);
                var defaultDeliveryMethod = ObjectLookupService.getPrimaryLookup($scope.deliveryMethodOfResponses);

                if (!Util.isEmpty(defaultComponentAgencyFound)) {
                    $scope.config.data.componentAgency = defaultComponentAgencyFound.key;
                } else {
                    $scope.config.data.componentAgency = $scope.componentsAgencies[0].key;
                }
                if (!Util.isEmpty(defaultRequestType)) {
                    $scope.config.data.requestType = defaultRequestType.key;
                } else {
                    $scope.config.data.requestType = $scope.requestTypes[0].key;
                }
                if (!Util.isEmpty(defaultRequestCategory)) {
                    $scope.config.data.requestCategory = defaultRequestCategory.key;
                } else {
                    $scope.config.data.requestCategory = $scope.categories[0].key;
                }
                if (!Util.isEmpty(defaultDeliveryMethod)) {
                    $scope.config.data.deliveryMethodOfResponse = defaultDeliveryMethod.key;
                } else {
                    $scope.config.data.deliveryMethodOfResponse = $scope.deliveryMethodOfResponses[0].key;
                }
                $scope.config.data.payFee = $scope.payFees[0].key;

                $scope.config.portal = { portalId: portalConfig['portal.id'] };

                $scope.states = "";
                $scope.config.data.originator.person.addresses[0].country = countries[0].key;
                $scope.config.data.originator.person.addresses[0].type = defaultAddressType ? defaultAddressType.key : addressTypes[0].key;
                $scope.config.data.subject.person.addresses[0].country = defaultCountry ? defaultCountry.key : countries[0].key;
                $scope.config.data.subject.person.addresses[0].type = defaultAddressType ? defaultAddressType.key : addressTypes[0].key;
                $scope.config.data.receivedDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");
                $scope.config.data.subject.person.dateOfBirth = moment.utc().format("YYYY-MM-DD");

                $scope.blankPerson = angular.copy($scope.config.data.originator.person);

                if ($scope.config.data.originator.person.addresses[0] && !Util.isEmpty($scope.config.data.originator.person.addresses[0].country)) {
                    $scope.changeStates($scope.config.data.originator.person.addresses[0].country);
                }

                $scope.minDate = moment(new Date());
            });

            $scope.isEmailDaliveryMethod = false;

            $scope.validateForm = function (requestForm) {

                $scope.formInvalid = false;
                $scope.phoneEmpty = false;
                $scope.phoneInvalid = false;
                $scope.emailEmpty = false;
                $scope.emailInvalid = false;
                $scope.confirmEmailEmpty = false;
                $scope.confirmEmailInvalid = false;
                $scope.subjectConfirmEmailEmpty = false;
                $scope.subjectConfirmEmailInvalid = false;
                $scope.zipCodeEmpty = false;
                $scope.zipCodeInvalid = false;
                $scope.subjectEmpty = false;
                $scope.dateRangeInvalid = false;

                if ($scope.config.data.requestType) {

                    if (requestForm.phone.$viewValue === undefined || requestForm.phone.$viewValue === '') {
                        $scope.phoneEmpty = true;
                    } else if (requestForm.phone.$invalid) {
                        $scope.phoneInvalid = true;
                    }
                    if (requestForm.email.$viewValue === undefined || requestForm.email.$viewValue === '') {
                        $scope.emailEmpty = true;
                    } else if (requestForm.email.$invalid) {
                        $scope.emailInvalid = true;
                    }
                    if (requestForm.confirmationEmail.$viewValue === undefined || requestForm.confirmationEmail.$viewValue === '') {
                        $scope.confirmEmailEmpty = true;
                    } else if (requestForm.confirmationEmail.$viewValue !== requestForm.email.$viewValue) {
                        $scope.confirmEmailInvalid = true;
                        $scope.formInvalid = true;
                    }
                    if (requestForm.subjectConfirmationEmail.$viewValue === undefined || requestForm.subjectConfirmationEmail.$viewValue === '') {
                        $scope.subjectConfirmEmailEmpty = true;
                    } else if (requestForm.subjectConfirmationEmail.$viewValue !== requestForm.subjectEmail.$viewValue) {
                        $scope.subjectConfirmEmailInvalid = true;
                        $scope.formInvalid = true;
                    }
                    if (requestForm.subjectZip.$viewValue === undefined || requestForm.subjectZip.$viewValue === '') {
                        $scope.zipCodeEmpty = true;
                    } else if (requestForm.subjectZip.$invalid) {
                        $scope.zipCodeInvalid = true;
                    }
                    if (requestForm.subject.$invalid) {
                        $scope.subjectEmpty = true;
                    }
                    if ($scope.config.data.recordSearchDateFrom > $scope.config.data.recordSearchDateTo) {
                        $scope.formInvalid = true;
                        $scope.dateRangeInvalid = true;
                    }
                    if ($scope.uploadFilesDescription[subjectProofOfIdentityDocumentType].length == 0) {
                        $scope.formInvalid = true;
                    }
                    if (!$scope.subjectSameAsRequester && $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType].length == 0) {
                        $scope.formInvalid = true;
                    }
                    if (requestForm.$valid && !$scope.formInvalid) {
                        $scope.saveNewRequest();
                    } else {
                        $scope.formInvalid = true;
                        $location.hash('topSection1');
                        $anchorScroll();
                    }
                }
            };

            function openDuplicateSubjectPersonPicker(result) {
                $scope.config.data.subject.person.defaultEmail.value = '';
                $scope.subjectConfirmationEmail = '';

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
                            $scope.setSubject(person);
                            $scope.existingSubject = angular.copy($scope.config.data.subject.person);
                            $scope.newSubject = angular.copy($scope.blankPerson);
                        });
                    }
                });
            }

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
                            $scope.setOriginator(person);
                            $scope.existingOriginator = angular.copy($scope.config.data.originator.person);
                            $scope.newOriginator = angular.copy($scope.blankPerson);
                        });
                    }
                });
            }

            $scope.checkExistingEmail = function (person, confirmationEmail) {
                if (person.person.defaultEmail.value === confirmationEmail) {
                    PersonInfoService.queryByEmail(person.person.defaultEmail.value).then(function (result) {
                        if (result.data.response.numFound > 0) {
                            if (person.personType == 'Requester') {
                                openDuplicatePersonPicker(result);
                            } else {
                                openDuplicateSubjectPersonPicker(result);
                            }
                        }
                    });
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
            $scope.searchOriginator = function () {
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
                        $scope.setOriginator(person);
                        $scope.existingOriginator = angular.copy($scope.config.data.originator.person);
                    });
                });

            };

            $scope.searchSubject = function () {
                var params = {
                    showSetPrimary: false,
                    addNewEnabled: false,
                    isDefault: false,
                    types: $scope.personTypes,
                    type: $scope.personTypes[1].key,
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
                        $scope.setSubject(person);
                        $scope.existingSubject = angular.copy($scope.config.data.subject.person);
                    });
                });

            };

            $scope.pickExistingOriginatorChange = function () {

                if ($scope.isPickExistingOriginator) {
                    $scope.newOriginator = angular.copy($scope.config.data.originator.person);
                    $scope.setOriginator($scope.existingOriginator);

                } else {
                    $scope.existingOriginator = angular.copy($scope.config.data.originator.person);
                    $scope.setOriginator($scope.newOriginator);
                }
            };

            $scope.pickExistingSubjectChange = function () {

                if ($scope.isPickExistingSubject) {
                    $scope.newSubject = angular.copy($scope.config.data.subject.person);
                    $scope.setSubject($scope.existingSubject);

                } else {
                    $scope.existingSubject = angular.copy($scope.config.data.subject.person);
                    $scope.setSubject($scope.newSubject);
                }
            };

            // ------------------ end person added ----------------- //

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


            $scope.saveNewRequest = function () {
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

                $scope.config.data.title = $scope.config.data.subject.person.givenName + ' ' + $scope.config.data.subject.person.familyName;

                for (var property in $scope.config.data) {
                    if ($scope.config.data.hasOwnProperty(property)) {
                        basicData[property] = $scope.config.data[property];
                    }
                }
                
                var data = new Blob([angular.toJson(JSOG.encode(Util.omitNg(basicData)))], {
                    type: 'application/json'
                });
                formdata.append('casefile', data);

                for (var property in $scope.uploadFilesDescription) {
                    if ($scope.uploadFilesDescription.hasOwnProperty(property)) {
                        angular.forEach($scope.uploadFilesDescription[property], function (value) {
                            formdata.append(property, value);
                        });
                    }
                }

                if ($scope.config.data.requestType) {
                    saveRequestInfoWithFiles(formdata);
                }

            };

            function createNewPortalUser(personId) {

                RequestInfoService.saveNewPortalUser(personId, $scope.config.portal.portalId).then(function (response) {
                    if (response.registrationStatus === "REGISTRATION_EXISTS") {
                        MessageService.error($translate.instant('cases.newRequest.portalUser.message.error.exists'));
                    } else if (response.registrationStatus === "REGISTRATION_REJECTED") {
                        MessageService.error($translate.instant('cases.newRequest.portalUser.message.error.denied'));
                    } else {
                        MessageService.info($translate.instant('cases.newRequest.portalUser.message.created'));
                    }
                }).catch(function () {
                    MessageService.errorAction();
                });
            }


            var saveRequestInfoWithFiles = function (formdata) {
                RequestInfoService.saveRequestInfoWithFiles(formdata).then(function (response) {
                    $scope.onModalClose();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, response.id);
                    if ($scope.config.data.createNewPortalUser) {
                        createNewPortalUser(response.data.originator.person.id);
                    }
                }, function (error) {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.$emit("report-object-update-failed", error);
                    if (error.data && error.data.message) {
                        if (error.data.field == "duplicateName") {
                            MessageService.error($translate.instant("cases.newRequest.duplicateFilesName.error"));
                        } else {
                            MessageService.error(error.data.message);
                        }
                    } else {
                        MessageService.error(error);
                    }
                });
            };

            $scope.setOriginator = function (originator) {
                if (originator) {
                    $scope.config.data.originator.person = angular.copy(originator);

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

                    if (originator.addresses[0] && !Util.isEmpty(originator.addresses[0].country) && !Util.isEmpty(originator.addresses[0].state)) {
                        $scope.changeStates(originator.addresses[0].country);
                    }

                    $scope.isExistingOriginator = typeof $scope.config.data.originator.person.id !== 'undefined';

                    if ($scope.config.data.originator.person.defaultEmail) {
                        $scope.confirmationEmail = angular.copy($scope.config.data.originator.person.defaultEmail.value);
                    } else {
                        $scope.confirmationEmail = '';
                    }
                }
            };

            $scope.setSubject = function (subject) {
                if (subject) {
                    $scope.config.data.subject.person = angular.copy(subject);

                    if (!$scope.config.data.subject.person.defaultPhone) {
                        var phone = _.find($scope.config.data.subject.person.contactMethods, {
                            type: 'phone'
                        });
                        $scope.config.data.subject.person.defaultPhone = phone;
                    }

                    if (!$scope.config.data.subject.person.defaultEmail) {
                        var email = _.find($scope.config.data.subject.person.contactMethods, {
                            type: 'email'
                        });
                        $scope.config.data.subject.person.defaultEmail = email;
                    }

                    if (subject.addresses[0] && !Util.isEmpty(subject.addresses[0].country) && !Util.isEmpty(subject.addresses[0].state)) {
                        $scope.changeStates(subject.addresses[0].country);
                    }

                    $scope.isExistingSubject = typeof $scope.config.data.subject.person.id !== 'undefined';

                    if ($scope.config.data.subject.person.defaultEmail) {
                        $scope.subjectConfirmationEmail = angular.copy($scope.config.data.subject.person.defaultEmail.value);
                    } else {
                        $scope.subjectConfirmationEmail = '';
                    }
                }
            };

            $scope.isSubjectSameAsRequesterChecked = function (subjectSameAsRequester) {
                if (subjectSameAsRequester) {
                    $scope.config.data.originator = angular.copy($scope.config.data.subject);
                    $scope.confirmationEmail = $scope.subjectConfirmationEmail;
                    $scope.config.data.originator.personType = "Requester";
                    $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType] = [];
                } else {
                    $scope.config.data.originator.person = angular.copy($scope.blankPerson);
                    $scope.confirmationEmail = "";
                    $scope.uploadFilesDescription[originatorProofOfIdentityDocumentType] = [];

                }
            };

            $scope.requestInReleaseStatusSearch = function () {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/privacy-request-search.client.view.html',
                    controller: 'Cases.SARSearchModalController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        $filter: function () {
                            return $scope.configRequest.objectSearch.objectFacetFilter;
                        },
                        $config: function () {
                            return $scope.objectSearchConfig;
                        }
                    }
                });

                modalInstance.result.then(function (chosenObject) {
                    if (chosenObject) {
                        $scope.config.data.originalRequestNumber = chosenObject.name;

                        return;
                    }

                }, function () {
                    // Cancel button was clicked.
                    return [];
                });

            };

            $scope.cancelModal = function () {
                $scope.onModalDismiss();
            };
            $scope.opened = {};
            $scope.opened.openedStart = false;
            $scope.opened.openedEnd = false;

            $scope.openedScanned = {};
            $scope.openedScanned.openedStart = false;
            $scope.openedScanned.openedEnd = false;

            $scope.openedRecordSearchDateFrom = {};
            $scope.openedRecordSearchDateFrom.openedStart = false;
            $scope.openedRecordSearchDateFrom.openedEnd = false;

            $scope.openedRecordSearchDateTo = {};
            $scope.openedRecordSearchDateTo.openedStart = false;
            $scope.openedRecordSearchDateTo.openedEnd = false;

        }]);
