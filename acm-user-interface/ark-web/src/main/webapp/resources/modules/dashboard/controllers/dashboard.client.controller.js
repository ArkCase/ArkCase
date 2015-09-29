'use strict';

angular.module('dashboard').controller('DashboardController', ['$scope', 'ConfigService', 'Dashboard.DashboardService',
    function ($scope, ConfigService, DashboardService) {
        $scope.config = ConfigService.getModule({moduleId: 'dashboard'});
        $scope.$on('req-component-config', onConfigRequest);

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({}, function (data) {
            $scope.dashboard.model = _.merge(angular.fromJson(data.dashboardConfig),$scope.dashboard.model) ;
            // Set Dashboard custom title
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model)
            });
        });

        /**
         * Handles 'req-component-config' event
         * @param e
         * @param componentId
         */
        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
    }
]);
