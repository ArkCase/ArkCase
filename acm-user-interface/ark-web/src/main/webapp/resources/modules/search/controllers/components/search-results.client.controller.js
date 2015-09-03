'use strict';

angular.module('cases').controller('SearchResultsController', ['$scope', '$stateParams',
    function ($scope, $stateParams) {
        $scope.$emit('req-component-config', 'results');

        $scope.currentId = $stateParams.id;
        $scope.start = 0;
        $scope.pageSize = 10;
        $scope.sort = {by: "", dir: "asc"};
        $scope.filters = [];

        $scope.config = null;
        $scope.gridOptions = {};
        $scope.$on('component-config', applyConfig);
        function applyConfig(e, componentId, config) {
            if (componentId == 'results') {
                $scope.config = config;
            }
        }
    }
]);