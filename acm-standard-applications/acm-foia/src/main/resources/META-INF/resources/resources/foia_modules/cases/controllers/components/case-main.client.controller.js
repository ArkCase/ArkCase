'use strict';

angular.module('cases').controller(
    'Cases.MainController',
    ['$scope', '$state', '$stateParams', '$translate', '$rootScope', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'UtilService', 'Util.DateService', 'Object.LookupService', 'LookupService', 'DueDate.Service', 'Admin.HolidayService', 'Admin.FoiaConfigService',
        function ($scope, $state, $stateParams, $translate, $rootScope, CaseInfoService, HelperObjectBrowserService, ConfigService, Util, UtilDateService, ObjectLookupService, LookupService, DueDateService, AdminHolidayService, AdminFoiaConfigService) {

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

                if (!Util.isEmpty($scope.objectInfo.releasedDate)) {
                    $scope.objectInfo.releasedDate = moment($scope.objectInfo.releasedDate).format(UtilDateService.defaultDateTimeFormat);
                }
                if (!Util.isEmpty($scope.objectInfo.recordSearchDateFrom)) {
                    $scope.objectInfo.recordSearchDateFrom = moment(objectInfo.recordSearchDateFrom).format(UtilDateService.defaultDateTimeFormat);
                }
                if (!Util.isEmpty($scope.objectInfo.recordSearchDateTo)) {
                    $scope.objectInfo.recordSearchDateTo = moment($scope.objectInfo.recordSearchDateTo).format(UtilDateService.defaultDateTimeFormat);
                }

                ObjectLookupService.getRequestCategories().then(function (requestCategories) {
                    $scope.requestCategories = requestCategories;
                });

                ObjectLookupService.getPayFees().then(function (payFees) {
                    $scope.payFees = payFees;
                    if ($scope.objectInfo.payFee !== '' && $scope.objectInfo.payFee !== "0" && $scope.objectInfo.payFee !== null) {
                        $scope.payFeeValue = _.find($scope.payFees, function (payFee) {
                            if (payFee.key == $scope.objectInfo.payFee) {
                                return payFee.key;
                            }
                        });
                        $scope.objectInfo.payFee = $scope.payFeeValue.key;
                    } else {
                        $scope.objectInfo.payFee = $scope.payFees[0].key;
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
                populateDispositionTypes($scope.objectInfo);
                populateRequestTrack($scope.objectInfo);
                $scope.populateDispositionSubTypes($scope.objectInfo.disposition, $scope.objectInfo.requestType);
                $scope.originalDueDate = objectInfo.dueDate;

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

            function populateDispositionTypes(objectInfo) {
                ObjectLookupService.getDispositionTypes(objectInfo.requestType).then(function (requestDispositionType) {
                    $scope.dispositionTypes = requestDispositionType;
                    if (objectInfo.disposition != null && objectInfo.disposition != '') {
                        $scope.dispositionValue = _.find($scope.dispositionTypes, function (disposition) {
                            if (disposition.key == objectInfo.disposition) {
                                return disposition.key;
                            }
                        });
                        $scope.objectInfo.disposition = $scope.dispositionValue.key;
                    } else {
                        $scope.objectInfo.disposition = $scope.dispositionTypes[0].key;
                    }

                });
            }

            function populateRequestTrack(objectInfo) {
                ObjectLookupService.getRequestTrack().then(function (requestTrack) {
                    $scope.requestTracks = requestTrack;
                    if (objectInfo.requestTrack != null && objectInfo.requestTrack != '') {
                        $scope.requestTrackValue = _.find($scope.requestTracks, function (requestTrack) {
                            if (requestTrack.key == objectInfo.requestTrack) {
                                return requestTrack.key;
                            }
                        });
                        $scope.objectInfo.requestTrack = $scope.requestTrackValue.key;
                    } else {
                        $scope.objectInfo.requestTrack = $scope.requestTracks[0].key;
                    }
                });
            }

            $scope.isDisabled = true;
            $scope.isChanged = function (dispositionSubtype) {

                if (dispositionSubtype === 'other') {
                    $scope.isDisabled = false;
                } else {
                    $scope.isDisabled = true;
                    $scope.objectInfo.otherReason = "";
                }

            };

            $scope.populateDispositionSubTypes = function (selectedValue, requestType) {
                if (selectedValue === "full-denial" && requestType == "Appeal") {
                    ObjectLookupService.getAppealDispositionSubTypes().then(function (appealDispositionSubType) {
                        $scope.dispositionSubTypes = appealDispositionSubType;
                    });
                } else if (selectedValue === "full-denial" && requestType == "New Request") {
                    ObjectLookupService.getRequestDispositionSubTypes().then(function (requestDispositionSubType) {
                        $scope.dispositionSubTypes = requestDispositionSubType;
                    });
                } else {
                    $scope.dispositionSubTypes = "";
                }
            };

            AdminFoiaConfigService.getFoiaConfig().then(function (response) {
                $scope.extensionWorkingDays = response.data.requestExtensionWorkingDays;
                $scope.requestExtensionWorkingDaysEnabled = response.data.requestExtensionWorkingDaysEnabled;
            }, function (err) {
                MessageService.errorAction();
            });

            $scope.extensionClicked = function ($event) {
                if (!$event.target.checked) {
                    $scope.objectInfo.dueDate = $scope.originalDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.originalDueDate);
                } else {
                    if (!$scope.extendedDueDate) {
                        if ($scope.includeWeekends) {
                            $scope.extendedDueDate = DueDateService.dueDateWithWeekends($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                        } else {
                            $scope.extendedDueDate = DueDateService.dueDateWorkingDays($scope.originalDueDate, $scope.extensionWorkingDays, $scope.holidays);
                        }
                    }
                    $scope.objectInfo.dueDate = $scope.extendedDueDate;
                    $rootScope.$broadcast('dueDate-changed', $scope.extendedDueDate);
                }
            };

            /**
             * Persists the updated casefile metadata to the ArkCase database
             */
            function saveCase(conf) {
                var promiseSaveInfo = Util.errorPromise($translate.instant("common.service.error.invalidData"));
                if (CaseInfoService.validateCaseInfo($scope.objectInfo)) {
                    var objectInfo = Util.omitNg($scope.objectInfo);

                    objectInfo.recordSearchDateFrom = UtilDateService.dateToIsoDateTime($scope.objectInfo.recordSearchDateFrom);
                    objectInfo.recordSearchDateTo = UtilDateService.dateToIsoDateTime($scope.objectInfo.recordSearchDateTo);

                    $scope.originalDueDate = objectInfo.dueDate;
                    $scope.extendedDueDate = undefined;

                    promiseSaveInfo = CaseInfoService.saveFoiaRequestInfo(objectInfo);
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
