'use strict';

angular.module('cases').controller('Cases.ActionsFooterController',
    ['$q', '$scope', '$state', '$stateParams', 'Case.InfoService', 'Helper.ObjectBrowserService', 'QueuesService', '$modal', 'Object.NoteService', 'Admin.FoiaConfigService',
        function ($q, $scope, $state, $stateParams, CaseInfoService, HelperObjectBrowserService, QueuesService, $modal, NotesService, AdminFoiaConfigService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "actionFooter",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            AdminFoiaConfigService.getFoiaConfig().then(function (response) {
                $scope.limitedDeliveryToSpecificPageCountEnabled = response.data.limitedDeliveryToSpecificPageCountEnabled;
                $scope.limitedDeliveryToSpecificPageCount = response.data.limitedDeliveryToSpecificPageCount;
                $scope.provideReasonToHoldRequestEnabled = response.data.provideReasonToHoldRequestEnabled;
            }, function (err) {
                MessageService.errorAction();
            });

            $scope.loading = false;
            $scope.loadingIcon = "fa fa-check";

            $scope.availableQueues = [];
            var onObjectInfoRetrieved = function (objectInfo) {
                QueuesService.queryNextPossibleQueues(objectInfo.id).then(function (data) {
                    setQueueButtons(data);
                });
            };

            function setupNextQueue(name, deferred) {
                var nextQueue = name;
                if (name === 'Complete') {
                    nextQueue = $scope.defaultNextQueue;
                } else if (name === 'Return') {
                    nextQueue = $scope.defaultReturnQueue;
                } else if (name === 'Deny') {
                    nextQueue = $scope.defaultDenyQueue;
                }
                QueuesService.nextQueue($scope.objectInfo.id, nextQueue, name).then(function (data) {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";

                    if (data.success) {
                        $scope.$emit("report-object-updated", data.caseFile);
                    } else {
                        $scope.loading = false;
                        $scope.loadingIcon = "fa fa-check";
                        $scope.showErrorDialog(data.errors[0]);
                    }
                });
            }

            function displayLimitedReleaseModal() {
                return $scope.defaultNextQueue === "Release"
                    && !$scope.objectInfo.deniedFlag
                    && $scope.limitedDeliveryToSpecificPageCountEnabled;
            }

            $scope.onClickNextQueue = function (name, isRequestFormModified) {
                $scope.loading = true;
                $scope.loadingIcon = "fa fa-circle-o-notch fa-spin";
                $scope.nameButton = name;

                var nextQueue = name;
                var deferred = $q.defer();

                disableWorkflowControls(deferred.promise);

                if (name === 'Return') {
                    openReturnReasonModal(deferred);
                } else if (name === 'Delete') {
                    openDeleteCommentModal(deferred);
                } else if (name === 'Hold') {
                    if ($scope.provideReasonToHoldRequestEnabled) {
                        openHoldReasonModal(deferred, $scope.objectInfo.tollingFlag);
                    } else {
                        deferred.resolve();
                    }
                } else if (name === 'Complete' && displayLimitedReleaseModal()) {
                    openLimitedPageReleaseModal(deferred);
                } else if (name === 'Complete' && $scope.objectInfo.queue.name === 'Fulfill' && $scope.objectInfo.requestType === 'New Request' && $scope.objectInfo.disposition == null) {
                    openDispositionCategoryModal(deferred);
                } else if (name === 'Complete' && ($scope.objectInfo.queue.name === 'Fulfill' || $scope.defaultNextQueue === 'Release') && $scope.objectInfo.requestType === 'Appeal') {
                    if ($scope.objectInfo.disposition == null) {
                        openAppealDispositionCategoryModal(deferred);
                    } else {
                        deferred.resolve();
                    }
                } else if (name === 'Deny' && ($scope.objectInfo.queue.name === 'Intake' || $scope.objectInfo.queue.name === 'Fulfill')) {
                    openDenyDispositionCategoryModal(deferred);
                } else {
                    deferred.resolve();
                }

                deferred.promise.then(function () {
                    saveCase().then(function () {
                        setupNextQueue(name, deferred);
                    }, function () {
                        $scope.loading = false;
                        $scope.loadingIcon = "fa fa-check";
                    });
                });
            };

            $scope.$bus.subscribe('OPEN_REQUEST_DISPOSITION_MODAL', function (deferred) {
                openDispositionCategoryModal(deferred);
            });

            $scope.$bus.subscribe('OPEN_APPEAL_DISPOSITION_MODAL', function (deferred) {
                openAppealDispositionCategoryModal(deferred);
            });

            $scope.showErrorDialog = function (error) {
                $modal.open({
                    animation: true,
                    templateUrl: 'modules/cases/views/components/case-actions-error-dialog.client.view.html',
                    controller: 'Cases.ActionsErrorDialogController',
                    backdrop: 'static',
                    resolve: {
                        errorMessage: function () {
                            return error;
                        }
                    }
                });
            };

            function saveCase() {
                var saveCasePromise = $q.defer();
                $scope.$bus.publish('ACTION_SAVE_CASE', {
                    returnAction: "CASE_SAVED",
                    requestDisposition: $scope.requestDispositionCategory,
                    dispositionValue: $scope.dispositionValue,
                    requestOtherReason: $scope.requestOtherReason,
                    dispositionReasons: $scope.dispositionReasons,
                    deleteDenialLetter: $scope.deleteDenialLetter
                });
                var subscription = $scope.$bus.subscribe('CASE_SAVED', function (objectInfo) {
                    //after case is saved we are going to get new buttons
                    QueuesService.queryNextPossibleQueues(objectInfo.id).then(function (data) {
                        setQueueButtons(data);
                        saveCasePromise.resolve();
                    });
                });

                //when case file is saved and we get next possible queue, unsubscribe
                saveCasePromise.promise.then(function () {
                    $scope.$bus.unsubscribe(subscription);
                });

                return saveCasePromise.promise;
            }

            // Controls workflow buttons
            $scope.requestInProgress = false;

            /**
             * Disable workflow control and enable them when prome is resolved
             * @param promise
             */
            function disableWorkflowControls(promise) {
                $scope.requestInProgress = true;
                promise['finally'](function () {
                    $scope.requestInProgress = false;
                });
            }

            function openDeleteCommentModal(deferred) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/delete-comment-modal.client.view.html',
                    controller: 'Cases.DeleteCommentModalController',
                    size: 'md',
                    backdrop: 'static'
                });

                modalInstance.result.then(function (deleteComment) {
                    //save note
                    NotesService.saveNote({
                        note: deleteComment,
                        parentId: $stateParams['id'],
                        parentType: 'CASE_FILE',
                        type: 'DELETE_COMMENT'
                    }).then(function (addedNote) {
                        // Note saved
                        deferred.resolve();
                    });
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function openReturnReasonModal(deferred) {
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/return-reason-modal.client.view.html',
                    controller: 'Cases.ReturnReasonModalController',
                    size: 'md',
                    backdrop: 'static'
                });

                modalInstance.result.then(function (returnReason) {
                    //save note
                    NotesService.saveNote({
                        note: returnReason,
                        parentId: $stateParams['id'],
                        parentType: 'CASE_FILE',
                        type: 'RETURN_REASON'
                    }).then(function (addedNote) {
                        // Note saved
                        $scope.requestDispositionCategory = null;
                        if ($scope.objectInfo.requestType !== 'Appeal') {
                            if ($scope.objectInfo.deniedFlag && $scope.objectInfo.queue.name === 'Approve') {
                                $scope.objectInfo.status = 'Perfected';
                                $scope.deleteDenialLetter = true;
                            }
                            $scope.requestOtherReason = null;
                            $scope.objectInfo.deniedFlag = false;
                        }
                        deferred.resolve();
                    });
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }


            function openHoldReasonModal(deferred, tollingFlag) {
                var params = {};
                params.tollingFlag = tollingFlag;

                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/hold-reason-modal.client.view.html',
                    controller: 'Cases.HoldReasonModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.objectInfo.status = data.status;
                    if (data.isSelectedTolling) {
                        $scope.objectInfo.tollingFlag = true;
                    }
                    if (data.holdReason) {
                        //save note
                        NotesService.saveNote({
                            note: data.holdReason,
                            parentId: $stateParams['id'],
                            parentType: 'CASE_FILE',
                            type: 'HOLD_REASON'
                        }).then(function (addedNote) {
                            // Note saved
                            deferred.resolve();
                        });
                    } else {
                        deferred.resolve();
                    }
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function openLimitedPageReleaseModal(deferred) {
                var params = {
                    disposition: $scope.objectInfo.disposition,
                    dispositionReasons: $scope.objectInfo.dispositionReasons,
                    otherReason: $scope.objectInfo.otherReason,
                    caseId: $scope.objectInfo.id,
                    queue: $scope.objectInfo.queue.name,
                    requestType: $scope.objectInfo.requestType
                };
                params.pageCount = $scope.limitedDeliveryToSpecificPageCount;

                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/limited-release-modal.client.view.html',
                    controller: 'Cases.LimitedReleaseModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.objectInfo.limitedDeliveryFlag = data.limitedDeliveryFlag;
                    $scope.requestDispositionCategory = data.requestDispositionCategory;
                    $scope.dispositionValue = data.dispositionValue;
                    $scope.requestOtherReason = data.requestOtherReason;
                    $scope.dispositionReasons = data.dispositionReasons;
                    deferred.resolve();
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function openDispositionCategoryModal(deferred) {
                var params = {};
                params.objectId = $scope.objectInfo.id;
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/request-disposition-categories-modal.client.view.html',
                    controller: 'Cases.RequestDispositionCategoriesModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.requestDispositionCategory = data.requestDispositionCategory;
                    $scope.objectInfo.disposition = data.requestDispositionCategory;
                    $scope.dispositionValue = data.dispositionValue;
                    deferred.resolve();
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                    if($scope.objectInfo.dispositionClosedDate != null){
                        $scope.objectInfo.dispositionClosedDate = null;
                        $scope.$emit("report-object-updated", {});
                    }
                });
            }

            function openAppealDispositionCategoryModal(deferred) {
                var params = {
                    disposition: $scope.objectInfo.disposition,
                    dispositionReasons: $scope.objectInfo.dispositionReasons,
                    otherReason: $scope.objectInfo.otherReason,
                    caseId: $scope.objectInfo.id,
                    queue: $scope.objectInfo.queue.name,
                    isDispositionRequired: true
                };

                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: "modules/cases/views/components/add-appeal-disposition-category-modal.client.view.html",
                    controller: 'Cases.AddAppealDispositionCategoriesModalController',
                    size: 'md',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.requestDispositionCategory = data.disposition;
                    $scope.objectInfo.disposition = data.disposition;
                    $scope.objectInfo.otherReason = data.otherReason;
                    $scope.objectInfo.dispositionReasons = data.dispositionReasons;
                    $scope.dispositionValue = data.dispositionValue;
                    $scope.requestOtherReason = data.otherReason;
                    $scope.dispositionReasons = data.dispositionReasons;

                    deferred.resolve();
                }, function () {
                    deferred.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                    if($scope.objectInfo.dispositionClosedDate != null){
                        $scope.objectInfo.dispositionClosedDate = null;
                        $scope.$emit("report-object-updated", {});
                    }
                });
            }

            function openDenyDispositionCategoryModal(deffered) {
                var params = {};
                params.objectId = $scope.objectInfo.id;
                var modalInstance = $modal.open({
                    animation: $scope.animationsEnabled,
                    templateUrl: 'modules/cases/views/components/request-deny-disposition-categories-modal.client.view.html',
                    controller: 'Cases.RequestDenyDispositionCategoriesModalController',
                    size: 'lg',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function (data) {
                    $scope.requestDispositionCategory = data.requestDispositionCategory;
                    $scope.dispositionValue = data.dispositionValue;
                    $scope.requestOtherReason = data.requestOtherReason;
                    deffered.resolve();
                }, function () {
                    deffered.reject();
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            }

            function setQueueButtons(data) {
                var availableQueues = data.nextPossibleQueues;
                var defaultNextQueue = data.defaultNextQueue;
                var defaultReturnQueue = data.defaultReturnQueue;
                var defaultDenyQueue = data.defaultDenyQueue;

                if (defaultNextQueue || defaultReturnQueue) {
                    //if there is default next or return queue, then remove it from the list
                    //and add Complete, and Return queue aliases into list
                    _.remove(availableQueues, function (currentObject) {
                        return currentObject === defaultNextQueue || currentObject === defaultReturnQueue || currentObject === defaultDenyQueue;
                    });
                    if (defaultDenyQueue) {
                        availableQueues.unshift("Deny");
                    }
                    if (defaultReturnQueue) {
                        availableQueues.unshift("Return");
                    }
                    if (defaultNextQueue) {
                        availableQueues.push("Complete");
                    }
                }
                availableQueues = availableQueues.map(function (item) {
                    var tmpObj = {};
                    tmpObj.name = item;
                    if (item != 'Complete') {
                        tmpObj.disabled = true;
                    }
                    return tmpObj;
                });
                $scope.availableQueues = availableQueues;
                $scope.defaultNextQueue = defaultNextQueue;
                $scope.defaultReturnQueue = defaultReturnQueue;
                $scope.defaultDenyQueue = defaultDenyQueue;
            }
        }]);
angular.module('cases').controller('Cases.ActionsErrorDialogController', ['$scope', '$modalInstance', 'errorMessage', function ($scope, $modalInstance, errorMessage) {
    $scope.errorMessage = errorMessage;
    $scope.onClickOk = function () {
        $modalInstance.dismiss('cancel');
    };
}]);
