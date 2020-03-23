'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.ListService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/complaints/services/complaint-info.client.service.js modules/complaints/services/complaint-info.client.service.js}
 *
 * Complaint.ListService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.ListService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService', function($resource, $translate, Store, Util, ObjectService, ObjectListService) {
    var Service = $resource('api/latest/plugin', {}, {});

    Service.SessionCacheNames = {};
    Service.CacheNames = {
        COMPLAINT_LIST: "ComplaintList"
    };

    /**
     * @ngdoc method
     * @name resetComplaintsTreeData
     * @methodOf services:Complaint.ListService
     *
     * @description
     * Reset tree to initial state, including empty tree data
     *
     * @returns None
     */
    Service.resetComplaintsTreeData = function() {
        var cacheComplaintList = new Store.CacheFifo(Service.CacheNames.COMPLAINT_LIST);
        cacheComplaintList.reset();
    };

    /**
     * @ngdoc method
     * @name updateComplaintsTreeData
     * @methodOf services:Complaint.ListService
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
    Service.updateComplaintsTreeData = function(start, n, sort, filters, query, nodeData) {
        ObjectListService.updateObjectTreeData(Service.CacheNames.COMPLAINT_LIST, start, n, sort, filters, query, nodeData);
    };

    /**
     * @ngdoc method
     * @name queryComplaintsTreeData
     * @methodOf services:Complaint.ListService
     *
     * @description
     * Query list of complaints from SOLR, pack result for Object Tree.
     *
     * @param {Number} start  Zero based index of result starts from
     * @param {Number} n max Number of list to return
     * @param {String} sort  Sort value. Allowed choice is based on backend specification
     * @param {String} filters  Filter value. Allowed choice is based on backend specification
     * @param {String} query  Search term for tree entry to match
     *
     * @returns {Object} Promise
     */
    Service.queryComplaintsTreeData = function(start, n, sort, filters, query, nodeMaker) {
        var param = {};
        param.objectType = "COMPLAINT";
        param.start = Util.goodValue(start, 0);
        param.n = Util.goodValue(n, 32);
        param.sort = Util.goodValue(sort);
        param.filters = Util.goodValue(filters);
        param.query = Util.goodValue(query);

        var cacheComplaintList = new Store.CacheFifo(Service.CacheNames.COMPLAINT_LIST);
        var cacheKey = param.start + "." + param.n + "." + param.sort + "." + param.filters + "." + param.query;
        var treeData = cacheComplaintList.get(cacheKey);

        return Util.serviceCall({
            service: ObjectListService._queryObjects,
            param: param,
            result: treeData,
            onSuccess: function(data) {
                if (Service.validateComplaintList(data)) {
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
                                nodeType: ObjectService.ObjectTypes.COMPLAINT,
                                nodeNumber: Util.goodValue(doc.name),
                                nodeTitle: Util.goodValue(doc.title_parseable),
                                nodeToolTip: Util.goodValue(doc.title_parseable)
                            };
                        }
                        treeData.docs.push(node);
                    });
                    cacheComplaintList.put(cacheKey, treeData);
                    return treeData;
                }
            }
        });
    }

    /**
     * @ngdoc method
     * @name validateComplaintList
     * @methodOf services:Complaint.ListService
     *
     * @description
     * Validate complaint list data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateComplaintList = function(data) {
        if (!ObjectListService.validateObjects(data)) {
            return false;
        }

        return true;
    };

    return Service;
} ]);
