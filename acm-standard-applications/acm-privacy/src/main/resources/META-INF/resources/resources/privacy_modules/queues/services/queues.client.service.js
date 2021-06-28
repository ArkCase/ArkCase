'use strict';

/**
 * @ngdoc service
 * @name queues.service:Queues.QueuesService
 *
 * @description
 *
 * {@link /privacy-extension/blob/develop/src/main/resources/META-INF/resources/resources/custom_modules/queues/services/queues.client.service.js queues/services/queues.client.service.js}
 *
 * The QueuesService provides Queues management functionality.
 */
angular.module('queues').factory('Queues.QueuesService', [ '$http', '$q', 'Ecm.MultiDownloadService', 'Authentication', function($http, $q, MultiDownloadService, Authentication) {
    var filteredSortedRequests = [];

    /**
     * sorts the data provided to be ready for picking next request
     * @param {array} array of requests
     */
    function sortForPickingNextRequest(data) {
        var deferred = $q.defer();
        //get current logged in user
        Authentication.queryUserInfo().then(function(userInfo) {
            //remove locked by others
            data = _.filter(data, function(request) {
                if (!request.locked) {
                    return true;
                } else {
                    if (request.lockedBy == userInfo.userId) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            //add new field to help (simplify) sorting
            _.forEach(data, function(request) {
                //generate the string which will be used for sorting... sortField will have value "locked_priority_dueDateTime" like: 1-true_2-Urgent_13123121113-
                //add that string as new field in request

                var locked = 'locked-' + (request.locked ? '1-TRUE' : '0-FALSE');
                var assignee = 'assignee-' + (request.assignee_id_lcs == userInfo.userId ? '0-' + request.assignee_id_lcs : '1-' + request.assignee_id_lcs);
                var notAssigned = 'not_assigned-0-TRUE';
                if (request.assignee_id_lcs) {
                    notAssigned = 'not_assigned-1-FALSE';
                }
                var priorityByNumber = 'priority-' + getPriorityByNumber(request.priority_lcs);
                var dueDate = 'dueDate-' + request.dueDate_tdt.getTime() + '-' + request.dueDate_tdt;
                request['sortField'] = [ notAssigned, locked, assignee, priorityByNumber, dueDate ].join('_');
            });

            //do the sorting
            data = _.sortBy(data, 'sortField');
            deferred.resolve(data);
        }, function(error) {
            deferred.reject(error);
        });
        return deferred.promise;
    }

    /**
     *
     * Return next document for 'Start Working'. Document is becoming locked.
     * Returned Request is assigned to the current user
     *
     * @returns {HttpPromise} Future info about next request
     */
    function tryNextRequestInList(deferred) {
        if (!deferred) {
            deferred = $q.defer();
        }
        if (filteredSortedRequests.length > 0) {
            var requestId = filteredSortedRequests.shift().object_id_s;
            $http({
                method: 'POST',
                url: 'api/v1/plugin/requests/' + requestId + '/start-working'
            }).then(function(response) {
                //success
                deferred.resolve(response.data);
            }, function(error) {
                //error is returned, try next request in list if any
                if (filteredSortedRequests.length > 0) {
                    tryNextRequestInList(deferred);
                } else {
                    deferred.reject();
                }
            });
        } else {
            deferred.reject();
        }
        return deferred.promise;
    }

    /**
     * maps string values of priority into numbers
     * @param priorityStr
     * @returns {string} return combined number + priority_text_value
     */
    function getPriorityByNumber(priorityStr) {
        switch (priorityStr.toLowerCase()) {
            case 'normal':
                return 3 + '-' + priorityStr.toLowerCase();
            case 'medium':
                return 2 + '-' + priorityStr.toLowerCase();
            case 'urgent':
                return 1 + '-' + priorityStr.toLowerCase();
            default:
                return 4 + '-' + 'unknown';
        }
    }

    return {
        /**
         * tries each request to start working on, if first is successful then that request is returned
         * @returns request if eligible, null if not found
         */
        getNextRequest: function() {
            console.log('getNextRequest');
            return tryNextRequestInList();
        },

        /**
         * removes request from the sorted list for picking next request
         * @param requestId
         */
        removeRequestFromSortedList: function(requestId) {
            for ( var index in filteredSortedRequests) {
                if (filteredSortedRequests[index].object_id_s == requestId) {
                    filteredSortedRequests.splice(index, 1);
                    return;
                }
            }
        },

        /**
         * @ngdoc method
         * @name downloadRequests
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Array} downloadInfo Information about downloaded requests' documents
         *
         * @description
         * Performs multiple downloading of requests documents.
         *
         */
        downloadRequests: function(downloadInfo) {
            var pdfDownloadUrl = '/arkcase/api/v1/plugin/request/download/pdf/';
            var urls = [];
            // Prepare for doewnloading request documents
            _.forEach(downloadInfo.requestPackages, function(request) {
                _.forEach(request, function(value, key) {
                    urls.push(pdfDownloadUrl + value);
                });
            });

            // Add additoinal documents
            if (!_.isUndefined(downloadInfo.printManifest)) {
                urls.push(pdfDownloadUrl + downloadInfo.printManifest);
            }

            MultiDownloadService.multiDownload(urls);
        },

        /**
         * @ngdoc method
         * @name downloadMergedRequests
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Array} List of requests identifiers to merge and download
         *
         * @description
         * Performs multiple downloading of requests documents.
         */
        downloadMergedrequests: function(requests) {
            var url = this.getMergedRequestsUrl(requests);
            MultiDownloadService.multiDownload([ url ]);
        },

        /**
         * @ngdoc method
         * @name downloadMergedRequests
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Array} List of requests identifiers
         *
         * @description
         * Produce link to download merged PDF
         *
         * @returns {String} url to merged requests documents
         */
        getMergedRequestsUrl: function(requests) {
            return '/arkcase/api/v1/plugin/request/print/' + requests.join(',')
        },

        /**
         * @ngdoc method
         * @name queryQueues
         * @methodOf queues.service:Queues.QueuesService
         *
         *
         * @description
         * Retrieve list of queues
         *
         * @returns {HttpPromise} Future info about queues list
         */
        queryQueues: function() {
            return $http({
                method: 'GET',
                url: 'api/v1/plugin/queues',
                cache: true
            }).then(function(response) {
                return response.data
            });
        },

        /**
         * @ngdoc method
         * @name queryQueueRequests
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Object} req Request object
         * @param {String} req.queueId Queue identifier
         * @param {Array} req.filters Array of filter objects in next format {column: value}
         * @param {String} req.filters[].column Filtered column name
         * @param {String} req.filters[].value Filtered value
         * @param {String} req.filters[].condition Filter condition can take next values 'from' or 'to'
         * @param {String} req.sortBy Sort column
         * @param {String} req.sortDir Sort direction (asc, desc)
         * @param {Number} req.startWith Number of start item
         * @param {Number} req.pageSize Returned page size
         * @param {Array} req.columns Array of colums definitions
         *
         * @description
         * Returns paginated and filtered requests list
         *
         * @returns {HttpPromise} Future info about requests list
         */
        queryQueueRequests: function(req) {
            // required filters: object type and queueId
            var filters = [ 'object_type_s:CASE_FILE', 'queue_id_s:' + req.queueId ];

            _.forEach(req.filters, function(filter) {
                var columnType = _.find(req.columns, {
                    name: filter.column
                });
                if (columnType && columnType.type) {
                    columnType = columnType.type;
                } else {
                    columnType = null;
                }

                if (columnType == 'date' && _.isDate(filter.value)) {
                    var solrDate = moment(filter.value).format("YYYY-MM-DDTHH:mm:ss") + "Z";
                    if (filter.condition == 'from') {
                        filters.push(filter.column + ':[' + solrDate + ' TO *]');
                    } else if (filter.condition == 'to') {
                        filters.push(filter.column + ':[* TO ' + solrDate.replace("00:00:00", "23:59:59") + ']');
                    }
                } else if (columnType == 'boolean' && _.isBoolean(filter.value)) {
                    if (filter.value) {
                        filters.push(filter.column);
                    } else {
                        filters.push('NOT ' + filter.column);
                    }
                } else if (columnType == 'number' && !isNaN(parseInt(filter.value))) {
                    filter.value = filter.value.replace('*', '');
                    filters.push(filter.column + ':' + filter.value);
                } else if (_.isString(filter.value) && (!columnType || columnType == 'string' || columnType == 'object')) {
                    // Remove * from request. and add '*' to the end of request
                    var filterValue = '*+AND+' + filter.column + ':*';
                    filter.value = _.contains(filter.value, ' ') ? '*' + filter.value.split(' ').join(filterValue) : filter.value.replace('*', '');
                    if (filter.value != '') {
                        filters.push(filter.column + ':' + filter.value + '*');
                    }
                }
            });

            filters = filters.join('+AND+');

            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch',
                params: {
                    q: filters,
                    s: req.sortBy + ' ' + req.sortDir,
                    start: req.startWith,
                    n: req.pageSize

                    // TODO: figure out why fl parameter doesn't work
                    //fl: [
                    //    'address_lcs',
                    //    'assignee_full_name_lcs',
                    //    'billable_b',
                    //    'city_lcs',
                    //    'client_id_s',
                    //    'create_date_tdt',
                    //    'dueDate_tdt',
                    //    'mrn_s',
                    //    'request_id_s',
                    //    'page_count_l',
                    //    'patient_name_s',
                    //    'priority_lcs',
                    //    'requester_type_s',
                    //    'requester_name_s',
                    //    'reject_reason_ss',
                    //    'state_lcs',
                    //    'type_lcs',
                    //    'work_order_number_l',
                    //    'zip_code_s'
                    //].join(',')
                }
            }).then(function(response) {
                // Perform second request to get locked items
                var requestsData = response.data;
                var ids = _.pluck(requestsData.response.docs, 'id');

                _.forEach(requestsData.response.docs, function(item) {
                    // item['client_modified_tdt'] = new Date(item['client_modified_tdt']);
                    item['create_date_tdt'] = new Date(item['create_date_tdt']);
                    item['dueDate_tdt'] = new Date(item['dueDate_tdt']);
                    item['modified_date_tdt'] = new Date(item['modified_date_tdt']);
                    item['received_date_tdt'] = new Date(item['received_date_tdt']);
                });

                var idsFilter = [];

                _.forEach(ids, function(id) {
                    idsFilter.push('parent_ref_s:' + id);
                });

                if (idsFilter.length == 0) {
                    deferred.resolve(requestsData);
                } else {
                    idsFilter = '(' + idsFilter.join('+OR+') + ')';
                    var lockFilter = 'object_type_s:OBJECT_LOCK+AND+' + idsFilter;

                    $http({
                        method: 'GET',
                        url: 'api/v1/plugin/search/advancedSearch',
                        params: {
                            q: lockFilter,
                            n: req.pageSize
                        }
                    }).then(function(response) {
                        _.forEach(response.data.response.docs, function(lockObject) {
                            var foundRequest = _.find(requestsData.response.docs, {
                                id: lockObject.parent_ref_s
                            });
                            if (foundRequest) {
                                foundRequest.lockedBy = lockObject.creator_lcs;
                                foundRequest.locked = true;
                            }
                        });

                        filteredSortedRequests = requestsData.response.docs;

                        // The logic below is for sorting in Bactes
                        /*
                        //put clone of actual requests list
                        sortForPickingNextRequest(_.cloneDeep(requestsData.response.docs)).then(function (data) {
                            filteredSortedRequests = data;
                        }, function () {
                            //no data available
                            filteredSortedRequests = [];
                        });
                         */
                        deferred.resolve(requestsData);
                    });
                }
            });
            return deferred.promise;
        },

        /**
         * @ngdoc method
         * @name startWorking
         * @methodOf queues.service:Queues.QueuesService
         */
        startWorking: function(queueId) {
            return $http({
                method: 'POST',
                url: 'api/v1/plugin/requests/' + queueId + '/start-working'
            }).then(function(response) {
                return response.data;
            });
        },

        /**
         * @ngdoc method
         * @name startWorkingOnRequestFromQueues
         * @methodOf queues.service:Queues.QueuesService
         */
        startWorkingOnRequestFromQueues: function (requestId) {
            return $http({
                method: 'POST',
                url: 'api/v1/plugin/requests/' + requestId + '/start-working-on-selected'
            }).then(function (response) {
                return response.data;
            });
        },

        /**
         * @ngdoc method
         * @name queryDownloadRequestsInfo
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Object} req Request object
         * @param {Array} req.requestsIds Array of request identifiers
         *
         * @description
         * Returns list of requests' ready to download files identifiers
         *
         * @returns {HttpPromise} Future info about downloaded files
         */
        queryDownloadRequestsInfo: function(req) {
            return $http({
                method: 'GET',
                url: 'api/v1/plugin/request/download/' + req.requestsIds.join(',')
            }).then(function(response) {
                return response.data
            })
        },

        /**
         * @ngdoc method
         * @name completeRequests
         * @methodOf queues.service:Queues.QueuesService
         *
         * @param {Array} requestsIds Array of requests identifiers
         *
         *
         * @returns {HttpPromise} Future info about completed or error requests
         */
        completeRequests: function(requestsIds) {
            var url = 'api/v1/plugin/request/workflow/complete/' + requestsIds.join(',');
            return $http({
                method: 'POST',
                url: url,
                data: ''
            });
        }
    };
} ]);