'use strict';

angular.module('tags').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('tags', {
        url: '/tags',
        templateUrl: 'modules/tags/views/tags.client.view.html',
        params: {
            query: ""
        },
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('tags');
                return $translate.refresh();
            } ]
        }
    });
} ]);