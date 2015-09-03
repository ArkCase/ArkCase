'use strict';

angular.module('cases').controller('Cases.DetailsController', ['$scope', '$stateParams', 'CasesService',
	function($scope, $stateParams, CasesService) {
		$scope.$emit('req-component-config', 'details');

		$scope.config = null;
		$scope.$on('component-config', applyConfig);
		function applyConfig(e, componentId, config) {
			if (componentId == 'details') {
				$scope.config = config;
			}
		}

		$scope.$on('case-retrieved', function(e, data) {
			if (data && data.details != $scope.details) {
				$scope.details = data.details;
			}
		});


		$scope.options = {
			height: 120
		};
		$scope.saveDetails = function() {
			alert("saveDetails:" + $scope.details);
		};

		//var id = $stateParams.id;
		//CasesService.get({
		//	id: id
		//}, function(data) {
		//	$scope.details = data.details;
		//})
	}
]);