'use strict';

angular.module('search').controller('SearchController', ['$scope', '$stateParams', 'ConfigService', 'SearchService',
	function ($scope, $stateParams, ConfigService, SearchService) {
		$scope.config = ConfigService.getModule({moduleId: 'search'});
		$scope.$on('req-component-config', onConfigRequest);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);