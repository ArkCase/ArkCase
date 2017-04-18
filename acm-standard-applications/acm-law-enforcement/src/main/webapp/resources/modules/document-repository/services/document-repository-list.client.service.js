'use strict';

/**
 * @ngdoc service
 * @name services:DocumentRepository.ListService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cases/services/case-info.client.service.js modules/cases/services/case-info.client.service.js}
 *
 * Case.ListService provides functions for Case database data
 */
angular.module('services').factory('DocumentRepository.ListService', ['$resource', '$translate', 'Acm.StoreService'
    , 'UtilService', 'ObjectService', 'Object.ListService',
    function ($resource, $translate, Store, Util, ObjectService, ObjectListService) {
        var Service = $resource('api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            DOC_REPO_LIST: "DocumentRepositoryList"
        };

        /**
         * @ngdoc method
         * @name resetDocumentRepositoryTreeData
         * @methodOf services:DocumentRepository.ListService
         *
         * @description
         * Reset tree to initial state, including empty tree data
         *
         * @returns None
         */
        Service.resetDocumentRepositoryTreeData = function () {
            var cacheDocumentRepositoryList = new Store.CacheFifo(Service.CacheNames.DOC_REPO_LIST);
            cacheDocumentRepositoryList.reset();
        };

        /**
         * @ngdoc method
         * @name updateDocumentRepositoryTreeData
         * @methodOf services:DocumentRepository.ListService
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
        Service.updateDocumentRepositoryTreeData = function (start, n, sort, filters, query, nodeData) {
            ObjectListService.updateObjectTreeData(Service.CacheNames.DOC_REPO_LIST
                , start, n, sort, filters, query, nodeData
            );
        };

        /**
         * @ngdoc method
         * @name queryDocumentRepositoryTreeData
         * @methodOf services:DocumentRepository.ListService
         *
         * @description
         * Query list of document repositories from SOLR, pack result for Object Tree.
         *
         * @param {Number} start  Zero based index of result starts from
         * @param {Number} n max Number of list to return
         * @param {String} sort  Sort value. Allowed choice is based on backend specification
         * @param {String} filters  Filter value. Allowed choice is based on backend specification
         * @param {String} query  Search term for tree entry to match
         *
         * @returns {Object} Promise
         */
        Service.queryDocumentRepositoryTreeData = function (start, n, sort, filters, query, nodeMaker) {
            var param = {};
            param.objectType = ObjectService.ObjectTypes.DOC_REPO;
            param.start = Util.goodValue(start, 0);
            param.n = Util.goodValue(n, 32);
            param.sort = Util.goodValue(sort);
            param.filters = Util.goodValue(filters);
            param.query = Util.goodValue(query);
            var cacheDocumentRepositoryList = new Store.CacheFifo(Service.CacheNames.DOC_REPO_LIST);
            var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
            var treeData = cacheDocumentRepositoryList.get(cacheKey);

            return Util.serviceCall({
                service: ObjectListService._queryObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateDocumentRepositoryList(data)) {
                        treeData = {
                            docs: [],
                            total: data.response.numFound
                        };
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            var node;
                            if (nodeMaker) {
                                node = nodeMaker(doc);
                            } else {
                                node = {
                                    nodeId: Util.goodValue(doc.object_id_s, 0)
                                    , nodeType: ObjectService.ObjectTypes.DOC_REPO
                                    , nodeTitle: Util.goodValue(doc.title_parseable)
                                    , nodeToolTip: Util.goodValue(doc.title_parseable)
                                };
                            }
                            treeData.docs.push(node);
                        });
                        cacheDocumentRepositoryList.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            });
        };


        /**
         * @ngdoc method
         * @name validateDocumentRepositoryList
         * @methodOf services:DocumentRepository.ListService
         *
         * @description
         * Validate document repositories list data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateDocumentRepositoryList = function (data) {
            return ObjectListService.validateObjects(data);
        };

        return Service;
    }
]);
