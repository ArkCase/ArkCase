'use strict';

angular.module('analytics-audit').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.
        state('analytics-audit', {
            url: '/analytics-audit',
            templateUrl: 'modules/analytics-audit/views/analytics-audit.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('analytics-audit');
                    return $translate.refresh();
                }]
            }
        });
    }
]);