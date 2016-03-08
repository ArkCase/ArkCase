'use strict';

angular.module('audit').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('audit', {
			url: '/audit',
			templateUrl: 'modules/audit/views/audit.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('audit');
					return $translate.refresh();
				}]
			}
		});
	}
]);