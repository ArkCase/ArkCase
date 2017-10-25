'use strict';

/**
 * @ngdoc service
 * @name services:Organization.SearchService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/organizations/services/organization-search.client.service.js modules/organizations/services/organization-search.client.service.js}
 *
 * Organization.SearchService provides functions for Organization database data
 */
angular.module('services').factory('Organization.SearchService', ['$resource', 'UtilService',
    function ($resource, Util) {
        var Service = $resource('api/latest/plugin/search', {}, {
            /**
             * @ngdoc method
             * @name queryFilteredSearch
             * @methodOf services:Organization.SearchService
             *
             * @description
             * Performs "Search by organization id which exclude the organization itself and it's ancestors" REST call by supplying default filters
             *
             * @param {String} query Query to send to the server
             * @returns {HttpPromise} Future info about faceted search
             */
            queryFilteredSearch: {
                method: 'GET',
                url: "api/v1/plugin/organizations/search/:organizationId?q=:query",
                cache: false,
                isArray: false,
                transformResponse: Util.transformSearchResponse
            }
        });




        return Service;
    }
]);