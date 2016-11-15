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
angular.module('search').factory('SearchService', ['$resource', 'UtilService', '$filter',
    function ($resource, Util, $filter) {
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
                url: "api/v1/plugin/search/facetedSearch?q=:query",
                cache: false,
                isArray: false,
                transformResponse: function (data, headerGetter) {
                    if (Service.validateSolrData(JSON.parse(data))) {
                        var result = {};
                        var searchObj = JSON.parse(data);
                        var filter = $filter('capitalizeFirst');

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
                                            //name: filter(items[i]),
                                            name: {
                                                nameValue: items[i],
                                                nameFiltered: filter(items[i])
                                            },
                                            count: items[i + 1]
                                        });
                                    }
                                }
                                newFields[fieldName] = newItems;
                            });

                            searchObj.facet_counts.facet_fields = newFields;
                        }
                        // Process queries for faceting date range
                        if (searchObj && searchObj.facet_counts && searchObj.facet_counts.facet_queries) {
                            var fields = searchObj.facet_counts.facet_queries;
                            var splitField;
                            var dateRangeType;
                            var dateRangeValue;

                            _.forEach(fields, function (count, fieldName) {
                                if (count > 0) {
                                    splitField = fieldName.split(",");
                                    dateRangeType = filter(splitField[0]);
                                    dateRangeValue = filter(splitField[1]);

                                    if (!newFields[dateRangeType]) {
                                        newFields[dateRangeType] = [];
                                        newFields[dateRangeType].push({
                                            name: {
                                                nameValue: dateRangeValue,
                                                nameFiltered: filter(dateRangeValue)
                                            },
                                            count: count
                                        });
                                    }
                                    else {
                                        newFields[dateRangeType].push({
                                            name: {
                                                nameValue: dateRangeValue,
                                                nameFiltered: filter(dateRangeValue)
                                            },
                                            count: count
                                        });
                                    }
                                }
                            });
                            searchObj.facet_counts.facet_fields = newFields;
                        }
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
            queryAutoSuggestSearch: {
                method: 'GET',
                url: "api/v1/plugin/search/suggest?q=:query&core=:core&filter=:filter",
                cache: false,
                isArray: false,
                transformResponse: function (data, headerGetter) {
                    var searchObj = JSON.parse(data);
                    return searchObj;
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
        };

        /**
         * @ngdoc method
         * @name exportUrl
         * @methodOf services:Search
         *
         * @description
         * Returns export url for the current search query
         *
         * @param {String} query Query to send to the server
         * @param {Array} fields Array of fields to be exported (if empty omitted)
         * @param {String} exportType Type of export (csv)
         * @param {String} reportName The file name of the generated report
         * @returns {String} The export URL
         */
        Service.exportUrl = function (query, fields, exportType, reportName) {
            var url = "api/v1/plugin/search/facetedSearch?q=" + query;
            if (fields instanceof Array && fields.length > 0) {
                url += "&fields=" + fields.join(',');
            }
            url += "&export=" + exportType;
            url += "&reportName=" + reportName;
            return url;
        };

        return Service;
    }
]);
