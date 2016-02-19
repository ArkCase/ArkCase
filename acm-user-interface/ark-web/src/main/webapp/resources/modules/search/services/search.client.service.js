'use strict';
/**
 * @ngdoc service
 * @name services:Search.SearchService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/search/services/search.client.service.js modules/search/services/search.client.service.js}
 *
 * The SearchService provides "Faceted Search" REST call functionality
 */
angular.module('search').factory('SearchService', ['$resource', 'UtilService',
    function ($resource, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin/search', {}, {
			/**
             * @ngdoc method
             * @name queryFilteredSearchForUser
             * @methodOf services:Search.SearchService
             *
             * @description
             * Performs "Advanced Search" REST call by supplying default filters
             *
             * @param {String} query Query to send to the server
             * @returns {HttpPromise} Future info about faceted search
             */
			queryFilteredSearchForUser: {
                method: 'GET',
                url: "proxy/arkcase/api/v1/plugin/search/advancedSearch?q=:query&startRow=:start&n=:maxRows",
                cache: true,
                isArray: false,
                transformResponse: function transformReponseForUser(data, headerGetter) {
                    if (Service.validateSolrData(JSON.parse(data))) {
                        var result = {};
                        var searchObj = JSON.parse(data);

                        // Process Faceted fields
                        if (searchObj && searchObj.facet_counts && searchObj.facet_counts.facet_fields) {
                            var fields = searchObj.facet_counts.facet_fields;
                            var newFields = {};
                            _.forEach(fields, function (items, fieldName) {
                                // Convert field's dirty array to the array of structures
                                var newItems = [];
                                for (var i = 0; i < items.length; i += 2) {
                                    if (items[i + 1] > 0) {
                                        newItems.push({
                                            name: items[i],
                                            count: items[i + 1]
                                        });
                                    }
                                }
                                newFields[fieldName] = newItems;
                            });

                            searchObj.facet_counts.facet_fields = newFields;
                        }
                        return searchObj;
                    }
                }
            },
			
            /**
             * @ngdoc method
             * @name queryFilteredSearch
             * @methodOf services:Search.SearchService
             *
             * @description
             * Performs "Faceted Search" REST call by supplying default filters
             *
             * @param {String} query Query to send to the server
             * @returns {HttpPromise} Future info about faceted search
             */
            queryFilteredSearch: {
                method: 'GET',
                url: "proxy/arkcase/api/v1/plugin/search/facetedSearch?q=:query",
                cache: true,
                isArray: false,
                transformResponse: function (data, headerGetter) {
                    if (Service.validateSolrData(JSON.parse(data))) {
                        var result = {};
                        var searchObj = JSON.parse(data);

                        // Process Faceted fields
                        if (searchObj && searchObj.facet_counts && searchObj.facet_counts.facet_fields) {
                            var fields = searchObj.facet_counts.facet_fields;
                            var newFields = {};
                            _.forEach(fields, function (items, fieldName) {
                                // Convert field's dirty array to the array of structures
                                var newItems = [];
                                for (var i = 0; i < items.length; i += 2) {
                                    if (items[i + 1] > 0) {
                                        newItems.push({
                                            name: items[i],
                                            count: items[i + 1]
                                        });
                                    }
                                }
                                newFields[fieldName] = newItems;
                            });

                            searchObj.facet_counts.facet_fields = newFields;
                        }
                        return searchObj;
                    }
                }
            }
        });

        /**
         * @ngdoc method
         * @name validateSolrData
         * @methodOf services:Search.SearchService
         *
         * @description
         * Validate SOLR search data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSolrData = function (data) {
            if (!data) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader) || Util.isEmpty(data.response)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.status)) {
                return false;
            }
//            if (0 != responseHeader.status) {
//                return false;
//            }
            if (Util.isEmpty(data.responseHeader.params)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.params.q)) {
                return false;
            }

            if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                return false;
            }
            if (!Util.isArray(data.response.docs)) {
                return false;
            }
            return true;
        }

        return Service;
    }
]);