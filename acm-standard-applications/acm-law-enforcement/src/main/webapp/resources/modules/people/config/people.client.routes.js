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

            .state('people.pictures', {
                url: '/:id/pictures',
                templateUrl: 'modules/people/views/components/person-pictures.client.view.html'
            })

            .state('people.addresses', {
                url: '/:id/addresses',
                templateUrl: 'modules/people/views/components/person-addresses.client.view.html'
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

            .state('people.aliases', {
                url: '/:id/aliases',
                templateUrl: 'modules/people/views/components/person-aliases.client.view.html'
            })

            .state('people.organizations', {
                url: '/:id/organizations',
                templateUrl: 'modules/people/views/components/person-organizations.client.view.html'
            })

            .state('people.ids', {
                url: '/:id/ids',
                templateUrl: 'modules/people/views/components/person-ids.client.view.html'
            })

            .state('people.cases', {
                url: '/:id/cases',
                templateUrl: 'modules/people/views/components/person-cases.client.view.html'
            })

            .state('people.complaints', {
                url: '/:id/complaints',
                templateUrl: 'modules/people/views/components/person-complaints.client.view.html'
            })

            .state('people.related', {
                url: '/:id/related',
                templateUrl: 'modules/people/views/components/person-related.client.view.html'
            })

            .state('people.history', {
                url: '/:id/history',
                templateUrl: 'modules/people/views/components/person-history.client.view.html'
            })


    }
]);