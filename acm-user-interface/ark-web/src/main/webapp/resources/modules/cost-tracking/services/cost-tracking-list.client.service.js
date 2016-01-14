'use strict';

/**
 * @ngdoc service
 * @name service:CostTracking.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cost-tracking/services/cost-tracking-info.client.service.js modules/cost-tracking/services/cost-tracking-info.client.service.js}

 * CostTracking.ListService provides functions for Costsheet database data
 */
angular.module('services').factory('CostTracking.ListService', ['$resource', '$translate'
    , 'StoreService', 'UtilService', 'ObjectService', 'Object.ListService'
    , function ($resource, $translate, Store, Util, ObjectService, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/v1/service/costsheet', {}, {

            /**
             * @ngdoc method
             * @name listObjects
             * @methodOf service:CostTracking.ListService
             *
             * @description
             * Get list of all costsheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.userId  String that contains userId for logged user. List of costsheets are generated depending on this userId
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
                url: 'proxy/arkcase/api/v1/service/costsheet/user/:userId?start=:start&n=:n&s=:sort',
                cache: false,
                isArray: false
            }
        });

        Service.CacheNames = {
            COSTSHEET_LIST: "CostsheetList"
        };

        /**
         * @ngdoc method
         * @name resetCostTrackingTreeData
         * @methodOf services:CostTracking.ListService
         *
         * @description
         * Reset tree to initial state, including empty tree data
         *
         * @returns None
         */
        Service.resetCostTrackingTreeData = function () {
            var cacheCostTrackingList = new Store.CacheFifo(Service.CacheNames.COSTSHEET_LIST);
            cacheCostTrackingList.reset();
        };

        /**
         * @ngdoc method
         * @name queryCostTrackingTreeData
         * @methodOf service:CostTracking.ListService
         *
         * @description
         * Query list of costsheets from SOLR and pack result for Object Tree.
         *
         * @param {String} userId  String that contains logged user
         * @param {Number} start  Zero based index of result starts from
         * @param {Number} n max Number of list to return
         * @param {String} sort  Sort value. Allowed choice is based on backend specification
         *
         *
         * @returns {Object} Promise
         */
        Service.queryCostTrackingTreeData = function (userId, start, n, sort) {
            var cacheCostTrackingList = new Store.CacheFifo(Service.CacheNames.COSTSHEET_LIST);
            var cacheKey = userId + "." + start + "." + n + "." + sort;
            var treeData = cacheCostTrackingList.get(cacheKey);

            var param = {};
            param.userId = userId;
            param.start = start;
            param.n = n;
            param.sort = sort;

            return Util.serviceCall({
                service: Service.listObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateCostsheetList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: ObjectService.ObjectTypes.COSTSHEET
                                , nodeTitle: Util.goodValue(doc.name)
                                , nodeToolTip: Util.goodValue(doc.name)
                            });
                        });
                        cacheCostTrackingList.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCostsheetList
         * @methodOf service:CostTracking.ListService
         *
         * @description
         * Validate costsheet list data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCostsheetList = function (data) {
            if (!ObjectListService.validateObjects(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);