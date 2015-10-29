'use strict';

angular.module('notifications').controller('NotificationsController', ['$scope', 'ConfigService',
	function($scope, ConfigService) {
		$scope.config = ConfigService.getModule({moduleId: 'notifications'});
		/*$scope.config.$promise.then(function(config){
			$scope.filter = config.filter;
			var componentConfig = _.find(config.components, {id: 'notificationsFacetedSearch'})
			$scope.configured = componentConfig;
		});*/

		$scope.$on('req-component-config', onConfigRequest);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);