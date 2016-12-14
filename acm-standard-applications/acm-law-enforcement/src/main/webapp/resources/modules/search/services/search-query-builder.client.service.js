'use strict';
/**
 * @ngdoc service
 * @name service:Search.QueryBuilderService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/search/services/search-query-builder.client.service.js modules/search/services/search-query-builder.client.service.js}
 *
 * The Search.QueryBuilderService provides functions to build service URL using passed parameters
 */
angular.module('search').factory('Search.QueryBuilderService', [
    function () {
        return {

            /**
             * @ngdoc method
             * @name buildFacetedSearchQuery
             * @methodOf service:Search.QueryBuilderService
             *
             * @description
             * Builds URL to pass to the faceted search service
             *
             * @param {String} input Search term input by user
             * @param {String} filters Filters to send to the faceted search service
             * @param {String} n Total row count
             * @param {String} start Starting row count
             * @returns {HttpPromise} Future info about cancel status
             */
            buildFacetedSearchQuery: function (input, filters, n, start) {
                return (filters ? (input + "&filters=" + encodeURIComponent(filters) + "&n=" + n + "&start=" + start) : (input + "&n=" + n + "&start=" + start));
            },

            /**
             * @ngdoc method
             * @name buildSafeFqFacetedSearchQuery
             * @methodOf service:Search.QueryBuilderService
             *
             * @description
             * Builds URL to pass to the faceted search service.
             * It doesn't perform whole uri encoding, only encodes '&fq='. Some requests have
             *
             * @param {String} input Search term input by user
             * @param {String} filters Filters to send to the faceted search service
             * @param {String} n Total row count
             * @param {String} start Starting row count
             * @returns {HttpPromise} Future info about cancel status
             */
            buildSafeFqFacetedSearchQuery: function (input, filters, n, start) {
                return (filters ? (input + "&filters=" + filters.replace(/&fq=/gi, '%26fq%3D') + "&n=" + n + "&start=" + start) : (input + "&n=" + n + "&start=" + start));
            }
        }
    }
])
;