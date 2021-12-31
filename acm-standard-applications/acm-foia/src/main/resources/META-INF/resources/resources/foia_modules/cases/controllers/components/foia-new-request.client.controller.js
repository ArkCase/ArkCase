'use strict';

angular.module('cases').controller(
    'Cases.NewRequestController',
    ['$scope', '$sce', '$q', '$modal', '$translate', 'ConfigService', 'FOIA.Data', 'Request.InfoService', 'ObjectService', 'modalParams', 'Object.LookupService', 'Util.DateService', 'MessageService', 'UtilService',
        'Requests.RequestsService', 'Dialog.BootboxService', 'Organization.InfoService', '$location', '$anchorScroll', 'Admin.ObjectTitleConfigurationService', 'Person.InfoService', 'Admin.PortalConfigurationService', 'Admin.FoiaConfigService',
        function ($scope, $sce, $q, $modal, $translate, ConfigService, Data, RequestInfoService, ObjectService, modalParams, ObjectLookupService, UtilDateService, MessageService, Util, RequestsService, DialogService, OrganizationInfoService, $location, $anchorScroll,
                  AdminObjectTitleConfigurationService, PersonInfoService, AdminPortalConfigurationService, AdminFoiaConfigService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.formInvalid = false;
            $scope.enableTitle = false;
            $scope.isPickExistingPerson = false;
            $scope.primaryAddressIndex = 0;
            $scope.maxDate = moment.utc(new Date());
            $scope.receivedDate = new Date();

            var descriptionDocumentType = "Description Document";
            var consentDocumentType = "Consent";
            var proofOfIdentityDocumentType = "Proof of Identity";
            var assocOrgTypeLabel = $translate.instant("cases.newRequest.position.label");
            var assocTypeLabel = $translate.instant("cases.comp.people.type.label");

            $scope.uploadFilesDescription = {};
            $scope.uploadFilesDescription[descriptionDocumentType] = [];
            $scope.uploadFilesDescription[consentDocumentType] = [];
            $scope.uploadFilesDescription[proofOfIdentityDocumentType] = [];

            $scope.requestExpedite = false;
            $scope.config = null;

            $scope.objectSearchConfig = null;

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

            $scope.addFileDescription = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFilesDescription[descriptionDocumentType], file[i]) == false) {
                            $scope.uploadFilesDescription[descriptionDocumentType].push(file[i]);
                        }
                    }
                }
            };

            $scope.removeFileDescription = function (index) {
                $scope.uploadFilesDescription['Description Document'].splice(index, 1);
            };

            $scope.addFileConsent = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFilesDescription[consentDocumentType], file[i]) == false) {
                            $scope.uploadFilesDescription[consentDocumentType].push(file[i]);
                        }
                    }
                }
            };

            $scope.removeFileConsent = function (index) {
                $scope.uploadFilesDescription['Consent'].splice(index, 1);
            };

            $scope.addFileProofOfIdentity = function (file) {
                if (file && file.length > 0) {
                    for (var i = 0; i < file.length; i++) {
                        if (fileArrayContainsFile($scope.uploadFilesDescription[proofOfIdentityDocumentType], file[i]) == false) {
                            $scope.uploadFilesDescription[proofOfIdentityDocumentType].push(file[i]);
                        }
                    }
                }
            };

            $scope.removeFileProofOfIdentity = function (index) {
                $scope.uploadFilesDescription['Proof of Identity'].splice(index, 1);
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
            var getPortal = AdminPortalConfigurationService.getPortalConfigUser();
            var getCountries = ObjectLookupService.getCountries();
            var getAddressTypes = ObjectLookupService.getAddressTypes();
            var canadaProvinces = ObjectLookupService.getLookupByLookupName('canadaProvinces');
            var japanStates = ObjectLookupService.getLookupByLookupName('japanStates');
            var commonModuleConfig = ConfigService.getModuleConfig("common");
            var adminFoiaConfig = AdminFoiaConfigService.getFoiaConfig();
            var positionLookup = ObjectLookupService.getPersonOrganizationRelationTypes();

            $q.all([requestConfig, componentsAgenciesPromise, organizationTypeLookup, prefixNewRequest, newRequestTypes, deliveryMethodOfResponsesRequest, payFeesRequest, requestCategories, stateRequest, promiseConfigTitle, personTypesLookup, getPortal, getCountries, getAddressTypes, canadaProvinces, japanStates, commonModuleConfig, adminFoiaConfig, positionLookup]).then(function (data) {

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
                $scope.enableNewPortalUserCreation = data[17].data.createNewPortalUserOptionOnArkcaseRequestForm;
                $scope.positions = data[18];

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
                    id: "new-foia-request"
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

                //get json data for new foia request
                angular.copy(Data.getData(), $scope.config.data);

                $scope.config.data.organizationAssociations = [];

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

                $scope.config.portal = {portalId: portalConfig['portal.id']};

                $scope.states = "";
                $scope.config.data.originator.person.addresses[0].country = defaultCountry ? defaultCountry.key : countries[0].key;
                $scope.config.data.originator.person.addresses[0].type = defaultAddressType ? defaultAddressType.key : addressTypes[0].key;
                $scope.config.data.receivedDate = moment.utc().format("YYYY-MM-DDTHH:mm:ss.sss");

                $scope.blankPerson = angular.copy($scope.config.data.originator.person);

                if ($scope.config.data.originator.person.addresses[0] && !Util.isEmpty($scope.config.data.originator.person.addresses[0].country)) {
                    $scope.changeStates($scope.config.data.originator.person.addresses[0].country);
                }
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
                $scope.zipCodeEmpty = false;
                $scope.zipCodeInvalid = false;
                $scope.subjectEmpty = false;
                $scope.dateRangeInvalid = false;

                if ($scope.isNewRequestType()) {

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
                    if (requestForm.zip.$viewValue === undefined || requestForm.zip.$viewValue === '') {
                        $scope.zipCodeEmpty = true;
                    } else if (requestForm.zip.$invalid) {
                        $scope.zipCodeInvalid = true;
                    }
                    if (requestForm.subject.$invalid) {
                        $scope.subjectEmpty = true;
                    }
                    if ($scope.config.data.recordSearchDateFrom > $scope.config.data.recordSearchDateTo) {
                        $scope.formInvalid = true;
                        $scope.dateRangeInvalid = true;
                    }
                    if ($scope.config.data.recordSearchDateFrom > $scope.config.data.recordSearchDateTo) {
                        $scope.formInvalid = true;
                        $scope.dateRangeInvalid = true;
                    }
                    if (requestForm.$valid && !$scope.formInvalid) {
                        $scope.saveNewRequest();
                    } else {
                        $scope.formInvalid = true;
                        $location.hash('topSection1');
                        $anchorScroll();
                    }
                } else {
                    if (requestForm.subject.$invalid) {
                        $scope.subjectEmpty = true;
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

            $scope.checkExistingEmail = function () {
                if ($scope.config.data.originator.person.defaultEmail.value === $scope.confirmationEmail) {
                    PersonInfoService.queryByEmail($scope.config.data.originator.person.defaultEmail.value).then(function (result) {
                        if (result.data.response.numFound > 0) {
                            openDuplicatePersonPicker(result);
                        }
                    });
                }
            };


            $scope.isNewRequestType = function () {
                return $scope.config && $scope.config.data.requestType === 'New Request';
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

            $scope.pickExistingUserChange = function () {

                if ($scope.isPickExistingPerson) {
                    $scope.newPerson = angular.copy($scope.config.data.originator.person);
                    $scope.setPerson($scope.existingPerson);

                } else {
                    $scope.existingPerson = angular.copy($scope.config.data.originator.person);
                    $scope.setPerson($scope.newPerson);
                }
            };

            // ------------------ end person added ----------------- //

            $scope.searchOrganization = function () {
                var associationFound = _.find($scope.config.data.originator.person.organizationAssociations, function (item) {
                    return !Util.isEmpty(item) && !Util.isEmpty(item.organization);
                });
                var association = {};
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
                    if (data.organization) {
                        $scope.organizationValue = data.organization.organizationValue;
                        $scope.personPosition = data.type;
                        setOrganizationAssociation(association, data);
                    } else {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function (organization) {
                            data.organization = organization;
                            $scope.organizationValue = data.organization.organizationValue;
                            if(Util.isEmpty($scope.config.data.originator.person.organizationAssociations)){
                                association =_.find($scope.config.data.originator.person.organizationAssociations, function (item) {
                                    return item.organization.organizationId === data.organization.organizationId;
                                });

                            }
                            $scope.personPosition = data.type;
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
                organizationAssociation["associationType"] = 'requestor-organization';
                organizationAssociation["organization"] = data.organization;

                if (data.isDefault) {
                    //find and change previously default organization
                    var defaultAssociation = _.find($scope.config.data.originator.person.organizationAssociations, function (object) {
                        return object.defaultOrganization;
                    });
                    if (defaultAssociation) {
                        defaultAssociation.defaultOrganization = false;
                    }
                    association.person.defaultOrganization = association;
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

                if($scope.config.data.originator.person.addresses.length && Util.isEmpty($scope.config.data.originator.person.addresses[0].country) && Util.isEmpty($scope.config.data.originator.person.addresses[0].type)){
                    $scope.config.data.originator.person.addresses.shift();
                }

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

                if ($scope.isNewRequestType()) {
                    saveRequestInfoWithFiles(formdata);
                } else {
                    var param = {};
                    param.caseNumber = $scope.config.data.originalRequestNumber;
                    RequestsService.getRequestByNumber(param).$promise.then(function (originalRequest) {
                        if (originalRequest.queue.name === 'Release') {
                            saveRequestInfoWithFiles(formdata);

                        }
                    });
                }

            };

            function createNewPortalUser(personId, requestId) {

                RequestInfoService.saveNewPortalUser(personId, $scope.config.portal.portalId, requestId).then(function (response) {
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
                        createNewPortalUser(response.data.originator.person.id, response.data.id);
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

            $scope.populateAppeal = function () {
                var param = {};
                param.caseNumber = $scope.config.data.originalRequestNumber;
                RequestsService.getRequestByNumber(param).$promise.then(function (originalRequest) {
                    if (originalRequest.queue.name === 'Release' && !Util.isEmpty(originalRequest.caseNumber)) {
                        $scope.config.data.details = originalRequest.details;
                        $scope.config.data.title = originalRequest.title;
                        $scope.config.data.requestCategory = originalRequest.requestCategory;
                        $scope.config.data.deliveryMethodOfResponse = originalRequest.deliveryMethodOfResponse;

                        $scope.setPerson(originalRequest.originator.person);
                        $scope.existingPerson = angular.copy($scope.config.data.originator.person);
                    } else {
                        $scope.setPerson($scope.blankPerson);
                    }
                });
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

            $scope.requestInReleaseStatusSearch = function () {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/foia-request-search.client.view.html',
                    controller: 'Cases.FoiaRequestSearchModalController',
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

            $scope.checkLocationRules = function (location) {
                return !_.values(location).every(_.isEmpty)
            }

        }]);
