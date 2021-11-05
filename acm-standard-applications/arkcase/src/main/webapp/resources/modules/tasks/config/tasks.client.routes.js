'use strict';

//Setting up route
angular.module('tasks').config(
        [
                '$stateProvider',
                function($stateProvider) {

                    // Project state routing
                    $stateProvider.state(
                            'tasks',
                            {
                                url: '/tasks',
                                templateUrl: 'modules/tasks/views/tasks.client.view.html',
                                resolve: {
                                    translatePartialLoader: [
                                            '$translate',
                                            '$translatePartialLoader',
                                            'Config.LocaleService',
                                            'Object.LookupService',
                                            function($translate, $translatePartialLoader, LocaleService, ObjectLookupService) {
                                                $translatePartialLoader.addPart('common');
                                                $translatePartialLoader.addPart('dashboard');
                                                $translatePartialLoader.addPart('tasks');
                                                $translatePartialLoader.addPart('preference');
                                                $translatePartialLoader.addPart('document-details');
                                                $translate.resetDataDict().addDataDictFromLabels(LocaleService.getLabelResources([ "tasks", "common" ], "en")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("caseFileTypes")).addDataDictFromLookup(
                                                        ObjectLookupService.getLookupByLookupName("priorities")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("taskOutcomes"));
                                                return $translate.refresh();
                                            } ]
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
                            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                                $translatePartialLoader.addPart('common');
                                $translatePartialLoader.addPart('tasks');
                                return $translate.refresh();
                            } ]
                        }
                    })

                    .state('newTaskFromParentObject', {
                        url: '/newTask/:parentType/:parentObject',
                        templateUrl: 'modules/tasks/views/components/task-new-task.client.view.html',
                        resolve: {
                            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                                $translatePartialLoader.addPart('common');
                                $translatePartialLoader.addPart('tasks');
                                return $translate.refresh();
                            } ]
                        },
                        params: {
                            "parentTitle": ":parentTitle",
                            "parentId": ":parentId",
                            "returnState": ":returnState"
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

                    .state('tasks.documentsunderreview', {
                        url: '/:type/:id/documentsunderreview',
                        templateUrl: 'modules/tasks/views/components/task-docs-under-review.client.view.html'
                    })

                    .state('tasks.rejcomments', {
                        url: '/:type/:id/rejcomments',
                        templateUrl: 'modules/tasks/views/components/task-rejcomments.client.view.html'
                    })

                    .state('tasks.parentdocs', {
                        url: '/:type/:id/parentdocs',
                        templateUrl: 'modules/tasks/views/components/task-parentdocs.client.view.html'
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
                        templateUrl: 'modules/common/views/object-history.client.view.html',
                        params: {
                            "type": "TASK"
                        }
                    })

                    .state('tasks.signatures', {
                        url: '/:type/:id/signatures',
                        templateUrl: 'modules/tasks/views/components/task-signatures.client.view.html'
                    })

                    .state('tasks.tags', {
                        url: '/:type/:id/tags',
                        templateUrl: 'modules/tasks/views/components/task-tags.client.view.html'
                    })

                    .state('tasks.people', {
                        url: '/:type/:id/people',
                    templateUrl: 'modules/tasks/views/components/task-people.client.view.html'
                    })
                } ]).run([ 'Helper.DashboardService', function(DashboardHelper) {
    DashboardHelper.addLocales();
} ]);
