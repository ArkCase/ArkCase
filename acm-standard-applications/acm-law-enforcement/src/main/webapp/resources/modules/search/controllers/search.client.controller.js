'use strict';

angular.module('search').controller('SearchController', [ '$scope', 'ConfigService', '$stateParams', function($scope, ConfigService, $stateParams) {

    ConfigService.getModuleConfig("search").then(function(config) {
        $scope.config = config;
        return config;
    });

    $scope.searchQuery = $stateParams['query'] ? $stateParams['query'] : '';
    var isSelected = $stateParams['isSelected'] ? $stateParams['isSelected'] : false;
    var searchQuery = new Object();
    searchQuery.searchQuery = $scope.searchQuery;
    searchQuery.isSelected = isSelected;
    $scope.$broadcast('search-query', $scope.searchQuery);

} ]);