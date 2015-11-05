'use strict';

angular.module('search').controller('FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'facetedSearch');
            $scope.$on('component-config', applyConfig)
            function applyConfig(e, componentId, config) {
                if (componentId == 'facetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                }
            }
        }
    ]
);
