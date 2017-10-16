'use strict';

/**
 * @ngdoc service
 * @name service:TimeTracking.ListService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/time-tracking/services/time-tracking-list.client.service.js modules/time-tracking/services/time-tracking-list.client.service.js}

 * TimeTracking.ListService provides functions for Timesheet database data
 */
angular.module('services').factory('TimeTracking.ListService', ['$resource', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService'
    , function ($resource, $translate, Store, Util, ObjectService, ObjectListService) {
        var Service = $resource('api/v1/service', {}, {});

        Service.CacheNames = {
            TIMESHEET_LIST: "TimesheetList"
        };

        /**
         * @ngdoc method
         * @name resetTimeTrackingTreeData
         * @methodOf services:TimeTracking.ListService
         *
         * @description
         * Reset tree to initial state, including empty tree data
         *
         * @returns None
         */
        Service.resetTimeTrackingTreeData = function () {
            var cacheTimesheetList = new Store.CacheFifo(Service.CacheNames.TIMESHEET_LIST);
            cacheTimesheetList.reset();
        };

        /**
         * @ngdoc method
         * @name updateTimeTrackingTreeData
         * @methodOf services:TimeTracking.ListService
         *
         * @description
         * Update a node data in tree.
         *
         * @param {Number} start  Zero based index of result starts from
         * @param {Number} n max Number of list to return
         * @param {String} sort  Sort value. Allowed choice is based on backend specification
         * @param {String} filters  Filter value. Allowed choice is based on backend specification
         * @param {String} query  Search term for tree entry to match
         * @param {Object} nodeData  Node data
         *
         * @returns {Object} Promise
         */
        Service.updateTimeTrackingTreeData = function (start, n, sort, filters, query, nodeData) {
            ObjectListService.updateObjectTreeData(Service.CacheNames.TIMESHEET_LIST
                , start, n, sort, filters, query, nodeData
            );
        };

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
         * @param {String} query  Search term for tree entry to match
         *
         *
         * @returns {Object} Promise
         */
        Service.queryTimeTrackingTreeData = function (userId, start, n, sort, filters, query, nodeMaker) {
            var param = {};
            param.userId = Util.goodValue(userId);
            param.objectType = "timesheet";
            param.start = Util.goodValue(start, 0);
            param.n = Util.goodValue(n, 32);
            param.sort = Util.goodValue(sort);
            //param.filters = Util.goodValue(filters);
            param.query = Util.goodValue(query);

            var cacheTimesheetList = new Store.CacheFifo(Service.CacheNames.TIMESHEET_LIST);
            var cacheKey = param.userId + "." + param.start + "." + param.n + "." + param.sort + "." + param.query;
            var treeData = cacheTimesheetList.get(cacheKey);

            return Util.serviceCall({
                service: ObjectListService._queryUserObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateTimesheetList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            var node;
                            if (nodeMaker) {
                                node = nodeMaker(doc);
                            } else {
                                node = {
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: ObjectService.ObjectTypes.TIMESHEET
                                    , nodeTitle: Util.goodValue(doc.name)
                                    , nodeToolTip: Util.goodValue(doc.name)
                                };
                            }
                            treeData.docs.push(node);
                        });
                        cacheTimesheetList.put(cacheKey, treeData);
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
            if (!ObjectListService.validateObjects(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);