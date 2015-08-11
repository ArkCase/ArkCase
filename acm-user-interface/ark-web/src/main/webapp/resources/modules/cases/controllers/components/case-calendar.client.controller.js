'use strict';

angular.module('cases').controller('CaseCalendarController', ['$scope',
	function($scope) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'calendar');
		$scope.config = null;

		function applyConfig(e, componentId, config) {
			if (componentId == 'calendar') {
				$scope.config = config;
			}
		}
	}
]);