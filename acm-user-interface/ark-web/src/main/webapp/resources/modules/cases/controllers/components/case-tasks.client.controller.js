'use strict';

angular.module('cases').controller('Cases.TasksController', ['$scope', '$stateParams', 'CasesService',
	function($scope, $stateParams, CasesService) {
		$scope.$on('component-config', applyConfig);
		$scope.$emit('req-component-config', 'tasks');
		$scope.config = null;
		$scope.cellAction = cellAction;


		function cellAction(action, entity) {
			alert('make task completed');
		}

		function applyConfig(e, componentId, config) {
			if (componentId == 'tasks') {
				$scope.config = config;
				$scope.gridOptions = {
					enableColumnResizing: true,
					enableRowSelection: true,
					enableRowHeaderSelection: false,
					enableFiltering: config.enableFiltering,
					multiSelect: false,
					noUnselect : false,
					columnDefs: config.columnDefs,
					onRegisterApi: function(gridApi) {
						$scope.gridApi = gridApi;
					}
				};

				var id = $stateParams.id;
				CasesService.queryTasks({
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