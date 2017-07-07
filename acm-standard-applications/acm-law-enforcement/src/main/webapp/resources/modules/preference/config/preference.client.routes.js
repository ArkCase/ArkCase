'use strict';

angular.module('preference').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.
        state('preference', {
            url: '/preference',
            templateUrl: 'modules/preference/views/preference.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('preference');
                    $translatePartialLoader.addPart('dashboard');
                    return $translate.refresh();
                }]
            }
        });
    }
]);
