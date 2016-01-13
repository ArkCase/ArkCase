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
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "TIME_TRACKING"}, function (data) {
            var cacheDashboardConfig = new Store.CacheFifo(TimeTrackingInfoService.CacheNames.TIMESHEET_INFO);
            $scope.dashboard.timeModel = cacheDashboardConfig.get("dashboardConfig");

            if ($scope.dashboard.timeModel) {
                //If cached, use that model
                $scope.dashboard.timeModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.timeModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.timeModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(TimeTrackingInfoService.CacheNames.TIMESHEET_INFO);
            if (cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);

            //jwu:
            //Todo: fix above cache
            //XXX_INFO cache is used to save info data, not config data. The right place to save the
            //dashboard config in cache is when the config is first time retrieved (presumably in a service)
            //
        });

        //var widgetFilter = function(model) {
        //    var timeModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < timeModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < timeModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(timeModel.rows[i].columns[j].widgets){
        //                for(var k = timeModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = timeModel.rows[i].columns[j].widgets[k].type;
        //                    var type = timeModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        timeModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return timeModel;
        //};
    }
]);