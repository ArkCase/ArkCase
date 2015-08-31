'use strict';
'use strict';
angular.module('profile').controller('ContactController', ['$scope', 'ConfigService',
	function($scope, ConfigService) {
		$scope.config = ConfigService.getModule({moduleId: 'profile'});
		$scope.$on('req-component-config', onConfigRequest);

		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}
	}
]);
angular.module('profile').run(function(editableOptions, editableThemes) {
  editableThemes.bs3.inputClass = 'input-sm';
  editableThemes.bs3.buttonsClass = 'btn-sm';
  editableOptions.theme = 'bs3';
});