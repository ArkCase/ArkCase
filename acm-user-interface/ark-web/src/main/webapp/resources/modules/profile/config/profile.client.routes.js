'use strict';

angular.module('profile').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('profile', {
			url: '/profile',
			templateUrl: 'modules/profile/views/profile.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('profile');
					return $translate.refresh();
				}]
			}
		});
	}
]);