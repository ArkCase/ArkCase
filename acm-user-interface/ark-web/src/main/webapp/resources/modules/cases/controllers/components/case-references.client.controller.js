'use strict';

angular.module('cases').controller('CaseReferencesController', ['$scope',
	function($scope) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'references');
		$scope.config = null;

		function applyConfig(e, componentId, config) {
			if (componentId == 'references') {
				$scope.config = config;
			}
		}
	}
]);