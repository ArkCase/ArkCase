'use strict';

//Setting up route
angular.module('document-repository').config(['$stateProvider',
    function ($stateProvider) {

        // Project state routing
        $stateProvider
            .state('document-repository', {
                url: '/document-repository',
                templateUrl: 'modules/document-repository/views/document-repository.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('dashboard');
                        $translatePartialLoader.addPart('document-repository');
                        return $translate.refresh();
                    }]
                }
            })

            .state('document-repository.id', {
                url: '/:id',
                templateUrl: 'modules/document-repository/views/document-repository.client.view.html'
            })

            .state('document-repository.main', {
                url: '/:id/main',
                templateUrl: 'modules/document-repository/views/components/document-repository-main.client.view.html',
                params: {
                    "type": "DOC_REPO"
                }
            })

            .state('new-document-repository', {
                url: '/new-document-repository',
                templateUrl: 'modules/document-repository/views/components/document-repository-new-document-repository.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('document-repository');
                        return $translate.refresh();
                    }]
                }
            })

            .state('document-repository.details', {
                url: '/:id/details',
                templateUrl: 'modules/document-repository/views/components/document-repository-details.client.view.html'
            })

            .state('document-repository.documents', {
                url: '/:id/documents',
                templateUrl: 'modules/document-repository/views/components/document-repository-documents.client.view.html'
            })

            .state('document-repository.history', {
                url: '/:id/history',
                templateUrl: 'modules/document-repository/views/components/document-repository-history.client.view.html'
            })

            .state('document-repository.notes', {
                url: '/:id/notes',
                templateUrl: 'modules/document-repository/views/components/document-repository-notes.client.view.html'
            })

            .state('document-repository.participants', {
                url: '/:id/participants',
                templateUrl: 'modules/document-repository/views/components/document-repository-participants.client.view.html'
            })

            .state('document-repository.references', {
                url: '/:id/references',
                templateUrl: 'modules/document-repository/views/components/document-repository-references.client.view.html'
            })

            .state('document-repository.tags', {
                url: '/:id/tags',
                templateUrl: 'modules/document-repository/views/components/document-repository-tags.client.view.html'
            })

    }
]);
