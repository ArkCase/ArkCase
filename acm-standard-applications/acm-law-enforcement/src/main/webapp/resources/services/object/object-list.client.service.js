'use strict';

/**
 * @ngdoc service
 * @name services:Object.ListService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-list.client.service.js services/object/object-list.client.service.js}

 * Object.ListService includes REST calls related to object list in SOLR
 */
angular.module('services').factory('Object.ListService', ['$resource', 'Acm.StoreService', 'UtilService', 'SearchService'
    , function ($resource, Store, Util, SearchService) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryObjects
             * @methodOf services:Object.ListService
             *
             * @description
             * Query list of objects from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.objectType Object type. 'CASE_FILE', 'COMPLAINT', 'TASK', etc.
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {String} params.filters  Filter value. Allowed choice is based on backend specification
             * @param {String} params.query  Search term for tree entry to match
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _queryObjects: {
                method: 'GET',
                url: 'api/latest/plugin/search/:objectType?start=:start&n=:n&s=:sort&filters=:filters&searchQuery=:query',
                cache: false,
                isArray: false
            }

            /**
             * @ngdoc method
             * @name _queryUserObjects
             * @methodOf services:Object.ListService
             *
             * @description
             * Get list of all costsheets from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {String} params.dataType  Object data type. Currently it supports 'timesheet' and 'costsheet'
             * @param {String} params.userId  String that contains userId for logged user. List of costsheets are generated depending on this userId
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {String} params.query  Search term for tree entry to match
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _queryUserObjects: {
                method: 'GET',
                url: 'api/v1/service/:objectType/user/:userId?start=:start&n=:n&s=:sort&searchQuery=:query',
                cache: false,
                isArray: false
            }
        });


        /**
         * @ngdoc method
         * @name validateObjects
         * @methodOf services:Object.ListService
         *
         * @description
         * Validate list of objects as SOLR query result
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateObjects = function (data) {
            if (!SearchService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name updateObjectTreeData
         * @methodOf services:Object.ListService
         *
         * @description
         * Common help function to update a node data in tree.
         *
         * @param {String} cacheName  Tree cache name
         * @param {Number} start  Zero based index of result starts from
         * @param {Number} n max Number of list to return
         * @param {String} sort  Sort value. Allowed choice is based on backend specification
         * @param {String} filters  Filter value. Allowed choice is based on backend specification
         * @param {String} query  Search term for tree entry to match
         * @param {Object} nodeData  Node data
         *
         * @returns {Object} Promise
         */
        Service.updateObjectTreeData = function (cacheName, start, n, sort, filters, query, nodeData) {
            var param = {};
            param.objectType = nodeData.nodeType;
            param.start = Util.goodValue(start, 0);
            param.n = Util.goodValue(n, 32);
            param.sort = Util.goodValue(sort);
            param.filters = Util.goodValue(filters);
            param.query = Util.goodValue(query);
            var cacheObjectList = new Store.CacheFifo(cacheName);
            var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
            var treeData = cacheObjectList.get(cacheKey);
            var docs = Util.goodMapValue(treeData, "docs", []);
            var found = _.find(docs, {"nodeId": nodeData.nodeId});
            if (found) {
                found.nodeType = nodeData.nodeType;
                found.nodeTitle = nodeData.nodeTitle;
                found.nodeToolTip = nodeData.nodeToolTip;
            }
        };

        return Service;
    }
]);
