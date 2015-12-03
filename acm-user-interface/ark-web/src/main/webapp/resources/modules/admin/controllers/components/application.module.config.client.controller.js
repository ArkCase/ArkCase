'use strict';

angular.module('admin').controller('Admin.ModuleConfigController', ['$scope',
	function($scope) {
		$scope.$on('req-module-selected', moduleSelected);

		function moduleSelected(e, module) {
			$scope.$broadcast('module-selected', module);
		}
	}
]);