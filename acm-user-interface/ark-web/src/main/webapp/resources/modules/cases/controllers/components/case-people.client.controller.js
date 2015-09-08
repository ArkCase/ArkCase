'use strict';

angular.module('cases').controller('Cases.PeopleController', ['$scope',
	function($scope) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'people');
		$scope.config = null;

		function applyConfig(e, componentId, config) {
			if (componentId == 'people') {
				$scope.config = config;
			}
		}
	}
]);