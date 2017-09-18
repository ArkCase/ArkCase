'use strict';

angular.module('frevvo').controller('FrevvoController', ['$rootScope', '$scope', '$stateParams', '$sce', '$q', '$modal', '$translate', '$interval'
    , 'UtilService', 'ConfigService', 'TicketService', 'LookupService', 'Frevvo.FormService', 'ServCommService', 'Person.InfoService', 'Object.LookupService'
    , function ($rootScope, $scope, $stateParams, $sce, $q, $modal, $translate, $interval
        , Util, ConfigService, TicketService, LookupService, FrevvoFormService, ServCommService, PersonInfoService, ObjectLookupService) {

        var promiseConfig = ConfigService.getModuleConfig("frevvo");
        var promiseTicket = TicketService.getArkCaseTicket();
        var acmFormsInfo = LookupService.getConfig("acm-forms", ["frevvo.admin.user", "frevvo.admin.password"]);

        $q.all([promiseConfig, promiseTicket, acmFormsInfo]).then(function (data) {
            $scope.config = data[0];
            $scope.acmTicket = data[1].data;
            $scope.acmFormsProperties = data[2];
            var found = _.find(Util.goodArray($scope.config.forms), {name: Util.goodValue($stateParams.name)});
            if (found && (found.formKey || found.formDefault)) {
                var formType = Util.goodValue($scope.acmFormsProperties[found.formKey], found.formDefault);
                var formUrl = FrevvoFormService.buildFrevvoUrl($scope.acmFormsProperties, formType, $scope.acmTicket, $stateParams);
                $scope.frevvoFormUrl = $sce.trustAsResourceUrl(formUrl);

                ServCommService.request($scope, "frevvo", $stateParams.name, found);
            }
        });

        ObjectLookupService.getPersonTypes().then(
            function (personTypes) {
                var options = [];
                _.forEach(personTypes, function (v, k) {
                    options.push({type: v, name: v});
                });
                $scope.personTypes = options;
                return personTypes;
            });

        ConfigService.getModuleConfig('frevvo').then(function(moduleConfig) {
            $scope.frevvoPersonTypes = moduleConfig.dialogPersonPicker.personTypes;
        });

        $scope.iframeLoaded = function () {
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
            if (!Util.isEmpty(document) && !Util.isEmpty(document.getElementById('frevvoFormIframe')) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')) &&
                document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe').length > 0 && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0]) && !Util.isEmpty(document.getElementById('frevvoFormIframe').contentWindow.document.getElementsByTagName('iframe')[0].contentWindow)
            ) {
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
            params.filter = '"Object Type": USER' + owningGroup;
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/frevvo/views/frevvo-participants-picker-modal.client.view.html",
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
                    customParams.chargeCodes = _.filter(customParams.chargeCodes, function (code) {
                        return data.data.itemsToExclude.indexOf(code.name) == -1;
                    });
                }

                modalInstance = $modal.open({
                    templateUrl: "modules/frevvo/views/frevvo-timesheet-custom-modal-picker.view.html",
                    controller: ['$scope', '$modalInstance', 'customParams', function ($scope, $modalInstance, customParams) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = customParams.header;
                        $scope.gridOptions = {
                            enableRowSelection: true,
                            enableRowHeaderSelection: false,
                            multiSelect: false,
                            columnDefs: customParams.columnDefs,
                            onRegisterApi: function (gridApi) {
                                $scope.myGridApi = gridApi;
                                $scope.myGridApi.selection.on.rowSelectionChanged($scope, function (row) {
                                    $scope.selectedItem = row.entity;
                                });
                            }
                        };
                        $scope.gridOptions.data = customParams.chargeCodes;
                        $scope.gridOptions.data.totalItems = customParams.chargeCodes.length;
                        $scope.onClickOk = function () {
                            $modalInstance.close($scope.selectedItem);
                        };
                        $scope.onClickCancel = function () {
                            $modalInstance.dismiss('cancel');
                        }
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        customParams: customParams
                    }
                });
            }
            else {
                var params = {};
                if (!Util.isEmpty(data.data) && !Util.isEmpty(data.data.objectType)) {
                    var excludeObjectsFilter = "";
                    params.filter = '"Object Type":' + data.data.objectType;
                    if (!Util.isEmpty(data.data) && (data.data.itemsToExclude.length > 0)) {
                        excludeObjectsFilter = "&-name:";
                        _.forEach(data.data.itemsToExclude, function (item) {
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
                    controller: ['$scope', '$modalInstance', 'ConfigService', 'params', function ($scope, $modalInstance, ConfigService, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.header = params.header;
                        $scope.filter = params.filter;
                        $scope.config = params.config;
                        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                            var customization = Util.goodMapValue(moduleConfig, "customization", {});
                            if (customization) {
                                $scope.customization = customization;
                            }
                        });
                    }],
                    animation: true,
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: params
                    }
                });
            }

            modalInstance.result.then(function (selected) {
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

        function pickPerson(data) {
            var params = {};
            var message = {};

            message.source = "arkcase";
            message.action = "fill-person-picker-data";
            message.elementId = data.elementId;
            message.pickerType = data.pickerType;

            if(message.pickerType === "initiator") {
                var initiatorTypeExists = $scope.personTypes.find(function(el) {
                    return el.type === $scope.frevvoPersonTypes.initiatorType.type && el.name === $scope.frevvoPersonTypes.initiatorType.name;
                });

                if(initiatorTypeExists === undefined) {
                    $scope.personTypes.push($scope.frevvoPersonTypes.initiatorType);
                }
                params.type = $scope.frevvoPersonTypes.initiatorType.type;
                params.typeDisabled = true;
            }
            else {
                params.typeDisabled = false;
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
                    params: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                var returnMessage = {};

                 if (data.isNew) {
                    PersonInfoService.savePersonInfoWithPictures(data.person, data.personImages).then(function (response) {
                        data.personId = response.data.id;
                        data.person = response.data;

                        returnMessage.personId = data.personId;
                        returnMessage.fullName = (data.person.givenName + " " + data.person.familyName).trim();
                        returnMessage.personType = data.type;

                        message.data = returnMessage;
                        $scope.frevvoMessaging.send(message);
                    });
                } else {
                    PersonInfoService.getPersonInfo(data.personId).then(function (person) {

                        returnMessage.personId = person.id;
                        returnMessage.fullName = (person.givenName + " " + person.familyName).trim();
                        returnMessage.personType = data.type;

                        message.data = returnMessage;
                        $scope.frevvoMessaging.send(message);
                    })
                }
            });
        }

    }
]);