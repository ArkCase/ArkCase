'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', '$translate'
    , 'Acm.StoreService', 'UtilService', 'dashboard', 'Dashboard.DashboardService'
    , 'TimeTracking.InfoService', 'ConfigService'
    , function ($scope, $translate, Store, Util, dashboard, DashboardService
        , TimeTrackingInfoService, ConfigService) {

        ConfigService.getComponentConfig("time-tracking", "main").then(function (componentConfig) {
            $scope.config = componentConfig;
            $scope.allowedWidgets = ['details'];
            return componentConfig;
        });

        ConfigService.getModuleConfig("time-tracking").then(function (moduleConfig) {
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
            timeModel: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-blank-title.html'
            }
        };

        DashboardService.getConfig({moduleName: "TIME"}, function (data) {
            $scope.dashboard.timeModel = angular.fromJson(data.dashboardConfig);
            $scope.dashboard.timeModel.titleTemplateUrl = 'modules/dashboard/templates/widget-blank-title.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);