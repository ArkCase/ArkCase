'use strict';

angular.module('preference').controller('Preference.Controller', [ '$scope', 'ConfigService', '$modal', function($scope, ConfigService, $modal) {
    $scope.config = ConfigService.getModule({
        moduleId: 'preference'
    });
    $scope.$on('req-widget-config', onConfigRequest);

    $scope.$on('req-module-selected', moduleSelected);

    function moduleSelected(e, moduleId) {
        ConfigService.getModuleConfig('dashboard').then(function(dashboardConfig) {
            $scope.$broadcast('module-selected', moduleId, dashboardConfig, $scope.config);
        });
    }

    function onConfigRequest(e, widgetId) {
        $scope.config.$promise.then(function(config) {
            var widgetConfig = _.find(config.widgets, {
                id: widgetId
            });
            $scope.$broadcast('widget-config', widgetId, widgetConfig);
        });
    }
} ]);
