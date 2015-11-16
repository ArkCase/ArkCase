'use strict';

//Setting up route
angular.module('tasks').config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        // For any unmatched url redirect to  /agents
        $urlRouterProvider.otherwise('/tasks');

        // Project state routing
        $stateProvider
            .state('tasks', {
                url: '/tasks',
                templateUrl: 'modules/tasks/views/tasks.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('tasks');
                        return $translate.refresh();
                    }]
                }
            })

            .state('tasks.id', {
                url: '/:id',
                templateUrl: 'modules/tasks/views/tasks.client.view.html'
            })

            .state('tasks.main', {
                url: '/:id/main',
                templateUrl: 'modules/tasks/views/components/task-main.client.view.html'
            })

            .state('tasks.status', {
                url: '/status/:id/:taskNumber/:status',
                templateUrl: 'modules/tasks/views/components/task-status.client.view.html'
            })

            .state('tasks.wizard', {
                url: '/wizard',
                templateUrl: 'modules/tasks/views/components/task-wizard.client.view.html'
            })

            .state('tasks.details', {
                url: '/:id/details',
                templateUrl: 'modules/tasks/views/components/task-details.client.view.html'
            })

            .state('tasks.reworkdetails', {
                url: '/:id/details',
                templateUrl: 'modules/tasks/views/components/task-reworkdetails.client.view.html'
            })

            .state('tasks.docsreview', {
                url: '/:id/details',
                templateUrl: 'modules/tasks/views/components/task-docsreview.client.view.html'
            })

            .state('tasks.rejcomments', {
                url: '/:id/details',
                templateUrl: 'modules/tasks/views/components/task-rejcomments.client.view.html'
            })

            .state('tasks.attachments', {
                url: '/:id/attachments',
                templateUrl: 'modules/tasks/views/components/task-attachments.client.view.html'
            })

            .state('tasks.notes', {
                url: '/:id/notes',
                templateUrl: 'modules/tasks/views/components/task-notes.client.view.html'
            })

            .state('tasks.workflow', {
                url: '/:id/workflow',
                templateUrl: 'modules/tasks/views/components/task-workflow.client.view.html'
            })

            .state('tasks.history', {
                url: '/:id/history',
                templateUrl: 'modules/tasks/views/components/task-history.client.view.html'
            })

            .state('tasks.esignatures', {
                url: '/:id/esignatures',
                templateUrl: 'modules/tasks/views/components/task-signatures.client.view.html'
            })
    }
]);