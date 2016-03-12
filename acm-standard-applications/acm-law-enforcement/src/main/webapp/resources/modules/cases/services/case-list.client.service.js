'use strict';

/**
 * @ngdoc service
 * @name services:Case.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/cases/services/case-info.client.service.js modules/cases/services/case-info.client.service.js}
 *
 * Case.ListService provides functions for Case database data
 */
angular.module('services').factory('Case.ListService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ObjectService', 'Object.ListService',
    function ($resource, $translate, Store, Util, ObjectService, ObjectListService) {
        var Service = $resource('api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CASE_LIST: "CaseList"
        };

        /**
         * @ngdoc method
         * @name resetCasesTreeData
         * @methodOf services:Case.ListService
         *
         * @description
         * Reset tree to initial state, including empty tree data
         *
         * @returns None
         */
        Service.resetCasesTreeData = function () {
            var cacheCaseList = new Store.CacheFifo(Service.CacheNames.CASE_LIST);
            cacheCaseList.reset();
        };

        /**
         * @ngdoc method
         * @name updateCasesTreeData
         * @methodOf services:Case.ListService
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
        Service.updateCasesTreeData = function (start, n, sort, filters, query, nodeData) {
            var a1 = ObjectListService;

            ObjectListService.updateObjectTreeData(Service.CacheNames.CASE_LIST
                , start, n, sort, filters, query, nodeData
            );
            //var param = {};
            //param.objectType = "CASE_FILE";
            //param.start = Util.goodValue(start, 0);
            //param.n = Util.goodValue(n, 32);
            //param.sort = Util.goodValue(sort);
            //param.filters = Util.goodValue(filters);
            //param.query = Util.goodValue(query);
            //var cacheCaseList = new Store.CacheFifo(Service.CacheNames.CASE_LIST);
            //var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
            //var treeData = cacheCaseList.get(cacheKey);
            //var found = _.find(treeData.docs, {"nodeId": nodeData.nodeId});
            //if (found) {
            //    found.nodeType = nodeData.nodeType;
            //    found.nodeTitle = nodeData.nodeTitle;
            //    found.nodeToolTip = nodeData.nodeToolTip;
            //}
        };

        /**
         * @ngdoc method
         * @name queryCasesTreeData
         * @methodOf services:Case.ListService
         *
         * @description
         * Query list of cases from SOLR, pack result for Object Tree.
         *
         * @param {Number} start  Zero based index of result starts from
         * @param {Number} n max Number of list to return
         * @param {String} sort  Sort value. Allowed choice is based on backend specification
         * @param {String} filters  Filter value. Allowed choice is based on backend specification
         * @param {String} query  Search term for tree entry to match
         *
         * @returns {Object} Promise
         */
        Service.queryCasesTreeData = function (start, n, sort, filters, query, nodeMaker) {
            var param = {};
            param.objectType = "CASE_FILE";
            param.start = Util.goodValue(start, 0);
            param.n = Util.goodValue(n, 32);
            param.sort = Util.goodValue(sort);
            param.filters = Util.goodValue(filters);
            param.query = Util.goodValue(query);
            var cacheCaseList = new Store.CacheFifo(Service.CacheNames.CASE_LIST);
            var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
            var treeData = cacheCaseList.get(cacheKey);

            return Util.serviceCall({
                service: ObjectListService._queryObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateCaseList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            var node;
                            if (nodeMaker) {
                                node = nodeMaker(doc);
                            } else {
                                node = {
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: ObjectService.ObjectTypes.CASE_FILE
                                    , nodeTitle: Util.goodValue(doc.title_parseable)
                                    , nodeToolTip: Util.goodValue(doc.title_parseable)
                                };
                            }
                            treeData.docs.push(node);
                        });
                        cacheCaseList.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            });
        };


        /**
         * @ngdoc method
         * @name validateCaseList
         * @methodOf services:Case.ListService
         *
         * @description
         * Validate case list data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCaseList = function (data) {
            if (!ObjectListService.validateObjects(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);
