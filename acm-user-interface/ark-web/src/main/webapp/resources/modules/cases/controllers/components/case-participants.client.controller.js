'use strict';

angular.module('cases').controller('CaseParticipantsController', ['$scope', '$stateParams', 'CasesService',
	function($scope, $stateParams, CasesService) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'participants');
		$scope.config = null;


		function applyConfig(e, componentId, config) {
			if (componentId == 'participants') {
				$scope.config = config;
				$scope.gridOptions = {
					enableColumnResizing: true,
					enableRowSelection: true,
					enableRowHeaderSelection: false,
					multiSelect: false,
					noUnselect : false,
					columnDefs: config.columnDefs,
					onRegisterApi: function(gridApi) {
						$scope.gridApi = gridApi;
					}
				};

				var id = $stateParams.id;
				CasesService.queryParticipants({
					id: id,
					startWith: 0,
					count: 10
				}, function(data) {
					//numFound  start
					$scope.gridOptions.data = data.response.docs;
				})
			}
		}
	}
]);