'use strict';

angular.module('notifications').controller('NotificationsController', ['$scope', 'ConfigService', 'Authentication','UtilService'
    , 'ObjectService'
	, function($scope, ConfigService, Authentication, Util, ObjectService) {
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
                var objectTypeKey = Util.goodMapValue(objectData, "parent_type_s");
                var objectId = Util.goodMapValue(objectData, "parent_id_s");
                ObjectService.showObject(objectTypeKey, objectId);
            }
        };
        
	}
]);