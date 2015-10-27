'use strict';

angular.module('notifications').controller('notify', ['$scope', 
    function ($scope) {
        
        $scope.$on('req-component-config', onConfigRequest);

        
        $scope.config = ConfigService.getModule({moduleId: 'notifications'});
		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId});
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}        
    }
]);