'use strict';

angular.module('cost-tracking').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('cost-tracking', {
			url: '/cost-tracking',
			templateUrl: 'modules/cost-tracking/views/cost-tracking.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('cost-tracking');
					return $translate.refresh();
				}]
			}
		});
	}
]);