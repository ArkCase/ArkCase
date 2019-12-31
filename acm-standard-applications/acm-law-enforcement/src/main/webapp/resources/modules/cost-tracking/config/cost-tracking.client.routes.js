'use strict';

angular.module('cost-tracking').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('cost-tracking', {
        url: '/cost-tracking',
        templateUrl: 'modules/cost-tracking/views/cost-tracking.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('cost-tracking');
                $translatePartialLoader.addPart('request-info');
                return $translate.refresh();
            } ]
        }
    })

    .state('cost-tracking.main', {
        url: '/:id/main',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-main.client.view.html',
        params: {
            "type": "COSTSHEET"
        }
    })

    .state('cost-tracking.details', {
        url: '/:id/details',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-details.client.view.html'
    })

    .state('cost-tracking.person', {
        url: '/:id/person',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-person.client.view.html'
    })

    .state('cost-tracking.summary', {
        url: '/:id/summary',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-summary.client.view.html'
    })

    .state('cost-tracking.tags', {
        url: '/:id/tags',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-tags.client.view.html'
    })

    .state('cost-tracking.tasks', {
        url: '/:id/tasks',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-tasks.client.view.html'
    })

    .state('cost-tracking.documents', {
        url: '/:id/documents',
        templateUrl: 'modules/cost-tracking/views/components/cost-tracking-documents.client.view.html'
    })

} ]).run([ 'Helper.DashboardService', function(DashboardHelper) {
    DashboardHelper.addLocales();
} ]);
