'use strict';

angular.module('subscriptions').controller('Subscriptions.FacetedSearchController', [ '$scope', 'ConfigService', 'UtilService', function($scope, ConfigService, Util) {
    $scope.$emit('req-component-config', 'subscriptionsFacetedSearch');
    $scope.$on('component-config', applyConfig);

    ConfigService.getModuleConfig("common").then(function(moduleConfig) {
        var customization = Util.goodMapValue(moduleConfig, "customization", {});
        if (customization) {
            $scope.customization = customization;
        }
    });

    function applyConfig(e, componentId, config, user) {
        if (componentId == 'subscriptionsFacetedSearch') {
            $scope.config = config;
            var filter = config.filter;
            $scope.filter = filter.replace("userId", user.userId);
        }
    }
} ]);
