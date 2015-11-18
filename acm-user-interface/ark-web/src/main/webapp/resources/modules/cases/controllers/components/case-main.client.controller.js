'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', 'ConfigService',
	function($scope, ConfigService) {
		$scope.$emit('req-component-config', 'main');
		$scope.$on('component-config', function applyConfig(e, componentId, config) {
			if (componentId == 'main') {
				$scope.config = config;
			}
		});


		ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
			$scope.components = moduleConfig.components;
			return moduleConfig;
		});
		//ConfigService.getModule({moduleId: 'cases'}, function(moduleConfig){
		//	$scope.components = moduleConfig.components;
		//});
	}
]);