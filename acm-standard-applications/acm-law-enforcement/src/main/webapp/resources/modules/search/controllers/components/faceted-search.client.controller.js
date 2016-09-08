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

            // Customization sample
            //$scope.customization = {
            //    labels: [{"key": "Case File", "value": "Actions"}
            //        , {"key": "Complaint", "value": "DSA"}
            //        , {"key": "CASE_FILE", "value": "ACTIONS"}
            //        , {"key": "COMPLAINT", "value": "DSA"}
            //        , {"key": "Closed", "value": "Archive"}
            //    ]
            //    , showObject: function(objectData) {
            //        console.log("Customize code to show object goes here");
            //    }
            //    , showParentObject: function(objectData) {
            //        console.log("Customize code to show parent object goes here");
            //    }
            //};
        }
    ]
);
