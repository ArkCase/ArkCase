'use strict';

angular.module('cases').controller('Cases.CalendarController', ['$scope',
	function($scope) {
		$scope.$emit('req-component-config', 'calendar');
        $scope.$on('component-config', function (e, componentId, config) {
			if (componentId == 'calendar') {
				$scope.config = config;
			}
        });
	}
]);