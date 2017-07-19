'use strict';

angular.module('dashboard').controller('DashboardController', ['$rootScope', '$scope'
    , 'ConfigService', 'Dashboard.DashboardService'
    , function ($rootScope, $scope
        , ConfigService, DashboardService
    ) {

        // Future code, please do not delete
        // It will be in place after old code is phased out
        //
        // new DashboardHelper.Dashboard({
        //     scope: $scope
        //     , moduleId: "dashboard"
        //     , dashboardName: "DASHBOARD"
        //     , dashboard: {
        //         structure: '6-6',
        //         collapsible: false,
        //         maximizable: false,
        //         model: {
        //             titleTemplateUrl: 'modules/dashboard/templates/widget-title.html',
        //             editTemplateUrl: 'modules/dashboard/templates/dashboard-edit.html',
        //             addTemplateUrl : "modules/dashboard/templates/widget-add.html",
        //             title: ' '
        //         }
        //     }
        //     , onDashboardConfigRetrieved: function(data) {
        //         onDashboardConfigRetrieved(data);
        //     }
        // });




        var promiseConfig = ConfigService.getModuleConfig("dashboard").then(function (moduleConfig) {
            $scope.config = moduleConfig;
            return moduleConfig;
        });

        //Phased out code for backward compatibility.
        //Keep it for now just in case there are still widgets using 'req-component-config'
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            console.log("WARNING: DashboardController 'req-component-config' event is phasing out. Please rewrite codes that depend on the event.");

            promiseConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        //TODO: remove above phased out block, after long enough time for all users to update the code changes


        DashboardService.localeUseTypical($scope);

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-title.html',
                editTemplateUrl: 'modules/dashboard/templates/dashboard-edit.html',
                addTemplateUrl : "modules/dashboard/templates/widget-add.html",
                showUiGridUrl : "modules/dashboard/templates/widget-ui-grid.html",
                title: ' '
            }
        };

        var widgetsPerRoles;
        DashboardService.getConfig({moduleName: "DASHBOARD"}, function (data) {
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
            DashboardService.fixOldCode_removeLater("DASHBOARD", $scope.dashboard.model);
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/templates/dashboard-title.html';
            $scope.dashboard.model.editTemplateUrl = 'modules/dashboard/templates/dashboard-edit.html';
            $scope.dashboard.model.addTemplateUrl = 'modules/dashboard/templates/widget-add.html';
            $scope.dashboard.model.showUiGridUrl = 'modules/dashboard/templates/widget-ui-grid.html';

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
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "DASHBOARD"
            });
            $scope.dashboard.model = model;
        });

    }
]);
