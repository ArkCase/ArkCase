'use strict';

angular.module('reports').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('reports', {
			url: '/reports',
			templateUrl: 'modules/reports/views/reports.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('reports');
					return $translate.refresh();
				}]
			}
		});
	}
]);