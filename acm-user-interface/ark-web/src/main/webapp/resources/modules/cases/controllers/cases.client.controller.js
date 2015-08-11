'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$q','ConfigService',
	function($scope, $q, ConfigService) {
		$scope.config = ConfigService.getModule({moduleId: 'cases'});
		$scope.$on('req-component-config', onConfigRequest);

		$scope.$on('req-select-case', function(e, selectedCase){
			$scope.$broadcast('case-selected', selectedCase);
		});

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);