'use strict';

/**
 * @ngdoc service
 * @name services:Consultation.ListService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/consultations/services/consultation-list.client.service.js modules/consultations/services/consultation-list.client.service.js}
 *
 * Consultation.ListService provides functions for Consultation database data
 */
angular.module('services').factory('Consultation.ListService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'ObjectService', 'Object.ListService', function($resource, $translate, Store, Util, ObjectService, ObjectListService) {
    var Service = $resource('api/latest/plugin', {}, {});
    

    /**
     * @ngdoc method
     * @name queryConsultationsTreeData
     * @methodOf services:Consultation.ListService
     *
     * @description
     * Query list of consultations from SOLR, pack result for Object Tree.
     *
     * @param {Number} start  Zero based index of result starts from
     * @param {Number} n max Number of list to return
     * @param {String} sort  Sort value. Allowed choice is based on backend specification
     * @param {String} filters  Filter value. Allowed choice is based on backend specification
     * @param {String} query  Search term for tree entry to match
     *
     * @returns {Object} Promise
     */
    Service.queryConsultationsTreeData = function(start, n, sort, filters, query, nodeMaker) {
        var param = {};
        param.objectType = "CONSULTATION";
        param.start = Util.goodValue(start, 0);
        param.n = Util.goodValue(n, 32);
        param.sort = Util.goodValue(sort);
        param.filters = Util.goodValue(filters);
        param.query = Util.goodValue(query);

        return Util.serviceCall({
            service: ObjectListService._queryObjects,
            param: param,
            onSuccess: function(data) {
                if (Service.validateConsultationList(data)) {
                    var treeData = {
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
                                nodeType: ObjectService.ObjectTypes.CONSULTATION,
                                nodeNumber: Util.goodValue(doc.name),
                                nodeTitle: Util.goodValue(doc.title_parseable_lcs),
                                nodeToolTip: Util.goodValue(doc.title_parseable_lcs)
                            };
                        }
                        treeData.docs.push(node);
                    });
                    return treeData;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name updateConsultationsTreeData
     * @methodOf services:Consultation.ListService
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
    Service.updateConsultationsTreeData = function (start, n, sort, filters, query, nodeData) {

    };

    /**
     * @ngdoc method
     * @name resetConsultationsTreeData
     * @methodOf services:Consultation.ListService
     *
     * @description
     * Reset tree to initial state, including empty tree data
     *
     * @returns None
     */
    Service.resetConsultationsTreeData = function () {

    };

    /**
     * @ngdoc method
     * @name validateConsultationList
     * @methodOf services:Consultation.ListService
     *
     * @description
     * Validate consultation list data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateConsultationList = function(data) {
        if (!ObjectListService.validateObjects(data)) {
            return false;
        }

        return true;
    };

    return Service;
} ]);