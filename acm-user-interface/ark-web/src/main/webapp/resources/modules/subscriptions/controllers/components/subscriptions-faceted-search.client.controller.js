'use strict';

angular.module('subscriptions').controller('Subscriptions.FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'subscriptionsFacetedSearch');
            $scope.$on('component-config', applyConfig)
            function applyConfig(e, componentId, config) {
                if (componentId == 'subscriptionsFacetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                }
            }
        }
    ]
);
