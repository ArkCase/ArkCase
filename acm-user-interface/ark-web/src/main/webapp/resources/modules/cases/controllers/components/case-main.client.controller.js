'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$translate', 'dashboard', 'UtilService', 'ConfigService', 'Case.InfoService', 'Dashboard.DashboardService'
    , function ($scope, $translate, dashboard, Util, ConfigService, CaseInfoService, DashboardService) {
        //$scope.$emit('req-component-config', 'main');
        //$scope.$on('component-config', function (e, componentId, config) {
        //	if (componentId == 'main') {
        //		$scope.config = config;
        //	}
        //});

        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
			$scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            $scope.widgets = [];
            //_.each(Util.goodMapValue($scope.config, "widgets", []), function (widget) {
            //    if ("component" == Util.goodValue(item.type)) {
            //        var item = {};
            //        item.id = widget.id;
            //        item.type = widget.type;
            //        var found = _.find(moduleConfig.components, {id: widget.id});
            //        if (found) {
            //            item.title = found.title;
            //            $scope.widgets.push(item);
            //        }
            //    }
            //});

            $scope.widgets = Util.goodMapValue($scope.config, "widgets", []).filter(function (widget) {
                return "component" == widget.type;
            });
			return moduleConfig;
		});

        _.forEach(dashboard.widgets, function(widget, widgetId) {
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
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);

            // Set Dashboard custom title
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model)
            });
        });

        //ConfigService.getComponentConfig("cases", "main").then(function (componentConfig) {
        //    var a1 = componentConfig;
        //    var a2 = $scope.config;
        //
        //    return componentConfig;
        //});


        $scope.$on('case-updated', function (e, data) {
            if (!CaseInfoService.validateCaseInfo(data)) {
                return;
            }
            $scope.caseInfo = data;

            promiseConfig.then(function (moduleConfig) {
                _.each($scope.widgets, function (widget) {
                    var z = 1;
                });

                $scope["details"] = "11111";
                $scope["peopleNames"] = "22222";
                $scope["documentCount"] = "33333";
                $scope["participants"] = "44444";
                $scope["noteCount"] = "55555";
                $scope["taskCount"] = "66666";
                $scope["referenceCount"] = "rrrrr";
                $scope["historyCount"] = "hhhhh";
                $scope["correspondenceCount"] = "cccccc";
                $scope["timesheetCount"] = "timetttt";
                $scope["costsheetCount"] = "costcccc";
                $scope["calendarEventCount"] = "calllll";

                return moduleConfig;
            });
        });
	}
]);

