'use strict';

/**
 * @ngdoc service
 * @name services:Organization.ListService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/organizations/services/organization-info.client.service.js modules/organizations/services/organization-info.client.service.js}
 *
 * Organization.ListService provides functions for Organization database data
 */
angular.module('services').factory('Organization.ListService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService', function($resource, $translate, Store, Util, ObjectService, ObjectListService) {
    var Service = $resource('api/latest/plugin', {}, {});

    Service.SessionCacheNames = {};
    Service.CacheNames = {
        ORGANIZATION_LIST: "OrganizationList"
    };

    /**
     * @ngdoc method
     * @name resetOrganizationsTreeData
     * @methodOf services:Organization.ListService
     *
     * @description
     * Reset tree to initial state, including empty tree data
     *
     * @returns None
     */
    Service.resetOrganizationsTreeData = function() {
        var cacheOrganizationList = new Store.CacheFifo(Service.CacheNames.ORGANIZATION_LIST);
        cacheOrganizationList.reset();
    };

    /**
     * @ngdoc method
     * @name updateOrganizationsTreeData
     * @methodOf services:Organization.ListService
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
    Service.updateOrganizationsTreeData = function(start, n, sort, filters, query, nodeData) {
        ObjectListService.updateObjectTreeData(Service.CacheNames.ORGANIZATION_LIST, start, n, sort, filters, query, nodeData);
    };

    /**
     * @ngdoc method
     * @name queryPOrganizationsTreeData
     * @methodOf services:Organization.ListService
     *
     * @description
     * Query list of organizations from SOLR, pack result for Object Tree.
     *
     * @param {Number} start  Zero based index of result starts from
     * @param {Number} n max Number of list to return
     * @param {String} sort  Sort value. Allowed choice is based on backend specification
     * @param {String} filters  Filter value. Allowed choice is based on backend specification
     * @param {String} query  Search term for tree entry to match
     *
     * @returns {Object} Promise
     */
    Service.queryOrganizationsTreeData = function(start, n, sort, filters, query, nodeMaker) {
        var param = {};
        param.objectType = ObjectService.ObjectTypes.ORGANIZATION;
        param.start = Util.goodValue(start, 0);
        param.n = Util.goodValue(n, 32);
        param.sort = Util.goodValue(sort);
        param.filters = Util.goodValue(filters);
        param.query = Util.goodValue(query);
        param.activeOnly = false;

        var cacheOrganizationList = new Store.CacheFifo(Service.CacheNames.ORGANIZATION_LIST);
        var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
        var treeData = cacheOrganizationList.get(cacheKey);

        return Util.serviceCall({
            service: ObjectListService._queryObjects,
            param: param,
            result: treeData,
            onSuccess: function(data) {
                if (Service.validateOrganizationsList(data)) {
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
                                nodeType: ObjectService.ObjectTypes.ORGANIZATION,
                                nodeTitle: Util.goodValue(doc.title_parseable),
                                nodeToolTip: Util.goodValue(doc.title_parseable),
                                nodeStatus: Util.goodValue(doc.status_lcs),
                                nodeStatusColor: Util.goodValue(doc.status_lcs) && (doc.status_lcs == "INACTIVE") ? "list-group-item-inactive-icon" : ""
                            };
                        }
                        treeData.docs.push(node);
                    });
                    cacheOrganizationList.put(cacheKey, treeData);
                    return treeData;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateOrganizationsList
     * @methodOf services:Organization.ListService
     *
     * @description
     * Validate organizations list data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateOrganizationsList = function(data) {
        if (!ObjectListService.validateObjects(data)) {
            return false;
        }

        return true;
    };

    return Service;
} ]);
