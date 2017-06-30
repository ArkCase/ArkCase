'use strict';

angular.module('time-tracking').config(['$stateProvider',
	function($stateProvider) {
		$stateProvider.
		state('time-tracking', {
			url: '/time-tracking',
			templateUrl: 'modules/time-tracking/views/time-tracking.client.view.html',
			resolve: {
				translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
					$translatePartialLoader.addPart('common');
					$translatePartialLoader.addPart('time-tracking');
					return $translate.refresh();
				}]
			}
		})

			.state('time-tracking.id', {
				url: '/:id',
				templateUrl: 'modules/time-tracking/views/time-tracking.client.view.html'
			})

			.state('time-tracking.main', {
				url: '/:id/main',
				templateUrl: 'modules/time-tracking/views/components/time-tracking-main.client.view.html',
				params: {
					"type": "TIMESHEET"
				}
			})

			.state('time-tracking.details', {
				url: '/:id/details',
				templateUrl: 'modules/time-tracking/views/components/time-tracking-details.client.view.html'
			})

			.state('time-tracking.person', {
				url: '/:id/person',
				templateUrl: 'modules/time-tracking/views/components/time-tracking-person.client.view.html'
			})

			.state('time-tracking.summary', {
				url: '/:id/summary',
				templateUrl: 'modules/time-tracking/views/components/time-tracking-summary.client.view.html'
			})
			
			.state('time-tracking.tags', {
                url: '/:id/tags',
                templateUrl: 'modules/time-tracking/views/components/time-tracking-tags.client.view.html'
            })

	}
]).run(['ArkCaseDashboard', 'ConfigService'
    , function (ArkCaseDashboard, ConfigService) {
        ConfigService.getModuleConfig("dashboard").then(function (moduleConfig) {
            moduleConfig.locals.forEach(function(local){
                ArkCaseDashboard.addLocale(local.iso, local.translations);
            });
            return moduleConfig;
        });
    }
])
;