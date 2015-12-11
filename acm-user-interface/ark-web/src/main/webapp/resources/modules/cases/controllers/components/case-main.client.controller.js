'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$translate', 'dashboard', 'ConfigService', 'Dashboard.DashboardService',
	function($scope, $translate, dashboard, ConfigService, DashboardService) {
		ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
			$scope.components = moduleConfig.components;
			$scope.config = _.find(moduleConfig.components, {id: "main"});
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
		}

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
	}
]);