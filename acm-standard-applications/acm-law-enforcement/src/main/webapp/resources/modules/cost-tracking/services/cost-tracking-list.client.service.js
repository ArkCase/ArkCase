'use strict';

/**
 * @ngdoc service
 * @name service:CostTracking.ListService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cost-tracking/services/cost-tracking-info.client.service.js modules/cost-tracking/services/cost-tracking-info.client.service.js}

 * CostTracking.ListService provides functions for Costsheet database data
 */
angular.module('services').factory('CostTracking.ListService', ['$resource', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService'
    , function ($resource, $translate, Store, Util, ObjectService, ObjectListService) {
        var Service = $resource('api/v1/service', {}, {});

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
         * @name updateCostTrackingTreeData
         * @methodOf services:CostTracking.ListService
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
        Service.updateCostTrackingTreeData = function (start, n, sort, filters, query, nodeData) {
            ObjectListService.updateObjectTreeData(Service.CacheNames.COSTSHEET_LIST
                , start, n, sort, filters, query, nodeData
            );
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
         * @param {String} query  Search term for tree entry to match
         *
         *
         * @returns {Object} Promise
         */
        Service.queryCostTrackingTreeData = function (userId, start, n, sort, filters, query, nodeMaker) {
            var param = {};
            param.userId = Util.goodValue(userId);
            param.objectType = "costsheet";
            param.start = Util.goodValue(start, 0);
            param.n = Util.goodValue(n, 32);
            param.sort = Util.goodValue(sort);
            //param.filters = Util.goodValue(filters);
            param.query = Util.goodValue(query);

            var cacheCostTrackingList = new Store.CacheFifo(Service.CacheNames.COSTSHEET_LIST);
            var cacheKey = param.userId + "." + param.start + "." + param.n + "." + param.sort + "." + param.query;
            var treeData = cacheCostTrackingList.get(cacheKey);

            return Util.serviceCall({
                service: ObjectListService._queryUserObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateCostsheetList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            var node;
                            if (nodeMaker) {
                                node = nodeMaker(doc);
                            } else {
                                node = {
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: ObjectService.ObjectTypes.COSTSHEET
                                    , nodeTitle: Util.goodValue(doc.name)
                                    , nodeToolTip: Util.goodValue(doc.name)
                                };
                            }
                            treeData.docs.push(node);
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