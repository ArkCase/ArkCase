'use strict';

angular.module('dashboard').controller('DashboardController', ['$scope', '$translate', 'dashboard', 'ConfigService', 'Dashboard.DashboardService',
    function ($scope, $translate, dashboard, ConfigService, DashboardService) {
        $scope.config = ConfigService.getModule({moduleId: 'dashboard'});
        $scope.$on('req-component-config', onConfigRequest);


        //Update all dashboard widget titles and descriptions
        _.forEach(dashboard.widgets, function (widget, widgetId) {
            widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
            widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        });

        var widgetsPerRoles;

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html',
                editTemplateUrl: 'modules/dashboard/views/dashboard-edit.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "DASHBOARD"}, function (data) {
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);

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

            // Set Dashboard custom title
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

            // Set Dashboard custom editor
            $scope.dashboard.model.editTemplateUrl = 'modules/dashboard/views/dashboard-edit.client.view.html';
        });


        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "DASHBOARD"
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
