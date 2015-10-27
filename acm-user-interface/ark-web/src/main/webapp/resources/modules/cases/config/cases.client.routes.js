'use strict';

//Setting up route
angular.module('cases').config(['$stateProvider', '$urlRouterProvider',
	function($stateProvider, $urlRouterProvider) {
        // For any unmatched url redirect to  /agents
        $urlRouterProvider.otherwise('/cases');

		// Project state routing
		$stateProvider
			.state('cases', {
				url: '/cases',
				templateUrl: 'modules/cases/views/cases.client.view.html',
				resolve: {
					translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
						$translatePartialLoader.addPart('common');
						$translatePartialLoader.addPart('cases');
						return $translate.refresh();
					}]
				}
			})

            .state('wizard', {
                url: '/wizard',
                templateUrl: 'modules/cases/views/components/case-wizard.client.view.html'
            })

			.state('cases.id', {
				url: '/:id',
				templateUrl: 'modules/cases/views/components/case-calendar.client.view.html'
			})

			.state('cases.calendar', {
				url: '/:id/calendar',
				templateUrl: 'modules/cases/views/components/case-calendar.client.view.html'
			})

			.state('cases.correspondence', {
				url: '/:id/correspondence',
				templateUrl: 'modules/cases/views/components/case-correspondence.client.view.html'
			})

			.state('cases.cost', {
				url: '/:id/cost',
				templateUrl: 'modules/cases/views/components/case-cost.client.view.html'
			})

			.state('cases.details', {
				url: '/:id/details',
				templateUrl: 'modules/cases/views/components/case-details.client.view.html'
			})

			.state('cases.documents', {
				url: '/:id/documents',
				templateUrl: 'modules/cases/views/components/case-documents.client.view.html'
			})

			.state('cases.history', {
				url: '/:id/history',
				templateUrl: 'modules/cases/views/components/case-history.client.view.html'
			})

			.state('cases.main', {
				url: '/:id/main',
				templateUrl: 'modules/cases/views/components/case-main.client.view.html'
			})

			.state('cases.notes', {
				url: '/:id/notes',
				templateUrl: 'modules/cases/views/components/case-notes.client.view.html'
			})

			.state('cases.participants', {
				url: '/:id/participants',
				templateUrl: 'modules/cases/views/components/case-participants.client.view.html'
			})

			.state('cases.people', {
				url: '/:id/people',
				templateUrl: 'modules/cases/views/components/case-people.client.view.html'
			})

			.state('cases.references', {
				url: '/:id/references',
				templateUrl: 'modules/cases/views/components/case-references.client.view.html'
			})

			.state('cases.tasks', {
				url: '/:id/tasks',
				templateUrl: 'modules/cases/views/components/case-tasks.client.view.html'
			})

			.state('cases.time', {
				url: '/:id/time',
				templateUrl: 'modules/cases/views/components/case-time.client.view.html'
			})


	}
]);