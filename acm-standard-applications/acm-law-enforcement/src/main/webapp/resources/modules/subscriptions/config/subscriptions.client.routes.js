'use strict';

angular.module('subscriptions').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('subscriptions', {
			url: '/subscriptions',
			templateUrl: 'modules/subscriptions/views/subscriptions.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('subscriptions');
					return $translate.refresh();
				}]
			}
		});
	}
]);