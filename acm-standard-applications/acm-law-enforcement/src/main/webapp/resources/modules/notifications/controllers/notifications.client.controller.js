'use strict';

angular.module('notifications').controller('NotificationsController', ['$scope', '$window', 'ConfigService'
	, 'UtilService', 'Authentication'
	, function($scope, $window, ConfigService, Util, Authentication) {
		$scope.config = ConfigService.getModule({moduleId: 'notifications'});
		$scope.$on('req-component-config', onConfigRequest);

		Authentication.queryUserInfo().then(
			function (userInfo) {
				$scope.user = userInfo;
				return userInfo;
			}
		);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig, $scope.user);
			});
		}
		
        $scope.customization = {
            showParentObject: function(objectData) {
				var url = Util.goodMapValue(objectData, "notification_link_s", "#");
				$window.location.href = url;
            }
        };
        
	}
]);