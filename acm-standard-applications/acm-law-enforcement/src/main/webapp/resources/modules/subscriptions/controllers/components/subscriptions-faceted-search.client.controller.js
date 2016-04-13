'use strict';

angular.module('subscriptions').controller('Subscriptions.FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'subscriptionsFacetedSearch');
            $scope.$on('component-config', applyConfig);
            function applyConfig(e, componentId, config, user) {
                if (componentId == 'subscriptionsFacetedSearch') {
                    $scope.config = config;
                    var filter = config.filter;
                    $scope.filter = filter.replace("userId", user.userId);
                }
            }
        }
    ]
);
