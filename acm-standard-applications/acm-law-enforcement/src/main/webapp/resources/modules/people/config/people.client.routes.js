'use strict';

//Setting up route
angular.module('people').config(['$stateProvider',
    function ($stateProvider) {

        // Project state routing
        $stateProvider
            .state('people', {
                url: '/people',
                templateUrl: 'modules/people/views/people.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('dashboard');
                        $translatePartialLoader.addPart('people');
                        return $translate.refresh();
                    }]
                }
            })

            .state('people.id', {
                url: '/:id',
                templateUrl: 'modules/people/views/people.client.view.html'
            })

            .state('people.main', {
                url: '/:id/main',
                templateUrl: 'modules/people/views/components/person-main.client.view.html',
                params: {
                    "type": "PEOPLE"
                }
            })

            .state('people.details', {
                url: '/:id/details',
                templateUrl: 'modules/people/views/components/person-details.client.view.html'
            })

            .state('people.phones', {
                url: '/:id/phones',
                templateUrl: 'modules/people/views/components/person-phones.client.view.html'
            })

            .state('people.emails', {
                url: '/:id/emails',
                templateUrl: 'modules/people/views/components/person-emails.client.view.html'
            })

            .state('people.urls', {
                url: '/:id/urls',
                templateUrl: 'modules/people/views/components/person-urls.client.view.html'
            })

            .state('people.history', {
                url: '/:id/history',
                templateUrl: 'modules/people/views/components/person-history.client.view.html'
            })

            .state('people.pictures', {
                url: '/:id/pictures',
                templateUrl: 'modules/people/views/components/person-pictures.client.view.html'
            })

            .state('people.aliases', {
                url: '/:id/aliases',
                templateUrl: 'modules/people/views/components/person-aliases.client.view.html'
            })

            .state('newPerson', {
                url: '/newPerson',
                templateUrl: 'modules/people/views/components/person-new-person.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('people');
                        return $translate.refresh();
                    }]
                }
            })

    }
]);