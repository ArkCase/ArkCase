'use strict';

//Setting up route
angular.module('complaints').config([ '$stateProvider', function($stateProvider) {

    // Project state routing
    $stateProvider.state('complaints', {
        url: '/complaints',
        templateUrl: 'modules/complaints/views/complaints.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', 'Config.LocaleService', 'Object.LookupService', function($translate, $translatePartialLoader, LocaleService, ObjectLookupService) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('dashboard');
                $translatePartialLoader.addPart('complaints');
                $translatePartialLoader.addPart('document-details');
                $translatePartialLoader.addPart('preference');
                $translate.resetDataDict().addDataDictFromLabels(LocaleService.getLabelResources([ "complaints", "common" ], "en")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("complaintTypes")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("priorities"));
                return $translate.refresh();
            } ]
        }
    })

    .state('complaints.id', {
        url: '/:id',
        templateUrl: 'modules/complaints/views/complaints.client.view.html'
    })

    .state('complaints.main', {
        url: '/:id/main',
        templateUrl: 'modules/complaints/views/components/complaint-main.client.view.html',
        params: {
            "type": "COMPLAINT"
        }
    })

    .state('complaints.calendar', {
        url: '/:id/calendar',
        templateUrl: 'modules/complaints/views/components/complaint-calendar.client.view.html'
    })

    .state('complaints.cost', {
        url: '/:id/cost',
        templateUrl: 'modules/complaints/views/components/complaint-cost.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('cost-tracking');
                return $translate.refresh();
            } ]
        }
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
        templateUrl: 'modules/common/views/object-history.client.view.html',
        params: {
            "type": "COMPLAINT"
        }
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

    .state('complaints.organizations', {
        url: '/:id/organizations',
        templateUrl: 'modules/complaints/views/components/complaint-organizations.client.view.html'
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
        templateUrl: 'modules/complaints/views/components/complaint-time.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('time-tracking');
                return $translate.refresh();
            } ]
        }
    })

    .state('complaints.addresses', {
        url: '/:id/addresses',
        templateUrl: 'modules/complaints/views/components/complaint-addresses.client.view.html'
    })

    .state('complaints.tags', {
        url: '/:id/tags',
        templateUrl: 'modules/complaints/views/components/complaint-tags.client.view.html'
    })

    .state('complaints.approvalRouting', {
        url: '/:type/:id/approvals',
        templateUrl: 'modules/complaints/views/components/complaint-approval-routing.client.view.html'
    })

        .state('complaints.billing', {
            url: '/:id/billing',
            templateUrl: 'modules/complaints/views/components/complaint-billing.client.view.html'
        })
}

]).run([ 'Helper.DashboardService', function(DashboardHelper) {
    DashboardHelper.addLocales();
} ]);
