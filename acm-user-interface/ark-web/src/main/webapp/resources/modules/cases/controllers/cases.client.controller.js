'use strict';

angular.module('cases').controller('CasesController', ['$scope', '$stateParams', 'UtilService', 'ValidationService', 'ConfigService', 'CasesService',
	function($scope, $stateParams, Util, Validator, ConfigService, CasesService) {
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

			var id = Util.goodMapValue([selectedCase, "id"], null);
			loadCase(id);
		});


		var loadCase = function(id) {
			if (id) {
				CasesService.get({id: id
				}, function(data) {
                    if (Validator.validateCaseFile(data)) {
                        $scope.caseData = data;
                        $scope.$broadcast('case-retrieved', data);
                    }
				});
			}
		}

		var id = Util.goodMapValue([$stateParams, "id"], null);
		loadCase(id);
	}
]);