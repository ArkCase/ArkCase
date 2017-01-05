'use strict';

angular.module('search').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.state('search', {
            url: '/search',
            templateUrl: 'modules/search/views/search.client.view.html',
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('search');
                    return $translate.refresh();
                }]
            }
        })
            .state('quick-search', {
                url: '/search',
                templateUrl: 'modules/search/views/search.client.view.html',
                params: {query: "", isSelected: false},
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('search');
                        return $translate.refresh();
                    }]
                }
            });
    }
]);