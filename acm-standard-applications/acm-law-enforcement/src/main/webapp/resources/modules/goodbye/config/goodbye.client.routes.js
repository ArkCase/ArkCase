'use strict';

angular.module('goodbye').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.state('goodbye', {
            url: '/goodbye',
            templateUrl: 'modules/goodbye/views/goodbye.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('goodbye');
                    return $translate.refresh();
                }]
            }
        });
    }
]);