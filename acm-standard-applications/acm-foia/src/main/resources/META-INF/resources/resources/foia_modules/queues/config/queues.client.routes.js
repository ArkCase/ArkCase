'use strict';

//Setting up route
angular.module('queues').config([ '$stateProvider', function($stateProvider) {
    // Queues state routing

    // Perform redirection to first queue by default
    $stateProvider.state('queues', {
        url: '/queues',
        templateUrl: 'modules/queues/views/queues.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('queues');
                return $translate.refresh();
            } ]
        }
    })

    .state('queues.queue', {
        url: '/:name',
        templateUrl: 'modules/queues/views/queues.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('queues');
                $translatePartialLoader.addPart('common');
                return $translate.refresh();
            } ]
        }
    });
} ]);