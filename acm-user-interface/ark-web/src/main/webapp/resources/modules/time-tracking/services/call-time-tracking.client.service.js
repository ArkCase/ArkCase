'use strict';

/**
 * @ngdoc service
 * @name time-tracking.service:CallTimeTrackingService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/time-tracking/services/call-time-tracking.client.service.js modules/time-tracking/services/call-time-tracking.client.service.js}

 * CallTimeTrackingService contains functions of TimeTracking service to support default error handling and data validation.
 */
angular.module('services').factory('CallTimeTrackingService', ['$resource', '$translate', 'StoreService', 'UtilService', 'Solr.SearchService', 'TimeTrackingService', 'ObjectService',
    function ($resource, $translate, Store, Util, SolrSearchService, TimeTrackingService, ObjectService) {
        var ServiceCall = {

            /**
             * @ngdoc method
             * @name queryTimeTrackingTreeData
             * @methodOf time-tracking.service:CallTimeTrackingService
             *
             * @description
             * Query list of timesheets from SOLR and pack result for Object Tree.
             *
             * @param {String} userId  String that contains logged user
             * @param {Number} start  Zero based index of result starts from
             * @param {Number} n max Number of list to return
             * @param {String} sort  Sort value. Allowed choice is based on backend specification
             *
             *
             * @returns {Object} Promise
             */
             queryTimeTrackingTreeData: function (userId, start, n, sort) {
                var treeData = null;

                var param = {};
                param.userId = userId;
                param.start = start;
                param.n = n;
                param.sort = sort;

                return Util.serviceCall({
                    service: TimeTrackingService.listObjects
                    , param: param
                    , onSuccess: function (data) {
                        if (SolrSearchService.validateSolrData(data)) {
                            treeData = {docs: [], total: data.response.numFound};
                            var docs = data.response.docs;
                            _.forEach(docs, function (doc) {
                                treeData.docs.push({
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: ObjectService.ObjectTypes.TIMESHEET
                                    , nodeTitle: Util.goodValue(doc.name)
                                    , nodeToolTip: Util.goodValue(doc.name)
                                });
                            });
                            return treeData;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name getTimeTrackingInfo
             * @methodOf time-tracking.service:CallTimeTrackingService
             *
             * @description
             * Query timesheet data
             *
             * @param {Number} id  Timesheet ID
             *
             * @returns {Object} Promise
             */
            , getTimeTrackingInfo: function (id) {
                return Util.serviceCall({
                    service: TimeTrackingService.get
                    , param: {id: id}
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTimesheet(data)) {
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name saveTimesheetInfo
             * @methodOf time-tracking.service:CallTimeTrackingService
             *
             * @description
             * Save timesheet data
             *
             * @param {Object} timesheetInfo  Timesheet data
             *
             * @returns {Object} Promise
             */

            , saveTimesheetInfo: function (timesheetInfo) {
                if (!ServiceCall.validateTimesheet(timesheetInfo)) {
                    return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                }
                return Util.serviceCall({
                    service: TimeTrackingService.save
                    , data: timesheetInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateTimesheet(data)) {
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateTimesheet
             * @methodOf time-tracking.service:CallTimeTrackingService
             *
             * @description
             * Validate timesheet
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateTimesheet: function(data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.id)) {
                    return false;
                }
                if (Util.isEmpty(data.user)) {
                    return false;
                }
                if (Util.isEmpty(data.user.userId)) {
                    return false;
                }
                if (Util.isEmpty(data.startDate)) {
                    return false;
                }
                if (Util.isEmpty(data.endDate)) {
                    return false;
                }
                if (!Util.isArray(data.times)) {
                    return false;
                }
                if (Util.isEmpty(data.status)) {
                    return false;
                }
                if (Util.isEmpty(data.creator)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
