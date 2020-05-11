'use strict';

angular.module('cases').controller(
    'Cases.MainController',
    ['$scope', '$state', '$stateParams', '$translate', '$rootScope', '$modal', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'UtilService', 'Util.DateService', 'Object.LookupService', 'LookupService', 'DueDate.Service', 'Admin.HolidayService', 'Admin.FoiaConfigService', 'Admin.ObjectTitleConfigurationService', 'EcmService',
        function ($scope, $state, $stateParams, $translate, $rootScope, $modal, CaseInfoService, HelperObjectBrowserService, ConfigService, Util, UtilDateService, ObjectLookupService, LookupService, DueDateService, AdminHolidayService, AdminFoiaConfigService, AdminObjectTitleConfigurationService, EcmService) {

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

                populateDispositionCategories();
                populateDeniedDispositionCategories();
                populateOtherReasons();
                populateRequestTrack($scope.objectInfo);
                populateAppealDispositionCategories();
                populateAppealDispositionReasons();
                populateAppealOtherReasons();

                if ($scope.objectInfo.disposition === 'completely-reversed') {
                    $scope.showDispositionReasonsFlag = false;
                } else {
                    $scope.showDispositionReasonsFlag = true;
                }
                $scope.previousDueDate = objectInfo.dueDate;
                if (objectInfo.requestTrack === 'expedite') {
                    if ($scope.includeWeekends) {
                        $scope.previousDueDate = DueDateService.dueDateWithWeekends($scope.objectInfo.dueDate, $scope.expediteWorkingDays, $scope.holidays);
                    } else {
                        $scope.previousDueDate = DueDateService.dueDateWorkingDays($scope.objectInfo.dueDate, $scope.expediteWorkingDays, $scope.holidays);
                    }
                }
                $scope.originalDueDate = $scope.previousDueDate;
                $scope.enableDispositionClosedDate = objectInfo.dispositionClosedDate == null;

                var otherExistsInAppealReasons = _.some($scope.objectInfo.dispositionReasons, function (value) {
                    return value.reason === 'other';
                });

                if (otherExistsInAppealReasons) {
                    $scope.isAppealOtherReasonDisabled = false;
                } else {
                    $scope.isAppealOtherReasonDisabled = true;
                }
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

            function populateDispositionCategories() {
                ObjectLookupService.getLookupByLookupName('requestDispositionType').then(function (requestDispositionType) {
                    $scope.dispositionCategories = requestDispositionType;
                    if ($scope.objectInfo.disposition) {
                        var found = _.find(requestDispositionType, {
                            key: $scope.objectInfo.disposition
                        });
                        if (!Util.isEmpty(found)) {
                            $scope.objectInfo.disposition = $translate.instant(found.value);
                        }
                    }
                });
            }

            function populateDeniedDispositionCategories() {
                ObjectLookupService.getLookupByLookupName('requestDispositionSubType').then(function (requestDispositionSubType) {
                    $scope.dispositionDeniedCategories = requestDispositionSubType;
                    if ($scope.objectInfo.disposition) {
                        var found = _.find(requestDispositionSubType, {
                            key: $scope.objectInfo.disposition
                        });
                        if (!Util.isEmpty(found)) {
                            $scope.objectInfo.disposition = $translate.instant(found.value);
                        }
                    }
                });
            }

            function populateOtherReasons() {
                ObjectLookupService.getLookupByLookupName('requestOtherReason').then(function (requestOtherReasons) {
                    $scope.otherReasons = requestOtherReasons;
                    if ($scope.objectInfo.otherReason) {
                        var found = _.find(requestOtherReasons, {
                            key: $scope.objectInfo.otherReason
                        });
                        if (!Util.isEmpty(found)) {
                            $scope.objectInfo.otherReason = $translate.instant(found.value);
                        }
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

            $scope.requestTrackChanged = function (requestTrack) {
                if (requestTrack === 'expedite') {
                    expediteDueDate();
                } else {
                    resetDueDate();
                }
            };

            function populateAppealDispositionCategories() {
                if ($scope.objectInfo.requestType == "Appeal") {
                    ObjectLookupService.getLookupByLookupName('appealDispositionType').then(function (appealDispositionType) {
                        $scope.appealDispositionCategories = appealDispositionType;

                        if (Util.isEmpty($scope.objectInfo.disposition)) {
                            $scope.appealDispositionValue = null;
                        } else {
                            var disposition = _.find($scope.appealDispositionCategories, {
                                key: $scope.objectInfo.disposition
                            });
                            if (!Util.isEmpty(disposition)) {
                                $scope.appealDispositionValue = $translate.instant(disposition.value);
                            }
                        }
                    });
                }
            }

            function populateAppealDispositionReasons() {
                if ($scope.objectInfo.requestType == "Appeal") {
                    ObjectLookupService.getAppealDispositionReasons().then(function (appealDispositionReasons) {
                        $scope.appealDispositionReasons = appealDispositionReasons;
                    });
                }
            }

            function populateAppealOtherReasons() {
                ObjectLookupService.getAppealOtherReasons().then(function (appealOtherReasons) {
                    $scope.appealOtherReasons = appealOtherReasons;
                    if ($scope.objectInfo.otherReason) {
                        var found = _.find(appealOtherReasons, {
                            key: $scope.objectInfo.otherReason
                        });
                        if (found) {
                            $scope.isAppealCustomReason = false;
                        } else {
                            $scope.isAppealCustomReason = true;
                        }
                    }
                });
            }

            $scope.onAppealReasonSelected = function (reason) {
                if (reason) {
                    var reasonExists = _.some($scope.objectInfo.dispositionReasons, function (value) {
                        return value.reason === reason;
                    });
                    if (!reasonExists) {

                        var dispositionReason = {
                            reason: reason,
                            caseId: $scope.objectInfo.id,
                            requestType: $scope.objectInfo.requestType
                        };

                        $scope.objectInfo.dispositionReasons.push(dispositionReason);

                        if (reason === 'other') {
                            $scope.isAppealOtherReasonDisabled = false;
                        } else {
                            $scope.isAppealOtherReasonDisabled = true;
                        }

                        if (reason === 'other') {
                            if (Util.isEmpty($scope.objectInfo.otherReason)) {
                                $scope.openAddOtherReasonInAppeal();
                            }
                        }
                    } else {
                        _.forEach($scope.objectInfo.dispositionReasons, function (disReason, i) {
                            if (Util.compare(disReason.reason, reason)) {
                                $scope.objectInfo.dispositionReasons.splice(i, 1);
                                return false;
                            }
                        });

                        //if other was unchecked in reasons
                        if (reason === 'other') {
                            $scope.isAppealOtherReasonDisabled = true;
                            $scope.objectInfo.otherReason = null;
                        }
                    }
                }

            };

            $scope.isAppealReasonChecked = function (reason) {
                return _.some($scope.objectInfo.dispositionReasons, function (value) {
                    return value.reason === reason;
                });
            };

            AdminFoiaConfigService.getFoiaConfig().then(function (response) {
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
                    if (conf != null && typeof conf.requestDisposition !== 'undefined') {
                        objectInfo.disposition = conf.requestDisposition;
                    }
                    if (conf != null && typeof conf.requestOtherReason !== 'undefined') {
                        objectInfo.otherReason = conf.requestOtherReason;
                    }
                    if (conf != null && typeof conf.dispositionValue !== 'undefined') {
                        objectInfo.dispositionValue = conf.dispositionValue;
                    }
                    if (conf != null && typeof conf.dispositionReasons !== 'undefined') {
                        objectInfo.dispositionReasons = conf.dispositionReasons;
                    }
                    promiseSaveInfo = CaseInfoService.saveFoiaRequestInfo(objectInfo);
                    promiseSaveInfo.then(function (caseInfo) {
                        if (conf != null && conf.returnAction) {
                            $scope.$bus.publish(conf.returnAction, caseInfo);
                        }

                        if (caseInfo.dispositionClosedDate != null) {
                            $scope.isAppealOtherReasonDisabled = true;
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

            $scope.openAddAppealDispositionCategory = function () {
                addAppealDispositionCategory(false);
            };

            function addAppealDispositionCategory(dispositionRequiredFlag) {
                var params = {
                    disposition: $scope.objectInfo.disposition,
                    dispositionReasons: $scope.objectInfo.dispositionReasons,
                    otherReason: $scope.objectInfo.otherReason,
                    caseId: $scope.objectInfo.id,
                    isDispositionRequired: dispositionRequiredFlag
                };

                var modalInstance = $modal.open({
                    templateUrl: "modules/cases/views/components/add-appeal-disposition-category-modal.client.view.html",
                    controller: 'Cases.AddAppealDispositionCategoriesModalController',
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (selected) {
                    if (!Util.isEmpty(selected)) {
                        $scope.showDispositionReasonsFlag = selected.showDispositionReasonsFlag;
                        $scope.appealDispositionValue = selected.dispositionValue;

                        $scope.objectInfo.disposition = selected.disposition;
                        $scope.objectInfo.otherReason = selected.otherReason;
                        $scope.objectInfo.dispositionReasons = selected.dispositionReasons;

                        saveCase();
                    }
                });
            }

            $scope.openAddOtherReasonInAppeal = function () {
                var params = {
                    dispositionReasons: $scope.objectInfo.dispositionReasons,
                    otherReason: $scope.objectInfo.otherReason
                };

                var modalInstance = $modal.open({
                    templateUrl: "modules/cases/views/components/add-appeal-other-reason-modal.client.view.html",
                    controller: 'Cases.AddAppealOtherReasonModalController',
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (selected) {
                    if (!Util.isEmpty(selected)) {
                        if (selected.isAppealOtherReasonDisabled) {
                            $scope.isAppealOtherReasonDisabled = selected.isAppealOtherReasonDisabled;
                        } else {
                            $scope.objectInfo.otherReason = selected.otherReason;
                        }
                    }
                });
            };

            $scope.$bus.subscribe('ACTION_SAVE_CASE', function (data) {
                if (data != null && typeof data.deleteDenialLetter !== 'undefined' && data.deleteDenialLetter) {
                    removeDenialLetter();
                }
                saveCase(data);
            });

            function removeDenialLetter() {
                EcmService.findFileByContainerAndFileType({
                    containerId: $scope.objectInfo.container.id,
                    fileType: 'Denial Letter'
                }).$promise.then(function (fileInfo) {
                    EcmService.deleteFile({
                        fileId: fileInfo.fileId
                    })
                });
            }

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
