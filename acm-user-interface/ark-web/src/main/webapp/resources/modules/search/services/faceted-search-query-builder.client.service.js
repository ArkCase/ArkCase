'use strict';

angular.module('services').factory('Search.QueryBuilderService', [
    function () {
        return {
            buildFacetedSearchQuery: function (input, filters, n, start) {
                return (filters ? (input + "&filters=fq=" + filters + "&n=" + n + "&start=" + start) : (input + "&n=" + n + "&start=" + start));
            }
        }
    }
]);