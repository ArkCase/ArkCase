'use strict';

angular.module('cases').controller('SearchFacetsController', ['$scope',
    function ($scope) {
        $scope.$emit('req-component-config', 'facets');

        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'facets') {
                $scope.config = config;
            }
        }
    }
]);