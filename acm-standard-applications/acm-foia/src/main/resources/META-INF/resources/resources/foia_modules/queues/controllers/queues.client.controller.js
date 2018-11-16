'use strict';

angular.module('queues').controller('QueuesController',
        [ '$scope', '$timeout', '$state', '$window', 'ConfigService', 'Queues.QueuesConstants', 'Queues.QueuesService', 'Authentication', '$q', 'MessageService', '$translate', function($scope, $timeout, $state, $window, ConfigService, QueuesConstants, QueuesService, Authentication, $q, MessageService, $translate) {
            $scope.config = ConfigService.getModule({
                moduleId: 'queues'
            });
            $scope.$on('req-component-config', onConfigRequest);
            $scope.$on('req-select-queue', onSelectQueue);
            $scope.$on('req-select-requests', onSelectRequests);

            $scope.selectedRequests = [];
            $scope.printUrl = '';

            // Methods
            $scope.downloadRequests = downloadRequests;
            $scope.printRequests = printRequests;
            $scope.startWorking = startWorking;
            $scope.completeRequests = completeRequests;

            $scope.userInfo = null;

            Authentication.queryUserInfo().then(function(userInfo) {
                $scope.userInfo = userInfo;
            });

            /**
             * Handles 'req-select-queue' event
             * @param e
             * @param selectedQueue
             */
            function onSelectQueue(e, selectedQueue) {
                $state.go('queues.queue', {
                    name: selectedQueue.name
                }, {
                    notify: false
                });
                $scope.selectedRequests = [];
                $scope.selectedQueue = selectedQueue;
                $scope.$broadcast('queue-selected', selectedQueue);
            }

            /**
             * find a request to work on
             * Open the first unassigned request in queue, and then assign that request to the current user.
             */
            function startWorking() {
                // Changing a route inside a promise, triggers browser's popup blocker.
                // We first open the new tab on the user action and set the tab's URL in the promise
                var newTabWindow = $window.open('', '_blank');

                var requestPromise = QueuesService.startWorking($scope.selectedQueue.id);
                requestPromise.then(function(request) {
                    var url = $state.href('request-info', {id: request.id}, {absolute: true})
                    newTabWindow.location.href = url;
                    $scope.$emit("report-object-updated", request);
                }, function() {
                    //nothing found, nothing to do
                    newTabWindow.close();
                    MessageService.info($translate.instant("queues.startWorking.noRequestToAssign"));
                });
            }

            /**
             * Prevent new browser window opening for 'Print Requests' action if no selected requests.
             * @param $event
             */
            function printRequests($event) {
                if ($scope.selectedRequests.length == 0) {
                    $event.preventDefault();
                }
            }

            /**
             *
             */
            function completeRequests() {
                if ($scope.selectedRequests.length > 0) {
                    var requestsIds = _.pluck($scope.selectedRequests, 'request_id_lcs');
                    QueuesService.completeRequests(requestsIds).then(function() {
                        // TODO Fix another server delay issue. Can't refresh request immediately, only after delay
                        $timeout(function() {
                            $scope.$broadcast('update-requests-list');
                        }, 1000);
                    })
                }
            }

            /**
             * Handle select requests event
             * @param e
             * @param requests
             */
            function onSelectRequests(e, requests) {
                $scope.selectedRequests = requests;
                updatePrintUrl(requests);
            }

            /**
             * Handles 'req-component-config' event
             * @param e
             * @param componentId
             */
            function onConfigRequest(e, componentId) {
                $scope.config.$promise.then(function(config) {
                    var componentConfig = _.find(config.components, {
                        id: componentId
                    });
                    $scope.$broadcast('component-config', componentId, componentConfig);
                });
            }

            /**
             * Perform multiple requests download
             * @param requests
             */
            function downloadRequests(requests) {
                var requestsIds = _.pluck(requests, 'request_id_lcs');
                QueuesService.queryDownloadRequestsInfo({
                    requestsIds: requestsIds
                }).then(function(downloadInfo) {
                    QueuesService.downloadRequests(downloadInfo)
                })
            }

            /**
             * Open Selected request batched into one pdf file in new window
             * @param requests
             */
            function updatePrintUrl(requests) {
                var requestsIds = _.pluck(requests, 'request_id_lcs');
                $scope.printUrl = QueuesService.getMergedRequestsUrl(requestsIds);
            }
        }

        ]);