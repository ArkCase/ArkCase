'use strict';

//Setting up route
angular.module('administration').config(['$stateProvider', '$urlRouterProvider',
	function($stateProvider, $urlRouterProvider) {
        // For any unmatched url redirect to  /agents
        $urlRouterProvider.otherwise('/administration');

		// Project state routing
		$stateProvider
			.state('administration', {
				url: '/administration',
				templateUrl: 'modules/administration/views/administration.client.view.html'
			})
	}
]);