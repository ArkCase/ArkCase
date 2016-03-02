'use strict';

angular.module('notifications').controller('Notifications.FacetedSearchController', ['$scope',
        function ($scope) {
            $scope.$emit('req-component-config', 'notificationsFacetedSearch');
            $scope.$on('component-config', applyConfig)
            function applyConfig(e, componentId, config) {
                if (componentId == 'notificationsFacetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                }
            }
        }
    ]
);
