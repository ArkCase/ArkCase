'use strict';

angular.module('frevvo').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.
            state('frevvo', {
                url: '/frevvo',
                params: {name: '', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('frevvo');
                        return $translate.refresh();
                    }]
                }
            })
            .state('frevvo-new-case', {
                url: '/frevvo-new-case',
                params: {name: 'new-case'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-complaint', {
                url: '/frevvo-new-complaint',
                params: {name: 'new-complaint'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-costsheet', {
                url: '/frevvo-new-costsheet',
                params: {name: 'new-costsheet'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-timesheet', {
                url: '/frevvo-new-timesheet',
                params: {name: 'new-timesheet'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })

        ;
    }
]);
