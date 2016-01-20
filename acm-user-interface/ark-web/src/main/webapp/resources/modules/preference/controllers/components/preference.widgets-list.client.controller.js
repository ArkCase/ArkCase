'use strict';

angular.module('preference').controller('Preference.WidgetsListController', ['$scope', '$q', '$state', '$stateParams'
    , 'Preference.PreferenceService', 'Dashboard.DashboardService',
    function ($scope, $q, $state, $stateParams, PreferenceService, DashboardService) {
        $scope.widgets = [];

        $scope.formData = ["*", {"type": "submit", "title": "Save"}];

        $scope.selectWidget = selectWidget;
        $scope.toggleWidget = toggleWidget;
        $scope.enableWidget = enableWidget;

        $scope.showDefaultForm = false;
        $scope.toggleDefaultView = toggleDefaultView;
        $scope.defaultViewExpand = "";

        $scope.$on('show-widgets', showWidgets);

        function toggleDefaultView() {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson($scope.preferenceDashboardConfig),
                module: $scope.moduleName,
                isCollapsed: $scope.defaultViewExpand
            });
        }
        /**
         * Toggle widget panel on header click
         * @param widget
         */
        function toggleWidget($event, widget) {
            $event.preventDefault();
            _.forEach($scope.widgets, function (compIter) {
                if (compIter.id != widget.id) {
                    compIter.isCollapsed = true
                }
            });
            widget.isCollapsed = !widget.isCollapsed;
        }

        function enableWidget($event, widget, enable) {
            widget.enabled = enable;
            var enabledWidgets = getEnabledWidgets();
            var preferredWidgets = {moduleName: $scope.moduleName, widgets: enabledWidgets};
            PreferenceService.setPreferredWidgets({preferredWidgets: preferredWidgets});
        }
        var getEnabledWidgets = function() {
            var enabledWidgets = [];
            _.forEach($scope.widgets, function (widget) {
                if(widget.enabled) {
                    enabledWidgets.push(widget.title);
                }

            });
            return enabledWidgets;
        };

        function showWidgets(e, widgets, moduleName, config) {
            $scope.preferenceDashboardConfig = config;
            $scope.moduleName = config.module;
            $scope.defaultViewExpand = config.isCollapsed ? 'true' : 'false';

            var configObject = angular.fromJson(config.dashboardConfig);
            if(configObject.rows[0].columns[0].widgets) {
                PreferenceService.setPrefer
            }

            PreferenceService.getPreferredWidgets({moduleName: $scope.moduleName}, function (preferredWidgets) {
                _.forEach(widgets, function (widget) {
                    widget.isCollapsed = true;
                    //assuming service returns camelCase widget names
                    widget.enabled = _.includes(preferredWidgets, widget.controllerAs);
                });
            }, function(error) {

            });

            $scope.widgets = widgets;
            $scope.showDefaultForm = true;
        }

        function selectWidget(newActive) {
            var prevActive = _.find($scope.widgets, {active: true});
            if (prevActive) {
                prevActive.active = false;
            }
            newActive.active = true;
            $scope.$emit('req-widget-selected', newActive);
        }
    }
]);
