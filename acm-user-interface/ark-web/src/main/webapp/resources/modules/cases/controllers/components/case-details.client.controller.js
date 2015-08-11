'use strict';

angular.module('cases').controller('CaseDetailsController', ['$scope', '$stateParams', 'CasesService',
	function($scope, $stateParams, CasesService) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'details');
		$scope.config = null;

		function applyConfig(e, componentId, config) {
			if (componentId == 'details') {
				$scope.config = config;
			}
		}

		var id = $stateParams.id;
		CasesService.get({
			id: id
		}, function(data) {
			$scope.details = data.details;
		})
	}
]);