'use strict';

/**
 * @ngdoc service
 * @name service:TimeTracking.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/time-tracking/services/time-tracking-list.client.service.js modules/time-tracking/services/time-tracking-list.client.service.js}

 * TimeTracking.ListService provides functions for Timesheet database data
 */
angular.module('services').factory('TimeTracking.ListService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ConstantService', 'Object.ListService',
    function ($resource, $translate, Store, Util, Constant, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/v1/service/timesheet', {}, {

            /**
             * @ngdoc method
             * @name listObjects
             * @methodOf service:TimeTracking.ListService
             *
             * @description
             * Get list of all timesheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.userId  String that contains userId for logged user. List of timesheets are generated depending on this userId
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            listObjects: {
                method: 'GET',
                url: 'proxy/arkcase/api/v1/service/timesheet/user/:userId?start=:start&n=:n&sort=:sort',
                cache: false,
                isArray: false
            }
        });

        /**
         * @ngdoc method
         * @name queryTimeTrackingTreeData
         * @methodOf service:TimeTracking.ListService
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
        Service.queryTimeTrackingTreeData = function (userId, start, n, sort) {
            var treeData = null;

            var param = {};
            param.userId = userId;
            param.start = start;
            param.n = n;
            param.sort = sort;

            return Util.serviceCall({
                service: Service.listObjects
                , param: param
                , onSuccess: function (data) {
                    if (Service.validateTimesheetList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: Constant.ObjectTypes.TIMESHEET
                                , nodeTitle: Util.goodValue(doc.name)
                                , nodeToolTip: Util.goodValue(doc.name)
                            });
                        });
                        return treeData;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateTimesheetList
         * @methodOf service:TimeTracking.ListService
         *
         * @description
         * Validate timesheet list data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateTimesheetList = function (data) {
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);