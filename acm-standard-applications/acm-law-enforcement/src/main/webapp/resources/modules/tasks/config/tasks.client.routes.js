'use strict';

//Setting up route
angular.module('tasks').config(['$stateProvider',
    function ($stateProvider) {

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
                url: '/:type/:id',
                templateUrl: 'modules/tasks/views/tasks.client.view.html'
            })

            .state('tasks.main', {
                url: '/:type/:id/main',
                templateUrl: 'modules/tasks/views/components/task-main.client.view.html',
                params: {
                    "type": "TASK"
                }
            })

            .state('newTask', {
                url: '/newTask',
                templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('tasks');
                        return $translate.refresh();
                    }]
                }
            })

            .state('newTaskFromParentObject', {
                url: '/newTask/:parentType/:parentObject',
                templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('tasks');
                        return $translate.refresh();
                    }]
                },
                params: {
                    "parentTitle": ":parentTitle"
                }
            })

            .state('tasks.details', {
                url: '/:type/:id/details',
                templateUrl: 'modules/tasks/views/components/task-details.client.view.html'
            })

            .state('tasks.reworkdetails', {
                url: '/:type/:id/reworkdetails',
                templateUrl: 'modules/tasks/views/components/task-reworkdetails.client.view.html'
            })

            .state('tasks.docsreview', {
                url: '/:type/:id/docsreview',
                templateUrl: 'modules/tasks/views/components/task-docsreview.client.view.html'
            })

            .state('tasks.rejcomments', {
                url: '/:type/:id/rejcomments',
                templateUrl: 'modules/tasks/views/components/task-rejcomments.client.view.html'
            })

            .state('tasks.attachments', {
                url: '/:type/:id/attachments',
                templateUrl: 'modules/tasks/views/components/task-attachments.client.view.html'
            })

            .state('tasks.notes', {
                url: '/:type/:id/notes',
                templateUrl: 'modules/tasks/views/components/task-notes.client.view.html'
            })
            
            .state('tasks.references', {
                url: '/:type/:id/references',
                templateUrl: 'modules/tasks/views/components/task-references.client.view.html'
            })

            .state('tasks.workflow', {
                url: '/:type/:id/workflow',
                templateUrl: 'modules/tasks/views/components/task-workflow.client.view.html'
            })

            .state('tasks.history', {
                url: '/:type/:id/history',
                templateUrl: 'modules/tasks/views/components/task-history.client.view.html'
            })

            .state('tasks.signatures', {
                url: '/:type/:id/signatures',
                templateUrl: 'modules/tasks/views/components/task-signatures.client.view.html'
            })

            .state('tasks.tags', {
                url: '/:type/:id/tags',
                templateUrl: 'modules/tasks/views/components/task-tags.client.view.html'
            })
    }
]);