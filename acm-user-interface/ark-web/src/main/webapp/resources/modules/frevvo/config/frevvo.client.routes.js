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
            .state('frevvo-edit-case', {
                url: '/frevvo-edit-case',
                params: {name: 'edit-case', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-change-case-status', {
                url: '/frevvo-change-case-status',
                params: {name: 'change-case-status', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-reinvestigate', {
                url: '/frevvo-reinvestigate',
                params: {name: 'reinvestigate', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-complaint', {
                url: '/frevvo-new-complaint',
                params: {name: 'new-complaint'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-close-complaint', {
                url: '/frevvo-close-complaint',
                params: {name: 'close-complaint', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-costsheet', {
                url: '/frevvo-new-costsheet',
                params: {name: 'new-costsheet'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-edit-costsheet', {
                url: '/frevvo-edit-costsheet',
                params: {name: 'edit-costsheet', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-timesheet', {
                url: '/frevvo-new-timesheet',
                params: {name: 'new-timesheet'},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-edit-timesheet', {
                url: '/frevvo-edit-timesheet',
                params: {name: 'edit-timesheet', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-new-plainform', {
                url: '/frevvo-new-plainform',
                params: {name: 'new-plainform', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
            .state('frevvo-edit-plainform', {
                url: '/frevvo-edit-plainform',
                params: {name: 'edit-plainform', arg: null},
                templateUrl: 'modules/frevvo/views/frevvo.client.view.html'
            })
        ;
    }
]);
