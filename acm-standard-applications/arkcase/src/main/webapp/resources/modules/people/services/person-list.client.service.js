'use strict';

/**
 * @ngdoc service
 * @name services:Person.ListService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/people/services/person-info.client.service.js modules/people/services/person-info.client.service.js}
 *
 * Person.ListService provides functions for Person database data
 */
angular.module('services').factory('Person.ListService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService', function($resource, $translate, Store, Util, ObjectService, ObjectListService) {
    var Service = $resource('api/latest/plugin', {}, {});

    Service.SessionCacheNames = {};
    Service.CacheNames = {
        PERSON_LIST: "PersonList"
    };

    /**
     * @ngdoc method
     * @name resetPeopleTreeData
     * @methodOf services:Person.ListService
     *
     * @description
     * Reset tree to initial state, including empty tree data
     *
     * @returns None
     */
    Service.resetPeopleTreeData = function() {
        var cachePeopleList = new Store.CacheFifo(Service.CacheNames.PERSON_LIST);
        cachePeopleList.reset();
    };

    /**
     * @ngdoc method
     * @name updatePeopleTreeData
     * @methodOf services:Person.ListService
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
    Service.updatePeopleTreeData = function(start, n, sort, filters, query, nodeData) {
        ObjectListService.updateObjectTreeData(Service.CacheNames.PERSON_LIST, start, n, sort, filters, query, nodeData);
    };

    /**
     * @ngdoc method
     * @name queryPeopleTreeData
     * @methodOf services:Person.ListService
     *
     * @description
     * Query list of people from SOLR, pack result for Object Tree.
     *
     * @param {Number} start  Zero based index of result starts from
     * @param {Number} n max Number of list to return
     * @param {String} sort  Sort value. Allowed choice is based on backend specification
     * @param {String} filters  Filter value. Allowed choice is based on backend specification
     * @param {String} query  Search term for tree entry to match
     *
     * @returns {Object} Promise
     */
    Service.queryPeopleTreeData = function(start, n, sort, filters, query, nodeMaker) {
        var param = {};
        param.objectType = ObjectService.ObjectTypes.PERSON;
        param.start = Util.goodValue(start, 0);
        param.n = Util.goodValue(n, 32);
        param.sort = Util.goodValue(sort);
        param.filters = Util.goodValue(filters);
        param.query = Util.goodValue(query);
        param.activeOnly = false;

        var cachePeopleList = new Store.CacheFifo(Service.CacheNames.PERSON_LIST);
        var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
        var treeData = cachePeopleList.get(cacheKey);

        return Util.serviceCall({
            service: ObjectListService._queryObjects,
            param: param,
            result: treeData,
            onSuccess: function(data) {
                if (Service.validatePeopleList(data)) {
                    treeData = {
                        docs: [],
                        total: data.response.numFound
                    };
                    var docs = data.response.docs;
                    _.forEach(docs, function(doc) {
                        var node;
                        if (nodeMaker) {
                            node = nodeMaker(doc);
                        } else {
                            node = {
                                nodeId: Util.goodValue(doc.object_id_s, 0),
                                nodeType: ObjectService.ObjectTypes.PERSON,
                                nodeTitle: Util.goodValue(doc.title_parseable),
                                nodeToolTip: Util.goodValue(doc.title_parseable),
                                nodeStatus: Util.goodValue(doc.status_lcs),
                                nodeStatusColor: Util.goodValue(doc.status_lcs) && (doc.status_lcs == "INACTIVE") ? "list-group-item-inactive-icon" : ""
                            };
                        }
                        treeData.docs.push(node);
                    });
                    cachePeopleList.put(cacheKey, treeData);
                    return treeData;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validatePeopleList
     * @methodOf services:Person.ListService
     *
     * @description
     * Validate people list data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validatePeopleList = function(data) {
        if (!ObjectListService.validateObjects(data)) {
            return false;
        }

        return true;
    };

    return Service;
} ]);
