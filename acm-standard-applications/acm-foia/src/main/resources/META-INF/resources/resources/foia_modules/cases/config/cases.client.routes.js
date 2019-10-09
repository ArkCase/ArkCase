'use strict';

//Setting up route
angular.module('cases').config([ '$stateProvider', function($stateProvider) {

    // Project state routing
    $stateProvider.state('cases', {
        url: '/requests',
        templateUrl: 'modules/cases/views/cases.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('cases');
                $translatePartialLoader.addPart('request-info');
                $translatePartialLoader.addPart('admin');
                $translatePartialLoader.addPart('document-details');
                return $translate.refresh();
            } ]
        }
    })

    .state('new-foia-request', {
        url: '/new-foia-request',
        templateUrl: 'modules/cases/views/components/foia-new-request.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('cases');
                return $translate.refresh();
            } ]
        }
    })

    .state('cases.id', {
        url: '/:id',
        templateUrl: 'modules/cases/views/cases.client.view.html'
    })

    .state('cases.main', {
        url: '/:id/main',
        templateUrl: 'modules/cases/views/components/case-main.client.view.html',
        params: {
            "type": "CASE_FILE"
        }
    })

    .state('cases.documents', {
        url: '/:id/documents',
        templateUrl: 'modules/cases/views/components/case-documents.client.view.html'
    })

    .state('cases.history', {
        url: '/:id/history',
        templateUrl: 'modules/common/views/object-history.client.view.html',
        params: {
            "type": "CASE_FILE"
        }
    })

    .state('cases.notes', {
        url: '/:id/notes',
        templateUrl: 'modules/cases/views/components/case-notes.client.view.html'
    })

    .state('cases.exemption', {
        url: '/:id/exemption',
        templateUrl: 'modules/cases/views/components/case-exemption.client.view.html'
    })

    .state('cases.participants', {
        url: '/:id/participants',
        templateUrl: 'modules/cases/views/components/case-participants.client.view.html'
    })

    .state('cases.people', {
        url: '/:id/people',
        templateUrl: 'modules/cases/views/components/case-people.client.view.html'
    })

            
    .state('cases.organizations', {
        url: '/:id/organizations',
        templateUrl: 'modules/cases/views/components/case-organizations.client.view.html'
    })

    .state('cases.references', {
        url: '/:id/references',
        templateUrl: 'modules/cases/views/components/case-references.client.view.html'
    })

    .state('cases.tasks', {
        url: '/:id/tasks',
        templateUrl: 'modules/cases/views/components/case-tasks.client.view.html'
    })

    .state('cases.tags', {
        url: '/:id/tags',
        templateUrl: 'modules/cases/views/components/case-tags.client.view.html'
    })

    .state('cases.cost', {
        url: '/:id/cost',
        templateUrl: 'modules/cases/views/components/case-cost.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('cost-tracking');
                return $translate.refresh();
            } ]
        }
    })

    .state('cases.time', {
        url: '/:id/time',
        templateUrl: 'modules/cases/views/components/case-time.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('time-tracking');
                return $translate.refresh();
            } ]
        }
    })

    .state('cases.approvalrouting', {
        url: '/:type/:id/approvals',
        templateUrl: 'modules/cases/views/components/case-approval-routing.client.view.html'
    })

    .state('cases.calendar', {
        url: '/:id/calendar',
        templateUrl: 'modules/cases/views/components/case-calendar.client.view.html'
    })

        .state('cases.billing', {
            url: '/:id/billing',
            templateUrl: 'modules/cases/views/components/case-billing.client.view.html'
        })
    .state('cases.suggestedCases', {
        url: '/:id/suggested',
        templateUrl: 'modules/cases/views/components/case-suggested-cases.client.view.html'
    })
} ]);