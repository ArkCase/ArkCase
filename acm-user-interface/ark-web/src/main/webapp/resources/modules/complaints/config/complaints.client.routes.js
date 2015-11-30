'use strict';

//Setting up route
angular.module('complaints').config(['$stateProvider', '$urlRouterProvider',
    function ($stateProvider, $urlRouterProvider) {
        // For any unmatched url redirect to  /agents
        $urlRouterProvider.otherwise('/complaints');

        // Project state routing
        $stateProvider
            .state('complaints', {
                url: '/complaints',
                templateUrl: 'modules/complaints/views/complaints.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('complaints');
                        return $translate.refresh();
                    }]
                }
            })

            .state('complaints.id', {
                url: '/:id',
                templateUrl: 'modules/complaints/views/complaints.client.view.html'
            })

            .state('complaints.main', {
                url: '/:id/main',
                templateUrl: 'modules/complaints/views/components/complaint-main.client.view.html'
            })

            .state('complaints.viewer', {
                url: '/:id/viewer/:containerId/:containerType/:name/:selectedIds',
                templateUrl: 'modules/complaints/views/components/complaint-viewer.client.view.html'
            })

            .state('complaints.status', {
                url: '/:id/status/:complaintNumber/:status',
                templateUrl: 'modules/complaints/views/components/complaint-status.client.view.html'
            })

            .state('complaints.wizard', {
                url: '/wizard',
                templateUrl: 'modules/complaints/views/components/complaint-wizard.client.view.html'
            })

            .state('complaints.calendar', {
                url: '/:id/calendar',
                templateUrl: 'modules/complaints/views/components/complaint-calendar.client.view.html'
            })

            .state('complaints.correspondence', {
                url: '/:id/correspondence',
                templateUrl: 'modules/complaints/views/components/complaint-correspondence.client.view.html'
            })

            .state('complaints.cost', {
                url: '/:id/cost',
                templateUrl: 'modules/complaints/views/components/complaint-cost.client.view.html'
            })

            .state('complaints.details', {
                url: '/:id/details',
                templateUrl: 'modules/complaints/views/components/complaint-details.client.view.html'
            })

            .state('complaints.documents', {
                url: '/:id/documents',
                templateUrl: 'modules/complaints/views/components/complaint-documents.client.view.html'
            })

            .state('complaints.history', {
                url: '/:id/history',
                templateUrl: 'modules/complaints/views/components/complaint-history.client.view.html'
            })

            .state('complaints.notes', {
                url: '/:id/notes',
                templateUrl: 'modules/complaints/views/components/complaint-notes.client.view.html'
            })

            .state('complaints.participants', {
                url: '/:id/participants',
                templateUrl: 'modules/complaints/views/components/complaint-participants.client.view.html'
            })

            .state('complaints.people', {
                url: '/:id/people',
                templateUrl: 'modules/complaints/views/components/complaint-people.client.view.html'
            })

            .state('complaints.references', {
                url: '/:id/references',
                templateUrl: 'modules/complaints/views/components/complaint-references.client.view.html'
            })

            .state('complaints.tasks', {
                url: '/:id/tasks',
                templateUrl: 'modules/complaints/views/components/complaint-tasks.client.view.html'
            })

            .state('complaints.time', {
                url: '/:id/time',
                templateUrl: 'modules/complaints/views/components/complaint-time.client.view.html'
            })

            .state('complaints.locations', {
                url: '/:id/locations',
                templateUrl: 'modules/complaints/views/components/complaint-locations.client.view.html'
            })


    }
]);