'use strict';

angular.module('cases').controller('Cases.CalendarController', ['$scope', 'ConfigService',
	function ($scope, ConfigService) {
		//$scope.$emit('req-component-config', 'calendar');
		//$scope.$on('component-config', function (e, componentId, config) {
		//if (componentId == 'calendar') {
		//	$scope.config = config;
		//}
		//});
		ConfigService.getComponentConfig("cases", "calendar").then(function (componentConfig) {
			$scope.config = componentConfig;
			return componentConfig;
		});
	}
]);