'use strict';
/**
 * @ngdoc service
 * @name services:Search.SearchService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/search/services/search.client.service.js modules/search/services/search.client.service.js}
 *
 * The SearchService provides "Faceted Search" REST call functionality
 */
angular.module('search').factory('SearchService', [ '$resource', 'UtilService', '$filter', function($resource, Util, $filter) {
    var Service = $resource('api/latest/plugin/search', {}, {
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
            url: "api/v1/plugin/search/advancedSearch?q=:query&start=:start&n=:maxRows",
            cache: false,
            isArray: false,
            transformResponse: function transformReponseForUser(data, headerGetter) {
                if (Util.validateSolrData(JSON.parse(data))) {
                    var result = {};
                    var searchObj = JSON.parse(data);
                    // Process Faceted fields
                    if (searchObj && searchObj.facet_counts && searchObj.facet_counts.facet_fields) {
                        var fields = searchObj.facet_counts.facet_fields;
                        var newFields = {};
                        _.forEach(fields, function(items, fieldName) {
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
         * Performs "Faceted Search" REST call by supplying default filters. The query will be Solr escaped on the server.
         *
         * @param {String} query Query to send to the server
         * @returns {HttpPromise} Future info about faceted search
         */
        queryFilteredSearch: {
            method: 'GET',
            url: "api/v2/plugin/search/facetedSearch?q=:query",
            cache: false,
            isArray: false,
            transformResponse: Util.transformSearchResponse
        },

        /**
         * @ngdoc method
         * @name unescapedQueryFilteredSearch
         * @methodOf services:Search.SearchService
         *
         * @description
         * Performs "Faceted Search" REST call by supplying default filters. The query will NOT be Solr escaped on the server.
         *
         * @param {String} unescapedQuery Query to send to the server
         * @returns {HttpPromise} Future info about faceted search
         */
        unescapedQueryFilteredSearch: {
            method: 'GET',
            url: "api/v1/plugin/search/facetedSearch?unescapedQuery=:unescapedQuery",
            cache: false,
            isArray: false,
            transformResponse: Util.transformSearchResponse
        },

        /**
         * @ngdoc method
         * @name queryAutoSuggestSearch
         * @methodOf services:Search.SearchService
         *
         * @description
         * Performs "Auto-Suggest Search" REST call by supplying default filters
         *
         * @param {String} query Query to send to the server
         * @returns {HttpPromise} Future info about auto-suggest search
         */
        queryAutoSuggestSearch: {
            method: 'GET',
            url: "api/v1/plugin/search/suggest?q=:query&core=:core&filter=:filter",
            cache: false,
            isArray: false,
            transformResponse: function(data, headerGetter) {
                var searchObj = JSON.parse(data);
                if (Util.validateSolrData(searchObj)) {
                    return searchObj;
                }
            }
        },

        /**
         * @ngdoc method
         * @name queryAutoSuggestSearch
         * @methodOf services:Search.SearchService
         *
         * @description
         * Performs "Auto-Suggest Search" REST call by supplying default filters
         *
         * @param {String} query Query to send to the server
         * @returns {HttpPromise} Future info about auto-suggest search
         */
        queryAutoSuggestSearchNoFilters: {
            method: 'GET',
            url: "api/v1/plugin/search/suggest?q=:query&core=:core",
            cache: false,
            isArray: false,
            transformResponse: function(data, headerGetter) {
                var searchObj = JSON.parse(data);
                if (Util.validateSolrData(searchObj)) {
                    return searchObj;
                }
            }
        },

        /**
         * @ngdoc method
         * @name querySimpleSearch
         * @methodOf services:Search.SearchService
         *
         * @description
         * Performs a very basic search with a simple query call to the advancedSearch API
         * Good for simple queries
         *
         * @param {String} query Query to send to the server, no added facets
         * @returns {HttpPronmise} Future info about advanced search
         */
        querySimpleSearch: {
            method: 'GET',
            url: "api/v1/plugin/search/advancedSearch?q=:query",
            cache: false,
            isArray: false,
            transformResponse: function(data, headerGetter) {
                var searchObj = JSON.parse(data);
                if (Util.validateSolrData(searchObj)) {
                    return searchObj;
                }
            }
        }
    });

    /**
     * @ngdoc method
     * @name exportUrl
     * @methodOf services:Search
     *
     * @description
     * Returns export url for the current search query
     *
     * @param {String} query Query to send to the server
     * @param {String} exportType Type of export (csv)
     * @param {String} reportName The file name of the generated report
     * @param {Array} fields Array of fields to be exported (if empty omitted)
     * @param {Array} titles Array of titles to be used (if empty omitted)
     * @returns {String} The export URL
     */
    Service.exportUrl = function(query, exportType, reportName, fields, titles) {
        var url = "api/v2/plugin/search/facetedSearch?q=" + query;
        if (fields instanceof Array && fields.length > 0) {
            url += "&fields=" + fields.join(',');
            url += "&titles=" + encodeURI(titles.join(','));
        }
        url += "&export=" + exportType;
        url += "&reportName=" + reportName;
        //add time-zone in exportUrl for AFDP-5769
        var timeZone = new Date().getTimezoneOffset();
        url += "&timeZone=" + encodeURI(timeZone.toString());
        return url;
    };

    return Service;
} ]);
