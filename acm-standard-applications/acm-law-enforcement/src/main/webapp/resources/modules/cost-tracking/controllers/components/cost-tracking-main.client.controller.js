'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', '$translate', 'dashboard', 'Dashboard.DashboardService',
    'UtilService', 'CostTracking.InfoService', 'ConfigService', 'Acm.StoreService',
    function ($scope, $translate, dashboard, DashboardService, Util, CostTrackingInfoService, ConfigService, Store) {

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
            structure: '12',
            collapsible: false,
            maximizable: false,
            costModel: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-blank-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "COST"}, function (data) {
            $scope.dashboard.costModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.costModel.titleTemplateUrl = 'modules/dashboard/templates/widget-blank-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);