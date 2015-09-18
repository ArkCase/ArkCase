'use strict';

angular.module('search').controller('Search.FacetsController', ['$scope', 'SearchService', 'ResultService',
    function ($scope, SearchService, ResultService) {
        $scope.$emit('req-component-config', 'facets');
        $scope.facets = [];
        $scope.checkEmpty = function (value) {
            if (value.length > 0) {
                return true;
            }
            else {
                return false;
            }
        };
        $scope.facetSearch = function (name, key) {
        };
        $scope.$on('query-complete', function () {
            $scope.facets = ResultService.data.facet_counts.facet_fields;
        });
    }
]);