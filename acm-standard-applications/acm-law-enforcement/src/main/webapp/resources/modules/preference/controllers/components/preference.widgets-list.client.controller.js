'use strict';

angular.module('preference').controller('Preference.WidgetsListController', ['$scope', '$q', '$state', '$stateParams'
    , 'Preference.PreferenceService', 'Dashboard.DashboardService', 'dashboard',
    function ($scope, $q, $state, $stateParams, PreferenceService, DashboardService, dashboard) {
        $scope.widgets = [];

        $scope.formData = ["*", {"type": "submit", "title": "Save"}];

        $scope.selectWidget = selectWidget;
        $scope.toggleWidget = toggleWidget;
        $scope.enableWidget = enableWidget;
        $scope.removeNonObjectWidgets = removeNonObjectWidgets;
        $scope.preferenceDashboardWidgetsCopy = _.cloneDeep(dashboard.widgets);
        $scope.showDefaultForm = false;
        $scope.toggleDefaultView = toggleDefaultView;
        $scope.defaultViewExpand = "";

        $scope.$on('show-widgets', showWidgets);

        function toggleDefaultView() {

           var collapsed = ($scope.defaultViewExpand === 'false');

            DashboardService.getConfig({moduleName: $scope.moduleName}, function (config) {
                DashboardService.saveConfig({
                    dashboardConfig: config.dashboardConfig,
                    module: $scope.moduleName,
                    collapsed: collapsed
                });
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

        function removeNonObjectWidgets(arrayOfWidgets) {
            var retval = arrayOfWidgets;
            _.remove(retval, function (widgetName) {
                if (!_.includes($scope.objectWidgets, widgetName)) {
                    return true;
                }
            });
            return retval;
        }

        function filterObjectWidgetsByModule(preferenceConfig, widgets) {
            var retval = [];
            var allowedWidgets = (_.find(preferenceConfig.moduleWidgetPreferences, {name: $scope.moduleName})).allowedWidgets;
            _.forEach(widgets, function (widget) {
                if (widget.commonName) {
                    if (_.includes(allowedWidgets, widget.commonName)) {
                        retval.push(widget);
                    }
                }
            });
            return retval;
        }

        function enableWidget($event, widget, enable) {
            PreferenceService.getPreferredWidgets({moduleName: $scope.moduleName}, function (preferredWidgets) {
                if (enable) {
                    if (!_.includes(preferredWidgets.preferredWidgets, widget.commonName)) {
                        preferredWidgets.preferredWidgets.push(widget.commonName);
                    }
                } else {
                    _.remove(preferredWidgets.preferredWidgets, function (prefWidget) {
                        return prefWidget == widget.commonName;
                    });
                }

                var enabledWidgets = preferredWidgets.preferredWidgets;
                enabledWidgets = removeNonObjectWidgets(enabledWidgets);
                PreferenceService.setPreferredWidgets({
                    moduleName: $scope.moduleName,
                    preferredWidgets: enabledWidgets
                });
                widget.enabled = enable;

                //set appropriate dashboardConfig to make note of appropriate widget
                DashboardService.getConfig({moduleName: $scope.moduleName}, function (config) {
                    var model = angular.fromJson(config.dashboardConfig);
                    model.rows[0].columns[0].widgets = [];
                    var widgetsToInsert = [];
                    _.forEach(enabledWidgets, function (widgetName) {
                        var widgetToInsert = createWidgetStructure(widgetName);
                        widgetsToInsert.push(widgetToInsert);
                    });

                    var orderedWidgetList = (_.find($scope.preferenceConfig.moduleWidgetPreferences, {name: $scope.moduleName})).allowedWidgets;
                    _.forEach(orderedWidgetList, function (widget) {
                        var found = _.find(widgetsToInsert, {type: widget});
                        if (found) {
                            model.rows[0].columns[0].widgets.push(found);
                        }
                    });


                    DashboardService.saveConfig({
                        dashboardConfig: angular.toJson(model),
                        module: $scope.moduleName
                    });
                })
            });
        }

        function createWidgetStructure(widgetName) {
            var widgetToInsert = {};
            var widgetInfo = _.find($scope.preferenceDashboardWidgetsCopy, {commonName: widgetName});
            widgetToInsert.type = widgetInfo.commonName;
            widgetToInsert.config = {};
            widgetToInsert.title = widgetInfo.title;
            widgetToInsert.titleTemplateUrl = "../src/templates/widget-title.html";
            return widgetToInsert;
        }

        function showWidgets(e, widgets, moduleDashboardConfig, objectWidgets, preferenceConfig) {
            $scope.objectWidgets = objectWidgets;
            $scope.moduleName = moduleDashboardConfig.module;
            $scope.defaultViewExpand = (moduleDashboardConfig.collapsed).toString();
            $scope.preferenceConfig = preferenceConfig;
            widgets = filterObjectWidgetsByModule(preferenceConfig, widgets);

            PreferenceService.getPreferredWidgets({moduleName: $scope.moduleName}, function (preferredWidgets) {
                preferredWidgets.preferredWidgets = removeNonObjectWidgets(preferredWidgets.preferredWidgets);
                _.forEach(widgets, function (widget) {
                    widget.isCollapsed = true;
                    widget.enabled = _.includes(preferredWidgets.preferredWidgets, widget.commonName);
                });
                $scope.widgets = widgets;
            }, function (error) {
            });
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