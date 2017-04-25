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

            .state('organizations.phones', {
                url: '/:id/phones',
                templateUrl: 'modules/organizations/views/components/organization-phones.client.view.html'
            })

            .state('organizations.emails', {
                url: '/:id/emails',
                templateUrl: 'modules/organizations/views/components/organization-emails.client.view.html'
            })

            .state('organizations.urls', {
                url: '/:id/urls',
                templateUrl: 'modules/organizations/views/components/organization-urls.client.view.html'
            })

            .state('organizations.history', {
                url: '/:id/history',
                templateUrl: 'modules/organizations/views/components/organization-history.client.view.html'
            })

            .state('newOrganization', {
                url: '/newOrganization',
                templateUrl: 'modules/organizations/views/components/organization-new-organizations.client.view.html',
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