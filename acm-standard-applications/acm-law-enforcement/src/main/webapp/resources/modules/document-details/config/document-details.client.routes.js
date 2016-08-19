'use strict';

angular.module('document-details').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider
            .state('viewer', {
                url: '/viewer/:id/:containerId/:containerType/:name/:selectedIds',
                templateUrl: 'modules/document-details/views/document-details.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('document-details');
                        $translatePartialLoader.addPart('common');
                        return $translate.refresh();
                    }]
                }
            });
    }
]);