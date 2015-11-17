'use strict';

angular.module('cost-tracking').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('cost-tracking', {
			url: '/cost-tracking',
			templateUrl: 'modules/cost-tracking/views/cost-tracking.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('common');
					$translatePartialLoader.addPart('cost-tracking');
					return $translate.refresh();
				}]
			}
		})

			.state('cost-tracking.main', {
				url: '/:id/main',
				templateUrl: 'modules/cost-tracking/views/components/cost-tracking-main.client.view.html'
			})

			.state('cost-tracking.details', {
				url: '/:id/details',
				templateUrl: 'modules/cost-tracking/views/components/cost-tracking-details.client.view.html'
			})

			.state('cost-tracking.person', {
				url: '/:id/person',
				templateUrl: 'modules/cost-tracking/views/components/cost-tracking-person.client.view.html'
			})

			.state('cost-tracking.timesSummary', {
				url: '/:id/person',
				templateUrl: 'modules/cost-tracking/views/components/cost-tracking-time-summary.client.view.html'
			})
	}
]);