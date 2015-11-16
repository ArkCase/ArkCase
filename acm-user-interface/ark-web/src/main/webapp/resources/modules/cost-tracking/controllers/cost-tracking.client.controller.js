'use strict';

angular.module('cost-tracking').controller('CostTrackingController', ['$scope', 'ConfigService',
	function($scope, ConfigService) {
		$scope.config = ConfigService.getModule({moduleId: 'cost-tracking'});
		$scope.$on('req-component-config', onConfigRequest);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);