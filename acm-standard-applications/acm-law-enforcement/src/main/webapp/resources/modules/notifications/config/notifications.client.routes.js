'use strict';

angular.module('notifications').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('notifications', {
			url: '/notifications',
			templateUrl: 'modules/notifications/views/notifications.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('notifications');
					return $translate.refresh();
				}]
			}
		});
	}
]);