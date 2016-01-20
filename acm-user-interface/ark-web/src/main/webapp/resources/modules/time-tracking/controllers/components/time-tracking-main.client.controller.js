'use strict';

angular.module('time-tracking').controller('TimeTracking.MainController', ['$scope', '$translate'
    , 'StoreService', 'UtilService', 'dashboard', 'Dashboard.DashboardService'
    , 'TimeTracking.InfoService', 'ConfigService'
    , function ($scope, $translate, Store, Util, dashboard, DashboardService
        , TimeTrackingInfoService, ConfigService) {

        $scope.$emit('main-component-started');

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
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            timeModel: {
                titleTemplateUrl: 'modules/dashboard/views/module-dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "TIME"}, function (data) {
            var retModel = angular.fromJson(data.dashboardConfig);
            retModel.titleTemplateUrl = $scope.dashboard.timeModel.titleTemplateUrl;
            retModel.title = "";
            retModel.structure = $scope.dashboard.structure;

            $scope.dashboard.timeModel = retModel;
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "TIME"
            });
        });
    }
]);