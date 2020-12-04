'use strict';

angular.module('search').controller('FacetedSearchController', [ '$scope', 'ConfigService', 'UtilService', function($scope, ConfigService, Util) {

    ConfigService.getComponentConfig("search", "facetedSearch").then(function(config) {
        $scope.config = config;
        $scope.filter = config.filter;
        return config;
    });

    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
        var customization = Util.goodMapValue(moduleConfig, "customization", {});
        if (customization) {
            $scope.customization = customization;
        }
    });

    $scope.$on('search-query', function(e, searchQuery) {
        $scope.searchQuery = searchQuery;
    });

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

]);
