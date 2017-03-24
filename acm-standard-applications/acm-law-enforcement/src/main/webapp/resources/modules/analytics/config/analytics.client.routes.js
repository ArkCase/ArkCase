'use strict';

angular.module('analytics').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.
        state('analytics', {
            url: '/analytics',
            templateUrl: 'modules/analytics/views/analytics.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('analytics');
                    return $translate.refresh();
                }]
            }
        });
    }
]);