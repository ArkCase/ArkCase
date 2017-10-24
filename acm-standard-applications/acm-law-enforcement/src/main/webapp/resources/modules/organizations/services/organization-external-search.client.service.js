'use strict';

/**
 * @ngdoc service
 * @name services:Organization.LookupService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/organizations/services/organization-lookup.client.service.js modules/organizations/services/organization-lookup.client.service.js}
 *
 * Organization.LookupService provides functions for Organization database data
 */
angular.module('services').factory('Organization.ExternalService', ['$resource', 'UtilService', '$filter',
    function ($resource, Util, $filter) {
        var Service = $resource('api/latest/plugin/search', {}, {
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
                url: "api/v1/plugin/organizations/search/:organizationId?q=:query",
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
            /*
            if (Util.isEmpty(data.responseHeader.params)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.params.q)) {
                return false;
            }
            */

            if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                return false;
            }
            if (!Util.isArray(data.response.docs)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);