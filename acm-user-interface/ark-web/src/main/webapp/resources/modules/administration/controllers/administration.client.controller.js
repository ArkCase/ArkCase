'use strict';

angular.module('administration').controller('AdministrationController', ['$scope',
	function($scope) {
		$scope.$on('req-module-selected', moduleSelected);

		function moduleSelected(e, module) {
			$scope.$broadcast('module-selected', module);
		}
	}
]);