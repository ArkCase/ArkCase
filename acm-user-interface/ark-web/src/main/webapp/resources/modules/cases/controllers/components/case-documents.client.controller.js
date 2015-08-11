'use strict';

angular.module('cases').controller('CaseDocumentsController', ['$scope',
	function($scope) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'documents');
		$scope.config = null;

		function applyConfig(e, componentId, config) {
			if (componentId == 'documents') {
				$scope.config = config;
			}
		}
	}
]);