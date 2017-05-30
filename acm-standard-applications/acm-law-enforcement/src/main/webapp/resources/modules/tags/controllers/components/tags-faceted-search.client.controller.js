'use strict';

angular.module('tags').controller('Tags.FacetedSearchController', ['$scope', 'ConfigService', 'UtilService',
    function ($scope, ConfigService, Util) {
            $scope.$emit('req-component-config', 'tagsFacetedSearch');
        $scope.$on('component-config', applyConfig);

        ConfigService.getModuleConfig("common").then(function (moduleConfig) {
            var customization = Util.goodMapValue(moduleConfig, "customization", {});
            if (customization) {
                $scope.customization = customization;
            }
        });

            function applyConfig(e, componentId, config) {
                if (componentId == 'tagsFacetedSearch') {
                    $scope.config = config;
                    $scope.filter = config.filter;
                    $scope.multiFilter = config.multiFilter;
                }
            }
        }
    ]
);
