'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$q', '$stateParams', 'ConfigService', 'CasesService',
	function($scope, $q, $stateParams, ConfigService, CasesService) {
		$scope.config = ConfigService.getModule({moduleId: 'cases'});
		$scope.$on('req-component-config', onConfigRequest);
		function onConfigRequest(e, componentId) {
			$scope.config.$promise.then(function(config){
				var componentConfig = _.find(config.components, {id: componentId})
				$scope.$broadcast('component-config', componentId, componentConfig);
			});
		}

		$scope.$on('req-select-case', function(e, selectedCase){
			$scope.$broadcast('case-selected', selectedCase);

			var id;
			if (selectedCase) {
				id = selectedCase.id;
			}
			if (id) {
				CasesService.get({id: id
				}, function(data) {
					$scope.$broadcast('case-retrieved', data);
				});
			}


		});

//        var id = $stateParams.id;
//        if (id) {
//            CasesService.get({
//                id: id
//            }, function(data) {
//                $scope.$broadcast('case-retrieved', data);
//            })
//        }

	}
]);