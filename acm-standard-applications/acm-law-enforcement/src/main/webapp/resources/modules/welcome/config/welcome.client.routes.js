'use strict';

angular.module('welcome').config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        // For any unmatched url redirect to welcome
        $urlRouterProvider.otherwise('/welcome');

        $stateProvider.state('welcome', {
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