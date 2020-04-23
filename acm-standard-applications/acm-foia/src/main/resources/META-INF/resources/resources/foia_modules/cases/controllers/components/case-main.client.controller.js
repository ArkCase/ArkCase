'use strict';

angular.module('cases').controller(
    'Cases.MainController',
    ['$scope', '$state', '$stateParams', '$translate', '$rootScope', '$modal', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'UtilService', 'Util.DateService', 'Object.LookupService', 'LookupService', 'DueDate.Service', 'Admin.HolidayService', 'Admin.FoiaConfigService', 'Admin.ObjectTitleConfigurationService',
        function ($scope, $state, $stateParams, $translate, $rootScope, $modal, CaseInfoService, HelperObjectBrowserService, ConfigService, Util, UtilDateService, ObjectLookupService, LookupService, DueDateService, AdminHolidayService, AdminFoiaConfigService, AdminObjectTitleConfigurationService) {

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
                AdminObjectTitleConfigurationService.getObjectTitleConfiguration().then(function (configTitle){
                    if(!Util.isEmpty(configTitle)) {
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
                $scope.populateDispositionSubTypes($scope.objectInfo.requestType);
                $scope.originalDueDate = objectInfo.dueDate;
                $scope.enableDispositionClosedDate = objectInfo.dispositionClosedDate == null;

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
                });
            };
            function populateDeniedDispositionCategories() {
                ObjectLookupService.getLookupByLookupName('requestDispositionSubType').then(function (requestDispositionSubType) { 
                    $scope.dispositionDeniedCategories =  requestDispositionSubType;     
                });
            };
            function populateOtherReasons() {
                ObjectLookupService.getLookupByLookupName('requestOtherReason').then(function (requestOtherReasons) {
                    $scope.otherReasons = requestOtherReasons;
                    if($scope.objectInfo.otherReason) {
                        var found = _.find(requestOtherReasons, {
                           key: $scope.objectInfo.otherReason  
                        });
                        if(found) {
                            $scope.isCustomReason = false;
                        } else {
                            $scope.isCustomReason = true;
                        }
                    }
                });
            };

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

            $scope.isDisabled = true;

            $scope.populateDispositionSubTypes = function (requestType) {
                if (requestType == "Appeal") {
                    ObjectLookupService.getAppealDispositionReasons().then(function (appealDispositionReasons) {
                        $scope.appealDispositionReasons = appealDispositionReasons;
                    });
                } else if (requestType == "New Request") {
                    ObjectLookupService.getRequestDispositionSubTypes().then(function (requestDispositionSubType) {
                        $scope.dispositionSubTypes = requestDispositionSubType;
                    });
                } else {
                    $scope.dispositionSubTypes = "";
                }
            };

            $scope.onAppealReasonSelected = function (reason) {
                if (reason) {
                    if (!_.includes($scope.objectInfo.dispositionReasons, reason)) {
                        $scope.objectInfo.dispositionReasons.push(reason);
                    } else {
                        _.forEach($scope.objectInfo.dispositionReasons, function (disReason, i) {
                            if (Util.compare(disReason, reason)) {
                                $scope.objectInfo.dispositionReasons.splice(i, 1);
                                return false;
                            }
                        });
                    }
                }
            };

            $scope.isAppealReasonChecked = function (reason) {
                return _.includes($scope.objectInfo.dispositionReasons, reason);
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

            $scope.openAddAppealDispositionCategory = function () {
                var params = {
                    dispositionTypes: $scope.dispositionTypes,
                    appealDispositionReasons: $scope.appealDispositionReasons,
                    disposition: $scope.objectInfo.disposition,
                    dispositionReasons: $scope.objectInfo.dispositionReasons,
                    otherReason: $scope.objectInfo.otherReason
                };


                var modalInstance = $modal.open({
                    templateUrl: "modules/cases/views/components/add-appeal-disposition-category-modal.client.view.html",
                    controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                        $scope.modalInstance = $modalInstance;
                        $scope.dispositionTypesLookup = params.dispositionTypes;
                        $scope.appealDispositionReasonsLookup = params.appealDispositionReasons;
                        $scope.modalInfo = {};

                        ObjectLookupService.getAppealOtherReasons().then(function (appealOtherReasons) {
                            $scope.otherReasonsLookup = appealOtherReasons;

                            if (!Util.isEmpty(params.otherReason)) {
                                $scope.modalInfo.otherReason = params.otherReason;
                            } else {
                                $scope.modalInfo.otherReason = $scope.otherReasonsLookup[0].key;
                            }
                        });

                        if (!Util.isEmpty(params.disposition)) {
                            $scope.modalInfo.disposition = params.disposition;
                        } else {
                            $scope.modalInfo.disposition = $scope.dispositionTypesLookup[0].key;
                        }

                        if (!Util.isArrayEmpty(params.dispositionReasons)) {
                            $scope.modalInfo.dispositionReasons = params.dispositionReasons;
                        }

                        $scope.onReasonSelected = function (reason) {
                            if (reason) {
                                if (!_.includes($scope.modalInfo.dispositionReasons, reason)) {
                                    $scope.modalInfo.dispositionReasons.push(reason);
                                } else {
                                    _.forEach($scope.modalInfo.dispositionReasons, function (disReason, i) {
                                        if (Util.compare(disReason, reason)) {
                                            $scope.modalInfo.dispositionReasons.splice(i, 1);
                                            return false;
                                        }
                                    });
                                }
                            }
                        };

                        $scope.isChecked = function (reason) {
                            return _.includes($scope.modalInfo.dispositionReasons, reason);
                        };

                        $scope.onClickOk = function () {
                            $modalInstance.close({
                                disposition: $scope.modalInfo.disposition,
                                otherReason: $scope.modalInfo.otherReason,
                                dispositionReasons: $scope.modalInfo.dispositionReasons
                            });
                        };

                        $scope.onClickCancel = function () {
                            $modalInstance.dismiss('Cancel');
                        };
                    }],
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (value) {
                    if (!Util.isEmpty(value)) {
                        $scope.objectInfo.disposition = value.disposition;
                        $scope.objectInfo.otherReason = value.otherReason;
                        $scope.objectInfo.dispositionReasons = value.dispositionReasons;
                    }
                });
            };

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
