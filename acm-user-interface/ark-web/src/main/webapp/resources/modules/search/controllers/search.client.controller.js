'use strict';

angular.module('search').controller('SearchController', ['$scope', 'ConfigService', '$stateParams',
	function($scope, ConfigService, $stateParams) {
		$scope.config = ConfigService.getModule({moduleId: 'search'});
		$scope.$on('req-component-config', onConfigRequest);

		$scope.searchQuery = $stateParams['query'] ? $stateParams['query'] : '';
		$scope.$broadcast('search-query', $scope.searchQuery);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);