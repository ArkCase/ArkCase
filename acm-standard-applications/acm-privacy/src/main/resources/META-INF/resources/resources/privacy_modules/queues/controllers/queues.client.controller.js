'use strict';

angular.module('queues').controller(
        'QueuesController',
    ['$scope', '$stateParams', '$rootScope', '$timeout', '$state', '$window', '$modal', 'ConfigService', 'Queues.QueuesService', 'Authentication', '$q', 'MessageService', '$translate', 'LookupService', 'Admin.PrivacyConfigService', 'Object.ModelService', 'UtilService', 'Case.InfoService',
                'Util.DateService', 'EcmService',
        function ($scope, $stateParams, $rootScope, $timeout, $state, $window, $modal, ConfigService, QueuesService, Authentication, $q, MessageService, $translate, LookupService, AdminPrivacyConfigService, ObjectModelService, Util, CaseInfoService, UtilDateService, EcmService) {

                    $scope.config = ConfigService.getModule({
                        moduleId: 'queues'
                    });

                    $scope.$bus.subscribe('object.changed/' + $stateParams.type + '/' + $stateParams.id, function(data) {
                        $scope.$emit('report-object-refreshed', $stateParams.id);
                    });
                    $scope.$on('req-component-config', onConfigRequest);
                    $scope.$on('req-select-queue', onSelectQueue);
                    $scope.$on('req-select-requests', onSelectRequests);

                    $scope.selectedRequests = [];

                    //caseFile objects retrieved from database user/group assignment.
                    $scope.requestsDataForUpdate = [];
                    $scope.printUrl = '';

                    // Methods
                    $scope.downloadRequests = downloadRequests;
                    $scope.printRequests = printRequests;
                    $scope.startWorking = startWorking;
                    $scope.assignSelectedQueues = assignSelectedQueues;
                    $scope.completeRequests = completeRequests;

                    $scope.userInfo = null;

                    function assignSelectedQueues() {
                        if ($scope.selectedRequests.length === 0) {
                            return MessageService.info($translate.instant("queues.selectRequest.selectMinOneRequest"));
                        } else {
                            $scope.userOrGroupSearch();
                        }
                    }

                    LookupService.getUserFullNames().then(function(userFullNames) {
                        $scope.userFullNames = userFullNames;
                        return userFullNames;
                    });

                    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
                        $scope.userOrGroupSearchConfig = _.find(moduleConfig.components, {
                            id: "userOrGroupSearch"
                        });
                    });

                    $scope.updateAssignee = function() {
                        _.forEach($scope.requestsDataForUpdate, function(item) {
                            ObjectModelService.setAssignee(item, $scope.assignee);
                        });
                    };

                    $scope.updateOwningGroup = function() {
                        _.forEach($scope.requestsDataForUpdate, function(item) {
                            ObjectModelService.setGroup(item, $scope.owningGroup);
                        });
                    };

                    /**
                     * Persists the updated casefile metadata to the ArkCase database
                     */
                    function saveCase() {
                        var promises = [];
                        _.forEach($scope.requestsDataForUpdate, function(request) {
                            if (CaseInfoService.validateCaseInfo(request)) {
                                request.recordSearchDateFrom = UtilDateService.dateToIsoDateTime(request.recordSearchDateFrom);
                                request.recordSearchDateTo = UtilDateService.dateToIsoDateTime(request.recordSearchDateTo);
                                var objectInfo = Util.omitNg(request);
                                promises.push(CaseInfoService.saveSubjectAccessRequestInfoMassAssigment(objectInfo));
                            }
                        });
                        $q.all(promises).then(function(caseInfo) {
                            MessageService.succsessAction();
                            $timeout(function() {
                                $rootScope.$broadcast('update-requests-list');
                            }, 1000);
                            $scope.$emit("report-object-updated", caseInfo);
                            return;
                        }, function(error) {
                            MessageService.errorAction();
                            $scope.$emit("report-object-update-failed", error);
                            return;
                        });
                        return;
                    }

                    $scope.saveCase = function() {
                        saveCase();
                    };

                    $scope.userOrGroupSearch = function() {
                        var assigneUserName = _.find($scope.userFullNames, function(user) {
                            return user.id === $scope.assignee
                        });
                        var params = {
                            owningGroup: $scope.owningGroup,
                            assignee: assigneUserName
                        };
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'modules/common/views/user-group-picker-modal.client.view.html',
                            controller: 'Common.UserGroupPickerController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $filter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetFilter;
                                },
                                $extraFilter: function() {
                                    return $scope.userOrGroupSearchConfig.userOrGroupSearchFilters.userOrGroupFacetExtraFilter;
                                },
                                $config: function() {
                                    return $scope.userOrGroupSearchConfig;
                                },
                                $params: function() {
                                    return params;
                                }
                            }

                        });

                        modalInstance.result.then(function(selection) {
                            var promises = [];
                            _.forEach($scope.selectedRequests, function(item) {
                                promises.push(CaseInfoService.getCaseInfo(parseInt(item.request_id_lcs)));
                            });
                            $q.all(promises).then(function(payload) {
                                $scope.requestsDataForUpdate = payload;
                                if (selection && !Util.isEmpty($scope.requestsDataForUpdate)) {
                                    var selectedObjectType = selection.masterSelectedItem.object_type_s;
                                    if (selectedObjectType === 'USER') { // Selected user
                                        var selectedUser = selection.masterSelectedItem;
                                        var selectedGroup = selection.detailSelectedItems;

                                        $scope.assignee = selectedUser.object_id_s;
                                        $scope.updateAssignee();
                                        if (selectedGroup) {
                                            $scope.owningGroup = selectedGroup.object_id_s;
                                            $scope.updateOwningGroup();
                                            $scope.saveCase();
                                            $scope.selectedRequests = [];
                                        } else {
                                            $scope.saveCase();
                                            $scope.selectedRequests = [];
                                        }
                                        return;
                                    } else if (selectedObjectType === 'GROUP') { // Selected group
                                        var selectedUser = selection.detailSelectedItems;
                                        var selectedGroup = selection.masterSelectedItem;
                                        $scope.owningGroup = selectedGroup.object_id_s;
                                        $scope.updateOwningGroup();
                                        if (selectedUser) {
                                            $scope.assignee = selectedUser.object_id_s;
                                            $scope.updateAssignee();
                                            $scope.saveCase();
                                            $scope.selectedRequests = [];
                                        } else {
                                            $scope.saveCase();
                                            $scope.selectedRequests = [];
                                        }

                                        return;
                                    }
                                }
                            });
                        }, function() {
                            // Cancel button was clicked.
                            return [];
                        });
                    };

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

                    function getFileId(repositoryInfo) {
                        return EcmService.findFileByContainerAndFileType({
                            containerId: repositoryInfo,
                            fileType: 'Request Form'
                        });
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
                            getFileId(request.container.id).$promise.then(function(fileInfo) {
                                var url = $state.href('request-info', {
                                    id: request.id,
                                    fileId: fileInfo.fileId
                                }, {
                                    absolute: true
                                });
                                newTabWindow.location.href = url;
                                $scope.$emit("report-object-updated", request);
                            });
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