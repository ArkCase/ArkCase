'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.ListService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/complaints/services/complaint-info.client.service.js modules/complaints/services/complaint-info.client.service.js}
 *
 * Complaint.ListService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.ListService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ConstantService', 'Object.ListService',
    function ($resource, $translate, Store, Util, Constant, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            COMPLAINT_LIST: "ComplaintList"
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
         *
         * @returns {Object} Promise
         */
        Service.queryComplaintsTreeData = function (start, n, sort, filters) {
            var cacheComplaintList = new Store.CacheFifo(Service.CacheNames.COMPLAINT_LIST);
            var cacheKey = start + "." + n + "." + sort + "." + filters;
            var treeData = cacheComplaintList.get(cacheKey);

            var param = {};
            param.objectType = "COMPLAINT";
            param.start = start;
            param.n = n;
            param.sort = sort;
            param.filters = filters;
            return Util.serviceCall({
                service: ObjectListService._queryObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateComplaintList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: Constant.ObjectTypes.COMPLAINT
                                , nodeTitle: Util.goodValue(doc.title_parseable)
                                , nodeToolTip: Util.goodValue(doc.title_parseable)
                            });
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
        Service.validateComplaintList = function (data) {
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);
