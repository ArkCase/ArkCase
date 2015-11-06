'use strict';
/**
 * @ngdoc service
 * @name search.service:SearchService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/search/services/faceted-search.client.service.js modules/search/services/faceted-search.client.service.js}
 *
 * The SearchService provides "Faceted Search" REST call functionality
 */
angular.module('search').factory('SearchService', ['$resource', 'ValidationService',
    function ($resource, ValidationService) {
        return $resource('proxy/arkcase/api/latest/plugin/search', {}, {
            /**
             * @ngdoc method
             * @name queryFilteredSearch
             * @methodOf search.service:SearchService
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
                    if(ValidationService.validateSolrData(JSON.parse(data))){
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
    }
]);