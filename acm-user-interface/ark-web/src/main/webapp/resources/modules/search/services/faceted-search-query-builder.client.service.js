'use strict';
/**
 * @ngdoc service
 * @name search.service:Search.QueryBuilderService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/search/services/faceted-search-query-builder.client.service.js modules/search/services/faceted-search-query-builder.client.service.js}
 *
 * The Search.QueryBuilderService provides functions to build service URL using passed parameters
 */
angular.module('search').factory('Search.QueryBuilderService', [
    function () {
        return {

            /**
             * @ngdoc method
             * @name buildFacetedSearchQuery
             * @methodOf search.service:Search.QueryBuilderService
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
                return (filters ? (input + "&filters=fq=" + filters + "&n=" + n + "&start=" + start) : (input + "&n=" + n + "&start=" + start));
            }
        }
    }
]);