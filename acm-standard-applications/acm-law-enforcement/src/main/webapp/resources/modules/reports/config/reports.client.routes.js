'use strict';

angular.module('reports').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('reports', {
        url: '/reports',
        templateUrl: 'modules/reports/views/reports.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', 'Object.LookupService', function($translate, $translatePartialLoader, ObjectLookupService) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('reports');
                $translatePartialLoader.addPart('document-details');
                $translate.resetDataDict().addDataDictFromLookup(ObjectLookupService.getLookupByLookupName("reportStates"));
                return $translate.refresh();
            } ]
        }
    });
} ]);
