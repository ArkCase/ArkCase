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
                    if ($scope.objectInfo.queue.name === 'Hold') {
                        nextQueue = $scope.objectInfo.previousQueue.name;
                    } else {
                        nextQueue = $scope.defaultNextQueue;   
                    }
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

        $scope.onClickNextQueue = function(name, isRequestFormModified) {
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
                    $scope.objectInfo.status = 'Hold';
                    deferred.resolve();
                }
            } else if (name === 'Complete' && $scope.defaultNextQueue === "Release" && $scope.limitedDeliveryToSpecificPageCountEnabled) {
                openLimitedPageReleaseModal(deferred);
            } else {
                deferred.resolve();
            }

            deferred.promise.then(function() {
                saveCase().then(function () {
                    setupNextQueue(name, deferred);
                }, function() {
                    $scope.loading = false;
                    $scope.loadingIcon = "fa fa-check";
                });
            });
        };

        $scope.showErrorDialog = function(error) {
            $modal.open({
                animation: true,
                templateUrl: 'modules/cases/views/components/case-actions-error-dialog.client.view.html',
                controller: 'Cases.ActionsErrorDialogController',
                backdrop: 'static',
                resolve: {
                    errorMessage: function() {
                        return error;
                    }
                }
            });
        };

        function saveCase() {
            var saveCasePromise = $q.defer();
            $scope.$bus.publish('ACTION_SAVE_CASE', {
                returnAction: "CASE_SAVED"
            });
            var subscription = $scope.$bus.subscribe('CASE_SAVED', function(objectInfo) {
                //after case is saved we are going to get new buttons
                QueuesService.queryNextPossibleQueues(objectInfo.id).then(function(data) {
                    setQueueButtons(data);
                    saveCasePromise.resolve();
                });
            });

            //when case file is saved and we get next possible queue, unsubscribe
            saveCasePromise.promise.then(function() {
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
            promise['finally'](function() {
                $scope.requestInProgress = false;
            });
        }

        function openDeleteCommentModal(deferred) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/delete-comment-modal.client.view.html',
                controller: 'Cases.DeleteCommentModalController',
                size: 'lg',
                backdrop: 'static'
            });

            modalInstance.result.then(function(deleteComment) {
                //save note
                NotesService.saveNote({
                    note: deleteComment,
                    parentId: $stateParams['id'],
                    parentType: 'CASE_FILE',
                    type: 'DELETE_COMMENT'
                }).then(function(addedNote) {
                    // Note saved
                    deferred.resolve();
                });
            }, function() {
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
                size: 'lg',
                backdrop: 'static'
            });

            modalInstance.result.then(function(returnReason) {
                //save note
                NotesService.saveNote({
                    note: returnReason,
                    parentId: $stateParams['id'],
                    parentType: 'CASE_FILE',
                    type: 'RETURN_REASON'
                }).then(function(addedNote) {
                    // Note saved
                    deferred.resolve();
                });
            }, function() {
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
                    params: function() {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function(data) {
                $scope.objectInfo.status = data.status;
                if (data.isSelectedTolling) {
                  $scope.objectInfo.tollingFlag = true;
                }
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
            }, function () {
                deferred.reject();
                $scope.loading = false;
                $scope.loadingIcon = "fa fa-check";
            });
        }

            function openLimitedPageReleaseModal(deferred) {
                var params = {};
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

                modalInstance.result.then(function (limitedDeliveryFlag) {
                    $scope.objectInfo.limitedDeliveryFlag = limitedDeliveryFlag;
                    deferred.resolve();
                }, function () {
                    deferred.reject();
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
                if ($scope.objectInfo.queue.name === 'Hold') {
                    availableQueues = ["Complete"];
                }
                availableQueues = availableQueues.map(function (item) {
                    var tmpObj = {};
                    tmpObj.name = item;
                    if (item != 'Complete'){
                        tmpObj.disabled = true;
                    }
                    return tmpObj;
                });
                $scope.availableQueues = availableQueues;
                $scope.defaultNextQueue = defaultNextQueue;
                $scope.defaultReturnQueue = defaultReturnQueue;
                $scope.defaultDenyQueue = defaultDenyQueue;
        }
    } ]);
angular.module('cases').controller('Cases.ActionsErrorDialogController', [ '$scope', '$modalInstance', 'errorMessage', function($scope, $modalInstance, errorMessage) {
    $scope.errorMessage = errorMessage;
    $scope.onClickOk = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);
