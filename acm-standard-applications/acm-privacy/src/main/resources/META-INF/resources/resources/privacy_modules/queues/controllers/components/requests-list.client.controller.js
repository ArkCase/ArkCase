'use strict';

angular.module('queues').controller(
        'Queues.RequestsListController',
        [ '$scope', '$state', '$stateParams', '$timeout', 'uiGridConstants', 'Queues.QueuesService', 'Admin.QueuesTimeToCompleteService', 'Task.AlertsService', 'DueDate.Service', 'Admin.HolidayService', '$filter', '$q', '$translate', 'MessageService', 'Requests.RequestsService', 'EcmService',
                '$window', function($scope, $state, $stateParams, $timeout, uiGridConstants, QueuesService, AdminQueuesTimeToCompleteService, TaskAlertsService, DueDateService, AdminHolidayService, $filter, $q, $translate, MessageService, GenericRequestsService, EcmService, $window) {
                    $scope.$on('component-config', applyConfig);
                    $scope.$on('queue-selected', queueSelected);
                    $scope.$on('update-requests-list', update);
                    $scope.$emit('req-component-config', 'requests');

                    $scope.rowClick = rowClick;

                    $scope.config = null;
                    $scope.selectedQueue = null;

                    // Timeout id. Used to prevent too frequent filter requests
                    var filterTimeout = null;

                    // Be sure that filterTimeout is canceled on destroy
                    $scope.$on('$destroy', function() {
                        if (angular.isDefined(filterTimeout)) {
                            $timeout.cancel(filterTimeout);
                        }
                    });

                    var paginationOptions = {
                        pageNumber: 1,
                        pageSize: 25,
                        sortBy: 'create_date_tdt',
                        sortDir: 'desc',
                        filters: []
                    };

                    function queueSelected(e, newQueue) {
                        $scope.selectedQueue = newQueue;
                        paginationOptions.pageNumber = 1;
                        paginationOptions.filters = [];
                        $scope.gridOptions.data = [];
                        if ($scope.gridApi) {
                            $scope.gridApi.grid.clearAllFilters();
                        }

                        //Custom grid column definitions go here (commented code bellow can be used as an example)

                        // if (newQueue.id == QueuesConstants.) {
                        //     $scope.gridOptions.columnDefs = $scope.config.columnDefsTranscribe;
                        //     $scope.gridOptions.enableSelectAll = false;
                        //     $scope.gridOptions.multiSelect = false;
                        // } else if (newQueue.id == QueuesConstants.FULFILL_ORDER) {
                        //     $scope.gridOptions.columnDefs = $scope.config.columnDefsFulfill;
                        //     $scope.gridOptions.enableSelectAll = false;
                        //     $scope.gridOptions.multiSelect = false;
                        // } else if (newQueue.id == QueuesConstants.DISTRIBUTION) {
                        //     $scope.gridOptions.columnDefs = $scope.config.columnDefsDistribution;
                        //     $scope.gridOptions.enableSelectAll = true;
                        //     $scope.gridOptions.multiSelect = true;
                        // } else if (newQueue.id == QueuesConstants.PENDING) {
                        //     $scope.gridOptions.columnDefs = $scope.config.columnDefsPending;
                        //     $scope.gridOptions.enableSelectAll = false;
                        //     $scope.gridOptions.multiSelect = false;
                        // } else {
                        $scope.gridOptions.columnDefs = $scope.config.columnDefs;
                        $scope.gridOptions.enableSelectAll = false;
                        $scope.gridOptions.multiSelect = true;
                        // }
                        if ($scope.gridApi) {
                            $scope.gridApi.core.notifyDataChange(uiGridConstants.dataChange.OPTIONS);
                            $scope.gridApi.core.refresh();
                        }

                        getPage();
                    }

                    $scope.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableFullRowSelection: false,
                        enableSelectAll: false,
                        useExternalPagination: true,
                        useExternalSorting: true,
                        useExternalFiltering: true,
                        enableRowHeaderSelection: true,
                        modifierKeysToMultiSelect: false,
                        multiSelect: false,
                        noUnselect: false,
                        columnDefs: [],
                        onRegisterApi: function(gridApi) {
                            $scope.gridApi = gridApi;

                            gridApi.selection.on.rowSelectionChanged($scope, function(row) {
                                var selectedRows = $scope.gridApi.selection.getSelectedRows();
                                $scope.$emit('req-select-requests', selectedRows);
                            });

                            gridApi.selection.on.rowSelectionChangedBatch($scope, function(rows) {
                                var selectedRows = $scope.gridApi.selection.getSelectedRows();
                                $scope.$emit('req-select-requests', selectedRows);
                            });

                            gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
                                if (sortColumns.length == 0) {
                                    paginationOptions.sort = null;
                                } else {
                                    paginationOptions.sortBy = sortColumns[0].name;
                                    paginationOptions.sortDir = sortColumns[0].sort.direction;
                                }
                                getPage();
                            });

                            gridApi.core.on.filterChanged($scope, function() {
                                var context = this;

                                // Prevent frequent filters requests
                                if (angular.isDefined(filterTimeout)) {
                                    $timeout.cancel(filterTimeout);
                                }

                                filterTimeout = $timeout(function() {
                                    var filters = [];

                                    // Find filter
                                    _.forEach(context.grid.columns, function(column) {
                                        _.forEach(column.filters, function(columnFilter) {
                                            if (!_.isUndefined(columnFilter.term)) {
                                                var filterOption = {
                                                    column: column.name
                                                };

                                                // Parese date filter and try to create Date object.
                                                // If error happens then don't add it to the filter
                                                if (column.name == 'dueDate_tdt' || column.name == 'queue_enter_date_tdt') {
                                                    var dateObj = moment(columnFilter.term, $scope.config['dateFormat']);
                                                    if (dateObj.isValid()) {
                                                        filterOption.value = dateObj.toDate();
                                                    } else {
                                                        return;
                                                    }
                                                } else {
                                                    filterOption.value = columnFilter.term
                                                }

                                                if (columnFilter.condition == uiGridConstants.filter.GREATER_THAN_OR_EQUAL) {
                                                    filterOption.condition = 'from';
                                                } else if (columnFilter.condition == uiGridConstants.filter.LESS_THAN_OR_EQUAL) {
                                                    filterOption.condition = 'to';
                                                }
                                                filters.push(filterOption);
                                            }
                                        })
                                    });
                                    paginationOptions.filters = filters;
                                    getPage();
                                }, 500);
                            });

                            gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                                paginationOptions.pageNumber = newPage;
                                paginationOptions.pageSize = pageSize;
                                getPage();
                            });
                        }
                    };

                    /**
                     * handles row click event (not selection) and opens request info page
                     * @param row
                     */
                    function rowClick(row) {
                        $state.go('cases.main', {
                            id: row.entity.request_id_lcs
                        });
                    }

                    function applyConfig(e, componentId, config) {
                        if (componentId == 'requests') {
                            $scope.config = config;
                            $scope.gridOptions.columnDefs = config.columnDefs;
                            $scope.gridOptions.enableFiltering = config.enableFiltering;
                            $scope.gridOptions.paginationPageSizes = config.paginationPageSizes;
                            $scope.gridOptions.paginationPageSize = config.paginationPageSize;
                            paginationOptions.pageSize = config.paginationPageSize;
                        }
                    }

                    function getPage() {
                        if (!$scope.selectedQueue) {
                            return;
                        } else {
                            var queueRequestPromise = QueuesService.queryQueueRequests({
                                queueId: $scope.selectedQueue.id,
                                sortBy: paginationOptions.sortBy,
                                sortDir: paginationOptions.sortDir,
                                startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                                pageSize: paginationOptions.pageSize,
                                filters: paginationOptions.filters,
                                columns: $scope.gridOptions.columnDefs
                            });

                            var queuesConfigPromise = AdminQueuesTimeToCompleteService.getQueuesConfig();
                            var holidaysPromise = AdminHolidayService.getHolidays();

                            $q.all([ queueRequestPromise, queuesConfigPromise, holidaysPromise ]).then(function(data) {
                                var queueRequest = data[0];
                                var queuesConfig = data[1];
                                var holidaySchedule = data[2];

                                var requests = queueRequest.response.docs;

                                $scope.timeToComplete = queuesConfig.data;

                                var requestsChanged = _.map(requests, function(request) {
                                    var name = request.queue_name_s.toLowerCase();
                                    var numDays;
                                    var queuesTotal;
                                    var dueDateTotal;
                                    if (request.request_type_lcs === "Appeal") {
                                        queuesTotal = $scope.timeToComplete.appeal.totalTimeToComplete;
                                        if (request.queue_name_s === 'General Counsel') {
                                            numDays = $scope.timeToComplete.appeal.generalCounsel;

                                        } else {
                                            numDays = $scope.timeToComplete.appeal[name];
                                        }

                                    } else {
                                        queuesTotal = $scope.timeToComplete.request.totalTimeToComplete;
                                        if (request.queue_name_s === 'General Counsel') {
                                            numDays = $scope.timeToComplete.request.generalCounsel;
                                        } else {
                                            numDays = $scope.timeToComplete.request[name];
                                        }
                                    }

                                    if (holidaySchedule.data.includeWeekends) {
                                        dueDateTotal = DueDateService.dueDateWithWeekends(request.create_date_tdt.toUTCString(), queuesTotal, holidaySchedule.data.holidays);

                                        request.queueDueDate = DueDateService.dueDateWithWeekends(request.queue_enter_date_tdt.toUTCString(), numDays, holidaySchedule.data.holidays);
                                        //calculate to show the due date on the entered queue with working days and weekends without holidays

                                        request.daysInQueue = DueDateService.workingDaysWithWeekends(request.queue_enter_date_tdt.toUTCString(), holidaySchedule.data.holidays);
                                        //calculate how many days the request is in the queue

                                        var totalDaysLeft = request.queue_name_s !== "Hold" ?
                                            DueDateService.daysLeftWithWeekends(holidaySchedule.data.holidays, dueDateTotal) :
                                            DueDateService.daysLeftWithWeekends(holidaySchedule.data.holidays, dueDateTotal, request.hold_enter_date_tdt);
                                        var queueDaysLeft = request.queue_name_s !== "Hold" ?
                                            DueDateService.daysLeftWithWeekends(holidaySchedule.data.holidays, request.queueDueDate) :
                                            DueDateService.daysLeftWithWeekends(holidaySchedule.data.holidays, request.queueDueDate, request.hold_enter_date_tdt);
                                        request.daysToComplete = queueDaysLeft.days + '/' + totalDaysLeft.days;
                                        //calculate to show how many days until time to complete per queue / per request
                                    } else {
                                        dueDateTotal = DueDateService.dueDateWorkingDays(request.create_date_tdt.toUTCString(), queuesTotal, holidaySchedule.data.holidays);
                                        request.queueDueDate = DueDateService.dueDateWorkingDays(request.queue_enter_date_tdt.toUTCString(), numDays, holidaySchedule.data.holidays);
                                        //calculate to show the due date on the entered queue with working days without holidays and weekends

                                        request.daysInQueue = DueDateService.workingDays(request.queue_enter_date_tdt.toUTCString(), holidaySchedule.data.holidays);
                                        //calculate how many days the request is in the queue

                                        var totalDaysLeft = request.queue_name_s !== "Hold" ?
                                            DueDateService.daysLeft(holidaySchedule.data.holidays, dueDateTotal) :
                                            DueDateService.daysLeft(holidaySchedule.data.holidays, dueDateTotal, request.hold_enter_date_tdt);
                                        var queueDaysLeft = request.queue_name_s !== "Hold" ?
                                            DueDateService.daysLeft(holidaySchedule.data.holidays, request.queueDueDate) :
                                            DueDateService.daysLeft(holidaySchedule.data.holidays, request.queueDueDate, request.hold_enter_date_tdt);
                                        request.daysToComplete = queueDaysLeft.days + '/' + totalDaysLeft.days;
                                        //calculate to show how many days until time to complete per queue / per request
                                    }

                                    if (request.queue_name_s !== "Release") {
                                        request.isOverdue = TaskAlertsService.calculateOverdue(new Date(request.queueDueDate));
                                        request.isDeadline = TaskAlertsService.deadlineCalculate(new Date(request.queueDueDate), $scope.timeToComplete.request.deadlineIndicator);
                                        //calculate to show alert icons if task is in overdue or deadline is approaching
                                    } else {
                                        request.isOverdue = false;
                                        request.isDeadline = false;
                                    }

                                    return request;

                                });

                                $scope.gridOptions = $scope.gridOptions || {};
                                $scope.gridOptions.data = requestsChanged;
                                $scope.gridOptions.totalItems = queueRequest.response.numFound;
                            });

                        }
                    }
                    function getDocumentQuery(requestId) {
                        return GenericRequestsService.queryDocument({
                            requestId: requestId
                        });
                    }

                    function getFileId(repositoryInfo) {
                        return EcmService.findFileByContainerAndFileType({
                            containerId: repositoryInfo,
                            fileType: 'Request Form'
                        });
                    }

                    function openRequestInfoPage(requestId) {

                        var newTabWindow = $window.open('', '_blank');
                        //remove this request from the sorted list for picking next request to avoid to be picked twice
                        QueuesService.removeRequestFromSortedList(requestId);
                        var requestPromise = QueuesService.startWorkingOnRequestFromQueues(requestId);
                        var fileInfo = getDocumentQuery(requestId);
                        $q.all([ requestPromise, fileInfo ]).then(function(request) {
                            getFileId(request[0].container.id).$promise.then(function(fileInfo) {
                                var url = $state.href('request-info', {
                                    id: requestId,
                                    fileId: fileInfo.fileId
                                }, {
                                    absolute: true
                                });
                                
                                newTabWindow.location.href = url;
                                $scope.$emit("report-object-updated", request);
                            }, function() {
                                MessageService.info($translate.instant("queues.startWorking.noRequestToAssign"));
                            });
                        });
                    }
                    function update() {
                        getPage();
                    }
                } ]);