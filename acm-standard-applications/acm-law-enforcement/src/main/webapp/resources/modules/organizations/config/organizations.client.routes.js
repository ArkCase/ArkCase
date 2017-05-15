'use strict';

//Setting up route
angular.module('organizations').config(['$stateProvider',
    function ($stateProvider) {

        // Project state routing
        $stateProvider
            .state('organizations', {
                url: '/organizations',
                templateUrl: 'modules/organizations/views/organizations.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('dashboard');
                        $translatePartialLoader.addPart('organizations');
                        return $translate.refresh();
                    }]
                }
            })

            .state('organizations.id', {
                url: '/:id',
                templateUrl: 'modules/organizations/views/organizations.client.view.html'
            })

            .state('organizations.main', {
                url: '/:id/main',
                templateUrl: 'modules/organizations/views/components/organization-main.client.view.html',
                params: {
                    "type": "ORGANIZATION"
                }
            })

            .state('organizations.details', {
                url: '/:id/details',
                templateUrl: 'modules/organizations/views/components/organization-details.client.view.html'
            })

            .state('organizations.addresses', {
                url: '/:id/addresses',
                templateUrl: 'modules/organizations/views/components/organization-addresses.client.view.html'
            })

            .state('organizations.phones', {
                url: '/:id/phones',
                templateUrl: 'modules/organizations/views/components/organization-phones.client.view.html'
            })

            .state('organizations.faxes', {
                url: '/:id/faxes',
                templateUrl: 'modules/organizations/views/components/organization-faxes.client.view.html'
            })

            .state('organizations.emails', {
                url: '/:id/emails',
                templateUrl: 'modules/organizations/views/components/organization-emails.client.view.html'
            })

            .state('organizations.urls', {
                url: '/:id/urls',
                templateUrl: 'modules/organizations/views/components/organization-urls.client.view.html'
            })

            .state('organizations.dbas', {
                url: '/:id/dbas',
                templateUrl: 'modules/organizations/views/components/organization-dbas.client.view.html'
            })

            .state('organizations.people', {
                url: '/:id/people',
                templateUrl: 'modules/organizations/views/components/organization-people.client.view.html'
            })

            .state('organizations.ids', {
                url: '/:id/ids',
                templateUrl: 'modules/organizations/views/components/organization-ids.client.view.html'
            })

            .state('organizations.cases', {
                url: '/:id/cases',
                templateUrl: 'modules/organizations/views/components/organization-cases.client.view.html'
            })

            .state('organizations.complaints', {
                url: '/:id/complaints',
                templateUrl: 'modules/organizations/views/components/organization-complaints.client.view.html'
            })

            .state('organizations.related', {
                url: '/:id/related',
                templateUrl: 'modules/organizations/views/components/organization-related.client.view.html'
            })

            .state('organizations.history', {
                url: '/:id/history',
                templateUrl: 'modules/organizations/views/components/organization-history.client.view.html'
            })

            .state('newOrganization', {
                url: '/newOrganization',
                templateUrl: 'modules/organizations/views/components/organization-new-organization.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('organizations');
                        return $translate.refresh();
                    }]
                }
            })
    }
]);