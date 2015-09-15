'use strict';

angular.module('profile').controller('Profile.GroupController', ['$scope', 'ConfigService','userInfoService',
	function($scope, ConfigService, userInfoService) {
		$scope.config = ConfigService.getModule({moduleId: 'profile'});
		$scope.$on('req-component-config', onConfigRequest);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
       userInfoService.getUserInfo().then(function(data) {
            $scope.profileGroups = data.groups;
        });
	}
]);
