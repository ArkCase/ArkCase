'use strict';

angular.module('dashboard').controller('DashboardController', ['$rootScope', '$scope'
    , 'ConfigService', 'Dashboard.DashboardService', 'Helper.DashboardService'
    , function ($rootScope, $scope
        , ConfigService, DashboardService, DashboardHelper
    ) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "dashboard"
            , dashboardName: "DASHBOARD"
            , dashboard: {
                structure: '6-6',
                collapsible: false,
                maximizable: false,
                model: {
                    titleTemplateUrl: 'modules/dashboard/templates/dashboard-title.html',
                    editTemplateUrl: 'modules/dashboard/templates/dashboard-edit.html',
                    addTemplateUrl : "modules/dashboard/templates/widget-add.html",
                    title: ' '
                }
            }
            , onDashboardConfigRetrieved: function(data) {
                onDashboardConfigRetrieved(data);
            }
        });


        var widgetsPerRoles;
        var onDashboardConfigRetrieved = function(data) {
            DashboardService.getWidgetsPerRoles(function (widgets) {
                widgetsPerRoles = widgets;
            });

            $scope.widgetFilter = function (widget, type) {
                var result = false;
                angular.forEach(widgetsPerRoles, function (w) {
                    if (type === w.widgetName) {
                        result = true;
                    }
                });
                return result;
            };
        };

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "DASHBOARD"
            });
            $scope.dashboard.model = model;
        });

    }
]);
