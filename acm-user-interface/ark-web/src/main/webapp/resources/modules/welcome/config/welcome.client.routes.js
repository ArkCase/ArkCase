'use strict';

angular.module('welcome').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('welcome', {
			url: '/welcome',
			templateUrl: 'modules/welcome/views/welcome.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('welcome');
					return $translate.refresh();
				}]
			}
		});
	}
]);