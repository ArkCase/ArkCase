'use strict';

angular.module('search').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('search', {
			url: '/search',
			templateUrl: 'modules/search/views/search.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('search');
					return $translate.refresh();
				}]
			}
		})
		.state('search.results', {
			url: '/results',
			templateUrl: 'modules/search/views/components/search-results.client.view.html',
		})
	}
]);