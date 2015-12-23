'use strict';

angular.module('cost-tracking').controller('CostTracking.MainController', ['$scope', '$translate', 'dashboard', 'Dashboard.DashboardService',
    'UtilService', 'CostTracking.InfoService', 'ConfigService', 'StoreService',
    function($scope, $translate, dashboard, DashboardService, Util, CostTrackingInfoService, ConfigService, Store) {

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

        DashboardService.getConfig({}, function (data) {
            var cacheDashboardConfig = new Store.CacheFifo(CostTrackingInfoService.CacheNames.COST_SHEETS);
            $scope.dashboard.costModel = cacheDashboardConfig.get("dashboardConfig");

            if($scope.dashboard.costModel) {
                //If cached, use that model
                $scope.dashboard.costModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.costModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.costModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(CostTrackingInfoService.CacheNames.COST_SHEETS);
            if(cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);
        });

        //var widgetFilter = function(model) {
        //    var costModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < costModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < costModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(costModel.rows[i].columns[j].widgets){
        //                for(var k = costModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = costModel.rows[i].columns[j].widgets[k].type;
        //                    var type = costModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        costModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return costModel;
        //};

    }
]);