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
angular.module('services').factory('Case.ListService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ConstantService', 'Object.ListService',
    function ($resource, $translate, Store, Util, Constant, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            CASE_LIST: "CaseList"
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
         *
         * @returns {Object} Promise
         */
        Service.queryCasesTreeData = function (start, n, sort, filters) {
            var cacheCaseList = new Store.CacheFifo(Service.CacheNames.CASE_LIST);
            var cacheKey = start + "." + n + "." + sort + "." + filters;
            var treeData = cacheCaseList.get(cacheKey);

            var param = {};
            param.objectType = "CASE_FILE";
            param.start = start;
            param.n = n;
            param.sort = sort;
            param.filters = filters;
            return Util.serviceCall({
                service: ObjectListService._queryObjects
                , param: param
                , result: treeData
                , onSuccess: function (data) {
                    if (Service.validateCaseList(data)) {
                        treeData = {docs: [], total: data.response.numFound};
                        var docs = data.response.docs;
                        _.forEach(docs, function (doc) {
                            treeData.docs.push({
                                nodeId: Util.goodValue(doc.object_id_s, 0)
                                , nodeType: Constant.ObjectTypes.CASE_FILE
                                , nodeTitle: Util.goodValue(doc.title_parseable)
                                , nodeToolTip: Util.goodValue(doc.title_parseable)
                            });
                        });
                        cacheCaseList.put(cacheKey, treeData);
                        return treeData;
                    }
                }
            });
        }


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
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }

            return true;
        };

        return Service;
    }
]);
