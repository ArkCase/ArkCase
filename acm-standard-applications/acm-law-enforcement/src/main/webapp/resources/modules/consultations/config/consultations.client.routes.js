'use strict';

//Setting up route
angular.module('consultations').config([ '$stateProvider', function($stateProvider) {
    // Project state routing
    $stateProvider.state('consultations', {
        url: '/consultations',
        templateUrl: 'modules/consultations/views/consultations.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', 'Config.LocaleService', 'Object.LookupService', function($translate, $translatePartialLoader, LocaleService, ObjectLookupService) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('dashboard');
                $translatePartialLoader.addPart('consultations');
                $translatePartialLoader.addPart('document-details');
                $translatePartialLoader.addPart('preference');
                $translatePartialLoader.addPart('admin');
                $translate.resetDataDict().addDataDictFromLabels(LocaleService.getLabelResources(["consultations", "common"], "en")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("consultationTypes")).addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("priorities"));
                return $translate.refresh();
            } ]
        }
    })

        .state('consultations.id', {
            url: '/:id',
            templateUrl: 'modules/consultations/views/consultations.client.view.html'
        })

        .state('consultations.main', {
            url: '/:id/main',
            templateUrl: 'modules/consultations/views/components/consultation-main.client.view.html',
            params: {
                "type": "CONSULTATIONS"
            }
        })

        .state('consultations.calendar', {
            url: '/:id/calendar',
            templateUrl: 'modules/consultations/views/components/consultation-calendar.client.view.html'
        })

        .state('consultations.cost', {
            url: '/:id/cost',
            templateUrl: 'modules/consultations/views/components/consultation-cost.client.view.html',
            resolve: {
                translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('cost-tracking');
                    return $translate.refresh();
                } ]
            }
        })

        .state('consultations.details', {
            url: '/:id/details',
            templateUrl: 'modules/consultations/views/components/consultation-details.client.view.html'
        })

        .state('consultations.documents', {
            url: '/:id/documents',
            templateUrl: 'modules/consultations/views/components/consultation-documents.client.view.html'
        })

        .state('consultations.history', {
            url: '/:id/history',
            templateUrl: 'modules/common/views/object-history.client.view.html',
            params: {
                "type": "CONSULTATION"
            }
        })

        .state('consultations.notes', {
            url: '/:id/notes',
            templateUrl: 'modules/consultations/views/components/consultation-notes.client.view.html'
        })

        .state('consultations.participants', {
            url: '/:id/participants',
            templateUrl: 'modules/consultations/views/components/consultation-participants.client.view.html'
        })

        .state('consultations.people', {
            url: '/:id/people',
            templateUrl: 'modules/consultations/views/components/consultation-people.client.view.html'
        })

        .state('consultations.organizations', {
            url: '/:id/organizations',
            templateUrl: 'modules/consultations/views/components/consultation-organizations.client.view.html'
        })

        .state('consultations.references', {
            url: '/:id/references',
            templateUrl: 'modules/consultations/views/components/consultation-references.client.view.html'
        })

        .state('consultations.tasks', {
            url: '/:id/tasks',
            templateUrl: 'modules/consultations/views/components/consultation-tasks.client.view.html'
        })

        .state('consultations.time', {
            url: '/:id/time',
            templateUrl: 'modules/consultations/views/components/consultation-time.client.view.html',
            resolve: {
                translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('time-tracking');
                    return $translate.refresh();
                } ]
            }
        })

        .state('consultations.tags', {
            url: '/:id/tags',
            templateUrl: 'modules/consultations/views/components/consultation-tags.client.view.html'
        })

        .state('consultations.billing', {
            url: '/:id/billing',
            templateUrl: 'modules/consultations/views/components/consultation-billing.client.view.html'
        })

        .state('consultations.suggestedConsultations', {
            url: '/:id/suggested',
            templateUrl: 'modules/consultations/views/components/consultation-suggested-consultations.client.view.html'
        })

} ]).run([ 'Helper.DashboardService', function(DashboardHelper) {
    DashboardHelper.addLocales();
} ]);
