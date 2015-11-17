'use strict';

angular.module('tags').controller('Tags.FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'tagsFacetedSearch');
            $scope.$on('component-config', applyConfig)
            function applyConfig(e, componentId, config) {
                if (componentId == 'tagsFacetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                    $scope.searchQuery = '*';
                }
            }
        }
    ]
);
