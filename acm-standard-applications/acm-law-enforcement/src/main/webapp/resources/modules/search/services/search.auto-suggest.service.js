'use strict';
/**
 * @ngdoc service
 * @name reports.service:Search.AutoSuggestService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/search/services/search.auto-suggest.service.js}
 *
 * The AutoSuggestService provides autosuggest search functionality.
 */
angular.module('search').factory('Search.AutoSuggestService', ['$q', '$http', 'Search.QueryBuilderService', 'SearchService',
    function ($q, $http, SearchQueryBuilder, SearchService) {
        return {

            /**
             * @ngdoc method
             * @name autoSuggest
             * @methodOf services:Search.AutoSuggestService
             *
             * @description
             * This function performs autosuggest searching
             *
             * @param {String} autoSuggestQuery Contains the query for witch autosuggest list should be prepared
             * @param {String} core Contains the solr Core against autosuggest query will be executed.
             * @param {String} autoSuggestObjectType is used for filtering the autosuggest results
             * @returns {HttpPromise} Future array of autosuggest data
             */
            autoSuggest: function (autoSuggestQuery, core, autoSuggestObjectType) {
                var deferred = $q.defer();
                if (autoSuggestObjectType != null) {
                    SearchService.queryAutoSuggestSearch({
                            query: autoSuggestQuery,
                            filter: "object_type_s:" + autoSuggestObjectType,
                            core: core
                        },
                        function (res) {
                            deferred.resolve(res.response.docs);
                        });
                    return deferred.promise;
                } else {
                    SearchService.queryAutoSuggestSearchNoFilters({
                            query: autoSuggestQuery,
                            core: core
                        },
                        function (res) {
                            deferred.resolve(res.response.docs);
                        });
                    return deferred.promise;
                }
            }
        }
    }]);