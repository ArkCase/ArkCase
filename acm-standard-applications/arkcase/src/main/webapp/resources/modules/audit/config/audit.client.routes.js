'use strict';

angular.module('audit').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('audit', {
        url: '/audit',
        templateUrl: 'modules/audit/views/audit.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', 'Object.LookupService', function($translate, $translatePartialLoader, ObjectLookupService) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('audit');
                $translatePartialLoader.addPart('core');
                $translatePartialLoader.addPart('document-details');

                return $translate.refresh();
            } ]
        }
    });
} ]);