'use strict';
/**
 * @ngdoc service
 * @name reports.service:Tags.TagsService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/tags/services/tags.client.service.js modules/tags/services/tags.client.service.js}
 *
 * The ClientsService provides clients  search functionality.
 */
angular.module('tags').factory('Tags.TagsService', ['$q', '$http', 'Search.QueryBuilderService', 'SearchService',
    function ($q, $http, SearchQueryBuilder, SearchService) {
        return {

            /**
             * @ngdoc method
             * @name searchTags
             * @methodOf services:Tags.TagsService
             *
             * @description
             * This function performs tags searching
             *
             * @param {Object} searchParams Contains search parameters
             * @param {String} searchParams.query Search query string
             * @param {String} searchParams.filter Filter query string
             * @param {Number} searchParams.n Number of returned tags
             * @returns {HttpPromise} Future array of tags
             */
            searchTags: function (searchParams) {
                var deferred = $q.defer();
                if (searchParams.query) {
                    var query = searchParams.query.replace('*', '');
                    query = '/' + query + '.*/';
                    var filters = searchParams.filter
                    var start = 0;
                    var n = searchParams.n || 10;

                    var query = SearchQueryBuilder.buildFacetedSearchQuery(
                        query,
                        filters,
                        n, start
                    );

                    if (query) {
                        SearchService.queryFilteredSearch({
                                query: query
                            },
                            function (data) {
                                deferred.resolve(data.response.docs);
                            },
                            function () {
                                deferred.reject();
                            }
                        );
                    }
                }

                return deferred.promise;
            }
        }
    }
]);