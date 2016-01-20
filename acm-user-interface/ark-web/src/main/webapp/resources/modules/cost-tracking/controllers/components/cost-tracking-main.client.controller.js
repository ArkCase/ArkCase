'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', '$translate', 'dashboard', 'Dashboard.DashboardService',
    'UtilService', 'CostTracking.InfoService', 'ConfigService', 'StoreService',
    function ($scope, $translate, dashboard, DashboardService, Util, CostTrackingInfoService, ConfigService, Store) {

        $scope.$emit('main-component-started');

        ConfigService.getComponentConfig("cost-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

        ConfigService.getModuleConfig("cost-tracking").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            return moduleConfig;
        });

        _.forEach(dashboard.widgets, function (widget, widgetId) {
            widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
            widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        });

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "COST"}, function (data) {
                $scope.dashboard.costModel = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "COST"
            });
        });
    }
]);