'use strict';

angular.module('frevvo').controller(
        'FrevvoController',
        [
                '$rootScope',
                '$scope',
                '$stateParams',
                '$sce',
                '$q',
                '$modal',
                '$translate',
                '$interval',
                'UtilService',
                'ConfigService',
                'TicketService',
                'LookupService',
                'Frevvo.FormService',
                'ServCommService',
                'Person.InfoService',
                'Object.LookupService',
                'Organization.InfoService',
                function($rootScope, $scope, $stateParams, $sce, $q, $modal, $translate, $interval, Util, ConfigService, TicketService, LookupService, FrevvoFormService, ServCommService, PersonInfoService, ObjectLookupService, OrganizationInfoService) {

                    var promiseConfig = ConfigService.getModuleConfig("frevvo");
                    var promiseTicket = TicketService.getArkCaseTicket();
                    var acmFormsInfo = LookupService.getConfig("acm-forms", [ "frevvo.admin.user", "frevvo.admin.password" ]);
                    $scope.userSearchConfig = _.find(promiseConfig.components, {
                        id: "userSearch"
                    });

                    $scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                        init(); // recreate frevvo iframe url on language change
                    });

                    function init() {
                        $q.all([ promiseConfig, promiseTicket, acmFormsInfo ]).then(function(data) {
                            $scope.config = data[0];
                            $scope.acmTicket = data[1].data;
                            $scope.acmFormsProperties = data[2];
                            var found = _.find(Util.goodArray($scope.config.forms), {
                                name: Util.goodValue($stateParams.name)
                            });
                            if (found && (found.formKey || found.formDefault)) {
                                var formType = Util.goodValue($scope.acmFormsProperties[found.formKey], found.formDefault);
                                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, formType, $scope.acmTicket, $stateParams);
                                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);

                                ServCommService.request($scope, "frevvo", $stateParams.name, found);
                            }
                        });
                    }
                    init();
                    ObjectLookupService.getPersonTypes("CASE_FILE", true).then(function response(personTypes) {
                        $scope.caseFilePersonInitiatorTypes = personTypes;
                    });

                    ObjectLookupService.getPersonTypes("CASE_FILE", false).then(function response(personTypes) {
                        $scope.caseFilePersonTypes = personTypes;
                    });

                    ObjectLookupService.getPersonTypes("COMPLAINT", true).then(function response(personTypes) {
                        $scope.complaintPersonInitiatorTypes = personTypes;
                    });

                    ObjectLookupService.getPersonTypes("COMPLAINT", false).then(function response(personTypes) {
                        $scope.complaintPersonTypes = personTypes;
                    });

                    $scope.iframeLoaded = function() {
                        startInitFrevvoMessaging();
                    };

                    var initFrevvoMessagingPromise;

                    function startInitFrevvoMessaging() {
                        stopInitFrevvoMessaging();
                        initFrevvoMessagingPromise = $interval(initFrevvoMessaging, 250);
                    }

                    function stopInitFrevvoMessaging() {
                        $interval.cancel(initFrevvoMessagingPromise);
                    }

                    function initFrevvoMessaging() {
                        var frevvoIframe = getFrevvoIframe();
                        if (!Util.isEmpty(frevvoIframe)) {
                            stopInitFrevvoMessaging();
                            if (Util.isEmpty($rootScope.frevvoMessaging)) {
                                $rootScope.frevvoMessaging = {};
                                $rootScope.frevvoMessaging.receiver = frevvoIframe;
                                $rootScope.frevvoMessaging.send = function send(message) {
                                    if (!Util.isEmpty($rootScope.frevvoMessaging.receiver)) {
                                        var targetOrigin = getTargetOrigin();
                                        $rootScope.frevvoMessaging.receiver.postMessage(message, targetOrigin);
                                    }
                                };
                                $rootScope.frevvoMessaging.receive = function receive(e) {
                                    if (!Util.isEmpty(e) && !Util.isEmpty(e.data) && !Util.isEmpty(e.data.source) && e.data.source == "frevvo" && !Util.isEmpty(e.data.action)) {
                                        // Do actions sent from Frevvo
                                        if (e.data.action == "open-user-picker") {
                                            pickUser(e.data);
                                        }
                                        if (e.data.action == "open-object-picker") {
                                            pickObject(e.data);
                                        }
                                        if (e.data.action == "open-person-picker") {
                                            pickPerson(e.data);
                                        }
                                        if (e.data.action == "open-organization-picker") {
                                            pickOrganization(e.data);
                                        }
                                        if (e.data.action == "open-user-or-group-picker") {
                                            pickUserOrGroup(e.data);
                                        }
                                    }
                                };

                                window.addEventListener("message", $rootScope.frevvoMessaging.receive);
                            } else {
                                $rootScope.frevvoMessaging.receiver = frevvoIframe;
                            }
                        }
                    }

                    function getTargetOrigin() {
                        var protocol = $scope.acmFormsProperties['frevvo.protocol'];
                        var host = $scope.acmFormsProperties['frevvo.host'];
                        var targetOrigin = '*';
                        if (protocol && host) {
                            targetOrigin = protocol + "://" + host;
                        }
                        return targetOrigin;
                    }

                    function getFrevvoIframe() {
                        if (!Util.isEmpty(document) && !Util.isEmpty(document.getElementById('frevvoFormIframe')) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document)
                                && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')) && document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe').length > 0
                                && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0]) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow)) {
                            return document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow;
                        }

                        return null;
                    }

                    function pickUser(data) {
                        var params = {};

                        var owningGroup = "";
                        if (!Util.isEmpty(data.data) && !Util.isEmpty(data.data.owningGroup)) {
                            owningGroup = '&fq="Group": ' + data.data.owningGroup;
                        }

                        params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
                        params.filter = '"Object Type": USER' + '&fq="status_lcs": "VALID"' + owningGroup;
                        params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

                        var modalInstance = $modal.open({
                            templateUrl: "modules/frevvo/views/frevvo-participants-picker-modal.client.view.html",
                            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                                $scope.modalInstance = $modalInstance;
                                $scope.header = params.header;
                                $scope.filter = params.filter;
                                $scope.config = params.config;
                            } ],
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });
                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {

                                var message = {};
                                message.source = "arkcase";
                                message.data = selected;
                                message.action = "fill-user-picker-data";
                                message.elementId = data.elementId;

                                $scope.frevvoMessaging.send(message);
                            }
                        });
                    }

                    function pickObject(data) {
                        var modalInstance;

                        if (!Util.isEmpty(data.data) && !Util.isEmpty(data.data.objectType) && data.data.objectType == 'OTHER') {
                            var customParams = {};
                            customParams.chargeCodes = Util.goodMapValue($scope.config, "timesheetCustomPicker.otherTypeChargeCodes");
                            customParams.columnDefs = Util.goodMapValue($scope.config, "timesheetCustomPicker.columnDefs")
                            customParams.header = $translate.instant("frevvo.timesheetCustomPicker.header");
                            if (!Util.isEmpty(data.data.itemsToExclude)) {
                                customParams.chargeCodes = _.filter(customParams.chargeCodes, function(code) {
                                    return data.data.itemsToExclude.indexOf(code.name) == -1;
                                });
                            }

                            modalInstance = $modal.open({
                                templateUrl: "modules/frevvo/views/frevvo-timesheet-custom-modal-picker.view.html",
                                controller: [ '$scope', '$modalInstance', 'customParams', function($scope, $modalInstance, customParams) {
                                    $scope.modalInstance = $modalInstance;
                                    $scope.header = customParams.header;
                                    $scope.gridOptions = {
                                        enableRowSelection: true,
                                        enableRowHeaderSelection: false,
                                        multiSelect: false,
                                        columnDefs: customParams.columnDefs,
                                        onRegisterApi: function(gridApi) {
                                            $scope.myGridApi = gridApi;
                                            $scope.myGridApi.selection.on.rowSelectionChanged($scope, function(row) {
                                                $scope.selectedItem = row.entity;
                                            });
                                        }
                                    };
                                    $scope.gridOptions.data = customParams.chargeCodes;
                                    $scope.gridOptions.data.totalItems = customParams.chargeCodes.length;
                                    $scope.onClickOk = function() {
                                        $modalInstance.close($scope.selectedItem);
                                    };
                                    $scope.onClickCancel = function() {
                                        $modalInstance.dismiss('cancel');
                                    }
                                } ],
                                animation: true,
                                size: 'lg',
                                backdrop: 'static',
                                resolve: {
                                    customParams: customParams
                                }
                            });
                        } else {
                            var params = {};
                            if (!Util.isEmpty(data.data) && !Util.isEmpty(data.data.objectType)) {
                                var excludeObjectsFilter = "";
                                params.filter = '"Object Type":' + data.data.objectType;
                                if (!Util.isEmpty(data.data) && (data.data.itemsToExclude.length > 0)) {
                                    excludeObjectsFilter = "&-name:";
                                    _.forEach(data.data.itemsToExclude, function(item) {
                                        excludeObjectsFilter += item;
                                    });
                                }
                                params.filter += excludeObjectsFilter;
                            }

                            params.filter = params.filter.replace(/&/gi, '%26');
                            params.header = $translate.instant("frevvo.dialogObjectPicker.header");
                            params.config = Util.goodMapValue($scope.config, "dialogObjectPicker");
                            modalInstance = $modal.open({
                                templateUrl: "modules/frevvo/views/frevvo-object-picker-modal.client.view.html",
                                controller: [ '$scope', '$modalInstance', 'ConfigService', 'params', function($scope, $modalInstance, ConfigService, params) {
                                    $scope.modalInstance = $modalInstance;
                                    $scope.header = params.header;
                                    $scope.filter = params.filter;
                                    $scope.config = params.config;
                                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                                        var customization = Util.goodMapValue(moduleConfig, "customization", {});
                                        if (customization) {
                                            $scope.customization = customization;
                                        }
                                    });
                                } ],
                                animation: true,
                                size: 'lg',
                                backdrop: 'static',
                                resolve: {
                                    params: params
                                }
                            });
                        }

                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {
                                var message = {};
                                message.source = "arkcase";
                                message.data = selected;
                                message.action = "fill-object-picker-data";
                                message.elementId = data.elementId;
                                $scope.frevvoMessaging.send(message);
                            }
                        });
                    }

                    function saveNewPersonAndUpdateFrevvo(data, message) {
                        var returnMessage = {};

                        PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function(response) {
                            returnMessage.personId = response.data.id;
                            returnMessage.fullName = (response.data.givenName + " " + response.data.familyName).trim();
                            returnMessage.firstName = response.data.givenName;
                            returnMessage.middleName = response.data.middleName;
                            returnMessage.lastName = response.data.familyName;
                            returnMessage.phone = !Util.isEmpty(response.data.defaultPhone) ? response.data.defaultPhone.value : '';
                            returnMessage.fax = !Util.isEmpty(response.data.defaultFax) ? response.data.defaultFax.value : '';
                            returnMessage.email = !Util.isEmpty(response.data.defaultEmail) ? response.data.defaultEmail.value : '';
                            returnMessage.personType = data.type;

                            message.data = returnMessage;
                            $scope.frevvoMessaging.send(message);
                        });
                    }

                    function retrieveExistingPersonAndUpdateFrevvo(data, message) {
                        var returnMessage = {};

                        PersonInfoService.getPersonInfo(data.personId).then(function(person) {
                            returnMessage.personId = person.id;
                            returnMessage.fullName = (person.givenName + " " + person.familyName).trim();
                            returnMessage.firstName = person.givenName;
                            returnMessage.middleName = person.middleName;
                            returnMessage.lastName = person.familyName;
                            returnMessage.phone = !Util.isEmpty(person.defaultPhone) ? person.defaultPhone.value : '';
                            returnMessage.fax = !Util.isEmpty(person.defaultFax) ? person.defaultFax.value : '';
                            returnMessage.email = !Util.isEmpty(person.defaultEmail) ? person.defaultEmail.value : '';
                            returnMessage.personType = data.type;

                            message.data = returnMessage;
                            $scope.frevvoMessaging.send(message);
                        });
                    }

                    function pickPerson(data) {
                        var params = {};
                        var message = {};

                        message.source = "arkcase";
                        message.action = "fill-person-picker-data";
                        message.elementId = data.elementId;
                        message.pickerType = data.pickerType;

                        if (message.pickerType === "initiator") {
                            if (data.formType === "CASE_FILE") {
                                $scope.personTypes = $scope.caseFilePersonInitiatorTypes;
                            } else if (data.formType === "COMPLAINT") {
                                $scope.personTypes = $scope.complaintPersonInitiatorTypes;
                            }
                            params.type = '';
                            params.typeEnabled = false;

                            if (!Util.isArrayEmpty($scope.personTypes)) {
                                params.type = $scope.personTypes[0].key;
                            }
                        } else if (message.pickerType === "people") {
                            if (data.formType === "CASE_FILE") {
                                $scope.personTypes = $scope.caseFilePersonTypes;
                            } else if (data.formType === "COMPLAINT") {
                                $scope.personTypes = $scope.complaintPersonTypes;
                            }
                            params.typeEnabled = true;
                        }

                        params.pickerType = message.pickerType;
                        params.types = $scope.personTypes;

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/common/views/add-person-modal.client.view.html',
                            controller: 'Common.AddPersonModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.isNew) {
                                saveNewPersonAndUpdateFrevvo(data, message);
                            } else {
                                retrieveExistingPersonAndUpdateFrevvo(data, message);
                            }
                        });
                    }

                    function saveNewOrganizationAndUpdateFrevvo(data, message) {
                        OrganizationInfoService.saveOrganizationInfo(data.organization).then(function(organization) {
                            message.data = createOrganizationFrevvoMessage(organization, data);
                            $scope.frevvoMessaging.send(message);
                        });
                    }

                    function retrieveExistingOrganizationAndUpdateFrevvo(data, message) {
                        OrganizationInfoService.getOrganizationInfo(data.organizationId).then(function(organization) {
                            message.data = createOrganizationFrevvoMessage(organization, data);
                            $scope.frevvoMessaging.send(message);
                        });
                    }

                    function createOrganizationFrevvoMessage(organization, data) {
                        var returnMessage = {};

                        returnMessage.id = organization.organizationId;
                        returnMessage.name = organization.organizationValue;
                        returnMessage.address = !Util.isEmpty(organization.defaultAddress) ? organization.defaultAddress.streetAddress : '';
                        returnMessage.city = !Util.isEmpty(organization.defaultAddress) ? organization.defaultAddress.city : '';
                        returnMessage.state = !Util.isEmpty(organization.defaultAddress) ? organization.defaultAddress.state : '';
                        returnMessage.zip = !Util.isEmpty(organization.defaultAddress) ? organization.defaultAddress.zip : '';
                        returnMessage.phone = !Util.isEmpty(organization.defaultPhone) ? organization.defaultPhone.value : '';
                        returnMessage.email = !Util.isEmpty(organization.defaultEmail) ? organization.defaultEmail.value : '';
                        returnMessage.type = data.type;

                        return returnMessage;
                    }

                    function pickOrganization(data) {
                        var params = {};
                        var message = {};

                        message.source = "arkcase";
                        message.action = "fill-organization-picker-data";
                        message.elementId = data.elementId;

                        // TODO: I can see in the case module and complaint module
                        // TODO: still we are using person types instead of some custom organization types
                        // TODO: Once this thing is changed there, we should change here too
                        if (data.formType === "CASE_FILE") {
                            $scope.organizationTypes = $scope.caseFilePersonTypes;
                        } else if (data.formType === "COMPLAINT") {
                            $scope.organizationTypes = $scope.complaintPersonTypes;
                        }
                        params.typeEnabled = true;
                        params.types = $scope.organizationTypes;

                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/common/views/add-organization-modal.client.view.html',
                            controller: 'Common.AddOrganizationModalController',
                            size: 'md',
                            backdrop: 'static',
                            resolve: {
                                params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(data) {
                            if (data.isNew) {
                                saveNewOrganizationAndUpdateFrevvo(data, message);
                            } else {
                                retrieveExistingOrganizationAndUpdateFrevvo(data, message);
                            }
                        });
                    }

                    function pickUserOrGroup(data) {

                        var params = {};

                        //params.header = $translate.instant("CHOOSE USER OR GROUP WHATEVER");
                        params.filter = '"object_type_s":(GROUP OR USER)&fq="status_lcs":(ACTIVE OR VALID)';
                        params.config = Util.goodMapValue($scope.config, "userOrGroupSearch");

                        var modalInstance = $modal.open({
                            templateUrl: "modules/frevvo/views/frevvo-user-and-groups-picker-modal.client.view.html",
                            controller: [ '$scope', '$modalInstance', '$filter', '$extraFilter', '$config', 'data', function($scope, $modalInstance, $filter, $extraFilter, $config, data) {
                                $scope.filter = $filter;
                                $scope.extraFilter = $extraFilter;
                                $scope.modalInstance = $modalInstance;
                                $scope.config = $config;
                                if (data.data.participantType == 'assignee') {
                                    $scope.secondGrid = 'true';
                                }
                            } ],
                            animation: true,
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $filter: function() {
                                    return params.filter;
                                },
                                $extraFilter: function() {
                                    return "&fq=\"name\": ";
                                },
                                $config: function() {
                                    return params.config;
                                },
                                data: function() {
                                    return data;
                                }
                            }
                        });
                        modalInstance.result.then(function(selected) {
                            if (!Util.isEmpty(selected)) {

                                var message = {
                                    data: {}
                                };
                                message.source = "arkcase";

                                if (!Util.isEmpty(selected.masterSelectedItem)) {
                                    message.data.fullName = selected.masterSelectedItem.name;
                                    message.data.personId = selected.masterSelectedItem.object_id_s;
                                    message.data.personType = selected.masterSelectedItem.object_type_s;
                                    message.data.title = selected.masterSelectedItem.title_parseable;
                                }

                                else if (!Util.isEmpty(selected)) {
                                    message.data.fullName = selected.name;
                                    message.data.personId = selected.object_id_s;
                                    message.data.personType = selected.object_type_s;
                                    message.data.title = selected.title_parseable;
                                }

                                if (data.data.participantType === 'assignee') {
                                    message.data.groupName = selected.detailSelectedItems.name;
                                }

                                message.action = "fill-user-group-picker-data";
                                message.elementId = data.elementId;

                                $scope.frevvoMessaging.send(message);
                            }
                        });
                    }

                } ]);