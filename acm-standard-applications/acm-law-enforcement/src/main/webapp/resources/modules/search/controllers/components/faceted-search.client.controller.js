'use strict';

angular.module('search').controller('FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'facetedSearch');
            $scope.$on('component-config', applyConfig);
            $scope.$on('search-query', function(e, searchQuery){
                $scope.searchQuery = searchQuery;
            });

            function applyConfig(e, componentId, config) {
                if (componentId == 'facetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                }
            }

            //label customization sample
            //$scope.customLabels = [{"key": "Case File", "value": "Actions"}
            //    , {"key": "Complaint", "value": "DSA"}
            //    , {"key": "CASE_FILE", "value": "ACTIONS"}
            //    , {"key": "COMPLAINT", "value": "DSA"}
            //];
        }
    ]
);
