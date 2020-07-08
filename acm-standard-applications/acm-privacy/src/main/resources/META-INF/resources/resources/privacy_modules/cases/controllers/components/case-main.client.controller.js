'use strict';

angular.module('cases').controller(
    'Cases.MainController',
    ['$scope', '$state', '$stateParams', '$translate', '$rootScope', '$modal', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'UtilService', 'Util.DateService', 'Object.LookupService', 'LookupService', 'DueDate.Service', 'Admin.HolidayService', 'Admin.PrivacyConfigService', 'Admin.ObjectTitleConfigurationService', 'EcmService',
        function ($scope, $state, $stateParams, $translate, $rootScope, $modal, CaseInfoService, HelperObjectBrowserService, ConfigService, Util, UtilDateService, ObjectLookupService, LookupService, DueDateService, AdminHolidayService, AdminPrivacyConfigService, AdminObjectTitleConfigurationService, EcmService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "main",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.objectInfo = objectInfo;

                ObjectLookupService.getRequestCategories().then(function (requestCategories) {
                    $scope.requestCategories = requestCategories;
                });
                
                AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function (configTitle) {
                    if (!Util.isEmpty(configTitle)) {
                        $scope.enableTitle = configTitle.data.CASE_FILE.enableTitleField;
                    }
                });

                ObjectLookupService.getDeliveryMethodOfResponses().then(function (deliveryMethodOfResponses) {
                    $scope.deliveryMethodOfResponses = deliveryMethodOfResponses;
                    if ($scope.objectInfo.deliveryMethodOfResponse != null && $scope.objectInfo.deliveryMethodOfResponse != '' && $scope.objectInfo.deliveryMethodOfResponse != '0') {
                        $scope.deliveryMethodOfResponseValue = _.find($scope.deliveryMethodOfResponses, function (deliveryMethodOfResponse) {
                            if (deliveryMethodOfResponse.key == $scope.objectInfo.deliveryMethodOfResponse) {
                                return deliveryMethodOfResponse.key;
                            }
                        });
                        $scope.objectInfo.deliveryMethodOfResponse = $scope.deliveryMethodOfResponseValue.key;
                    } else {
                        $scope.objectInfo.deliveryMethodOfResponse = $scope.deliveryMethodOfResponses[0].key;
                    }
                });

            };

            ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
                var config = _.find(moduleConfig.components, {
                    id: "requests"
                });
                $scope.requestTypes = config.requestTypes;
            });

            AdminHolidayService.getHolidays().then(function (response) {
                $scope.holidays = response.data.holidays;
                $scope.includeWeekends = response.data.includeWeekends;
            });

            AdminPrivacyConfigService.getPrivacyConfig().then(function (response) {
                $scope.extensionWorkingDays = response.data.requestExtensionWorkingDays;
                $scope.requestExtensionWorkingDaysEnabled = response.data.requestExtensionWorkingDaysEnabled;
                $scope.expediteWorkingDays = response.data.expediteWorkingDays;
                $scope.expediteWorkingDaysEnabled = response.data.expediteWorkingDaysEnabled;
            }, function (err) {
                MessageService.errorAction();
            });

            $scope.extensionClicked = function ($event) {
                if (!$event.target.checked) {
                    resetDueDate();
                } else {
                    if ($scope.includeWeekends) {
                        $scope.extendedDueDate = DueDateService.dueDateWithWeekends($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                    } else {
                        $scope.extendedDueDate = DueDateService.dueDateWorkingDays($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                    }
                    $scope.objectInfo.dueDate = $scope.extendedDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.extendedDueDate);
                }
            };

            function resetDueDate() {
                $scope.objectInfo.dueDate = $scope.originalDueDate;
                $rootScope.$broadcast('dueDate-changed', $scope.originalDueDate);
            }

            function expediteDueDate() {
                if ($scope.expediteWorkingDaysEnabled && !Util.isEmpty($scope.objectInfo.receivedDate)) {
                    if ($scope.includeWeekends) {
                        $scope.expeditedDueDate = DueDateService.dueDateWithWeekends($scope.objectInfo.receivedDate, $scope.expediteWorkingDays, $scope.holidays);
                    } else {
                        $scope.expeditedDueDate = DueDateService.dueDateWorkingDays($scope.objectInfo.receivedDate, $scope.expediteWorkingDays, $scope.holidays);
                    }
                    $scope.objectInfo.dueDate = $scope.expeditedDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.expeditedDueDate);
                }
            }

            /**
             * Persists the updated casefile metadata to the ArkCase database
             */
            function saveCase(conf) {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);

                    $scope.originalDueDate = objectInfo.dueDate;
                    $scope.extendedDueDate = undefined;

                    if (conf != null && typeof conf.limitedDeliveryFlag !== 'undefined') {
                        objectInfo.limitedDeliveryFlag = conf.limitedDeliveryFlag;
                    }

                    promiseSaveInfo = CaseInfoService.saveSubjectAccessRequestInfo(objectInfo);
                    promiseSaveInfo.then(function (caseInfo) {
                        if (conf != null && conf.returnAction) {
                            $scope.$bus.publish(conf.returnAction, caseInfo);
                        }

                        $scope.$emit("report-object-updated", caseInfo);
                        return caseInfo;
                    }, function (error) {
                        $scope.$emit("report-object-update-failed", error);
                        return error;
                    });
                }
                return promiseSaveInfo;
            }

            $scope.$bus.subscribe('ACTION_SAVE_CASE', function (data) {
                saveCase(data);
            });

            // Updates the ArkCase database when the user changes a case attribute
            // in a case top bar menu item and clicks the save check button
            $scope.save = function () {
                saveCase();
            };

            $scope.opened = {};
            $scope.opened.openedStart = false;
            $scope.opened.openedEnd = false;

            $scope.openedRecordSearchDateFrom = {};
            $scope.openedRecordSearchDateFrom.openedStart = false;
            $scope.openedRecordSearchDateFrom.openedEnd = false;

            $scope.openedRecordSearchDateTo = {};
            $scope.openedRecordSearchDateTo.openedStart = false;
            $scope.openedRecordSearchDateTo.openedEnd = false;

        }

    ]);
