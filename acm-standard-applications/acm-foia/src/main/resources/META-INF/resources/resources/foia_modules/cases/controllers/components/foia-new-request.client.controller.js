'use strict';

angular.module('cases').controller(
    'Cases.NewRequestController',
    ['$scope', '$sce', '$q', '$modal', '$translate', 'ConfigService', 'FOIA.Data', 'Request.InfoService', 'ObjectService', 'modalParams', 'Object.LookupService', 'Util.DateService', 'MessageService', 'UtilService',
        'Requests.RequestsService', 'Dialog.BootboxService', 'Organization.InfoService', '$location', '$anchorScroll', 'Admin.ObjectTitleConfigurationService', 'Person.InfoService',
        function ($scope, $sce, $q, $modal, $translate, ConfigService, Data, RequestInfoService, ObjectService, modalParams, ObjectLookupService, UtilDateService, MessageService, Util, RequestsService, DialogService, OrganizationInfoService, $location, $anchorScroll,
                  AdminObjectTitleConfigurationService, PersonInfoService) {

            $scope.modalParams = modalParams;
            $scope.loading = false;
            $scope.loadingIcon = "fa fa-floppy-o";
            $scope.formInvalid = false;
            $scope.enableTitle = false;

            var descriptionDocumentType = "Description Document";
            var consentDocumentType = "Consent";
            var proofOfIdentityDocumentType = "Proof of Identity";

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

            var prefixNewRequest = ObjectLookupService.getPrefixes();

            var requestConfig = ConfigService.getModuleConfig("cases");

            var componentsAgenciesPromise = ObjectLookupService.getLookupByLookupName("componentsAgencies");
            var organizationTypeLookup = ObjectLookupService.getPersonOrganizationRelationTypes();
            var promiseConfigTitle = AdminObjectTitleConfigurationService.getObjectTitleConfiguration();
            var personTypesLookup = ObjectLookupService.getPersonTypes(ObjectService.ObjectTypes.CASE_FILE, true);

            $q.all([requestConfig, componentsAgenciesPromise, organizationTypeLookup, prefixNewRequest, newRequestTypes, deliveryMethodOfResponsesRequest, payFeesRequest, requestCategories, stateRequest, promiseConfigTitle, personTypesLookup]).then(function (data) {

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
                $scope.states = states;

                //get json data for new foia request
                angular.copy(Data.getData(), $scope.config.data);

                $scope.config.data.organizationAssociations = [];

                $scope.config.data.requestType = $scope.requestTypes[0].key;

                $scope.config.data.requestCategory = $scope.categories[0].key;

                $scope.config.data.componentAgency = $scope.componentsAgencies[0].key;

                $scope.config.data.originator.person.addresses[0].state = $scope.states[0].key;

                $scope.config.data.originator.person.title = $scope.prefixes[0].key;

                $scope.config.data.deliveryMethodOfResponse = $scope.deliveryMethodOfResponses[0].key;

                $scope.config.data.payFee = $scope.payFees[0].key;

            });

            $scope.isEmailDaliveryMethod = false;
            
            $scope.validateForm = function (requestForm) {

                $scope.formInvalid = false;
                $scope.prefixEmpty = false;
                $scope.firstNameEmpty = false;
                $scope.lastNameEmpty = false;
                $scope.phoneEmpty = false;
                $scope.phoneInvalid = false;
                $scope.emailEmpty = false;
                $scope.emailInvalid = false;
                $scope.confirmEmailEmpty = false;
                $scope.confirmEmailInvalid = false;
                $scope.addressEmpty = false;
                $scope.cityEmpty = false;
                $scope.stateEmpty = false;
                $scope.zipCodeEmpty = false;
                $scope.zipCodeInvalid = false;
                $scope.subjectEmpty = false;
                $scope.deliveryMethodOfResponseEmpty = false;
                $scope.requestCategoryEmpty = false;

                if ($scope.isNewRequestType()) {

                    if (requestForm.prefix.$invalid) {
                        $scope.prefixEmpty = true;
                    }
                    if (requestForm.firstName.$invalid) {
                        $scope.firstNameEmpty = true;
                    }
                    if (requestForm.lastName.$invalid) {
                        $scope.lastNameEmpty = true;
                    }
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
                    if (requestForm.address1.$invalid) {
                        $scope.addressEmpty = true;
                    }
                    if (requestForm.city.$invalid) {
                        $scope.cityEmpty = true;
                    }
                    if (requestForm.state.$invalid) {
                        $scope.stateEmpty = true;
                    }
                    if (requestForm.zip.$viewValue === undefined || requestForm.zip.$viewValue === '') {
                        $scope.zipCodeEmpty = true;
                    } else if (requestForm.zip.$invalid) {
                        $scope.zipCodeInvalid = true;
                    }
                    if (requestForm.subject.$invalid) {
                        $scope.subjectEmpty = true;
                    }
                    if (requestForm.requesterCategory.$invalid) {
                        $scope.requestCategoryEmpty = true;
                    }
                    if (requestForm.deliveryMethodOfResponse.$invalid) {
                        $scope.deliveryMethodOfResponseEmpty = true;
                    }
                    if (requestForm.$valid && !$scope.formInvalid) {
                        $scope.saveNewRequest();
                    } else {
                        $scope.formInvalid = true;
                        $location.hash('topSection1');
                        $anchorScroll();
                    }
                }
                else {
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

            $scope.isNewRequestType = function () {
                return $scope.config && $scope.config.data.requestType === 'New Request';
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
                        data.person = person;
                        setPersonAssociation({}, data);
                    })
                });

            };

            function setPersonAssociation(association, data) {
                association.person = data.person;
                association.personType = data.type;

                //if is new created, add it to the person associations list
                if (!$scope.config.data.originator.person.personAssociations) {
                    $scope.config.data.originator.person.personAssociations = [];
                }

                if (!_.includes($scope.config.data.originator.person.personAssociations, association)) {
                    $scope.config.data.originator.person.personAssociations.push(association);
                }

                //populate contact information section
                $scope.config.data.originator.person = angular.copy(association.person);

                var phone = _.find($scope.config.data.originator.person.contactMethods, {
                    type: 'phone'
                });
                $scope.config.data.originator.person.defaultPhone = phone;

                var email = _.find($scope.config.data.originator.person.contactMethods, {
                    type: 'email'
                });

                $scope.config.data.originator.person.defaultEmail = email;
            }

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

                //check if person is existing and remove the contactMethods array
                if ($scope.config.data.originator.person.id != null) {
                    $scope.config.data.originator.person.contactMethods = [];
                }

                if (!$scope.config.data.originator.person.defaultPhone.value) {
                    $scope.config.data.originator.person.defaultPhone = null;
                } else {
                    $scope.config.data.originator.person.defaultPhone.type = "phone";
                    $scope.config.data.originator.person.contactMethods.push($scope.config.data.originator.person.defaultPhone);
                }

                if (Util.isEmpty($scope.config.data.originator.person.defaultEmail) || !$scope.config.data.originator.person.defaultEmail.value) {
                    $scope.config.data.originator.person.defaultEmail = null;
                } else {
                    $scope.config.data.originator.person.defaultEmail.type = "email";
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
                        if (originalRequest.status === 'Released') {
                            saveRequestInfoWithFiles(formdata);

                        }
                    });
                }

            };

            var saveRequestInfoWithFiles = function (formdata) {
                RequestInfoService.saveRequestInfoWithFiles(formdata).then(function (response) {
                    $scope.onModalClose();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    ObjectService.showObject(ObjectService.ObjectTypes.CASE_FILE, response.id);
                }, function (error) {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-floppy-o";
                    $scope.$emit("report-object-update-failed", error);
                    if (error.data && error.data.message) {
                        $scope.error = error.data.message;
                    } else {
                        MessageService.error(error);
                    }
                });
            };

            $scope.populateAppeal = function () {
                var param = {};
                param.caseNumber = $scope.config.data.originalRequestNumber;
                RequestsService.getRequestByNumber(param).$promise.then(function (originalRequest) {
                    if (originalRequest.status === 'Released') {
                        $scope.config.data.details = originalRequest.details;
                        $scope.config.data.requestCategory = originalRequest.requestCategory;
                        $scope.config.data.deliveryMethodOfResponse = originalRequest.deliveryMethodOfResponse;
                        $scope.config.data.originator.person.addresses[0].streetAddress = originalRequest.originator.person.addresses[0].streetAddress;
                        $scope.config.data.originator.person.addresses[0].streetAddress2 = originalRequest.originator.person.addresses[0].streetAddress2;
                        $scope.config.data.originator.person.addresses[0].city = originalRequest.originator.person.addresses[0].city;
                        $scope.config.data.originator.person.addresses[0].state = originalRequest.originator.person.addresses[0].state;
                        $scope.config.data.originator.person.addresses[0].country = originalRequest.originator.person.addresses[0].country;
                        $scope.config.data.originator.person.addresses[0].zip = originalRequest.originator.person.addresses[0].zip;
                    }
                });
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

        }]);
