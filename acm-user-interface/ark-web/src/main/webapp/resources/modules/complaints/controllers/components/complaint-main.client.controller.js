'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams', '$translate'
    , 'StoreService', 'UtilService', 'ConfigService', 'Complaint.InfoService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $translate
        , Store, Util, ConfigService, ComplaintInfoService, dashboard, DashboardService) {

        $scope.$emit('main-component-started');

        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});

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
            var cacheDashboardConfig = new Store.CacheFifo(ComplaintInfoService.CacheNames.COMPLAINT_INFO);
            $scope.dashboard.complaintModel = cacheDashboardConfig.get("dashboardConfig");

            if ($scope.dashboard.complaintModel) {
                //If cached, use that model
                $scope.dashboard.complaintModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.complaintModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.complaintModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(ComplaintInfoService.CacheNames.COMPLAINT_INFO);
            if (cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);
        });

        //var widgetFilter = function(model) {
        //    var complaintModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < complaintModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < complaintModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(complaintModel.rows[i].columns[j].widgets){
        //                for(var k = complaintModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = complaintModel.rows[i].columns[j].widgets[k].type;
        //                    var type = complaintModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        complaintModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return complaintModel;
        //};

    }
]);