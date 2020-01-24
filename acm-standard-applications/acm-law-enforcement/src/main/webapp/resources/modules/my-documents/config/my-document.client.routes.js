'use strict';

//Setting up route
angular.module('my-documents').config([ '$stateProvider', function($stateProvider) {

    // Project state routing
    $stateProvider.state('my-documents', {
        url: '/my-documents',
        templateUrl: 'modules/my-documents/views/my-documents.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('dashboard');
                $translatePartialLoader.addPart('document-repository');
                $translatePartialLoader.addPart('my-documents');
                return $translate.refresh();
            } ]
        }
    })

    .state('my-documents.id', {
        url: '/:id',
        templateUrl: 'modules/document-repository/views/document-repository.client.view.html'
    })

    .state('my-documents.main', {
        url: '/:id/main',
        templateUrl: 'modules/my-documents/views/components/my-documents-main.client.view.html',
        params: {
            "type": "MY_DOC_REPO"
        }
    })

    .state('my-documents.details', {
        url: '/:id/details',
        templateUrl: 'modules/document-repository/views/components/document-repository-details.client.view.html'
    })

    .state('my-documents.documents', {
        url: '/:id/documents',
        templateUrl: 'modules/document-repository/views/components/document-repository-documents.client.view.html'
    })

    .state('my-documents.history', {
        url: '/:id/history',
        templateUrl: 'modules/document-repository/views/components/document-repository-history.client.view.html'
    })

    .state('my-documents.notes', {
        url: '/:id/notes',
        templateUrl: 'modules/document-repository/views/components/document-repository-notes.client.view.html'
    })

    .state('my-documents.participants', {
        url: '/:id/participants',
        templateUrl: 'modules/document-repository/views/components/document-repository-participants.client.view.html'
    })

    .state('my-documents.tasks', {
        url: '/:id/tasks',
        templateUrl: 'modules/document-repository/views/components/document-repository-tasks.client.view.html'
    })

    .state('my-documents.references', {
        url: '/:id/references',
        templateUrl: 'modules/document-repository/views/components/document-repository-references.client.view.html'
    })

    .state('my-documents.tags', {
        url: '/:id/tags',
        templateUrl: 'modules/document-repository/views/components/document-repository-tags.client.view.html'
    })

} ]);
