'use strict';

angular.module('search').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider.state('search', {
            url: '/search',
            templateUrl: 'modules/search/views/search.client.view.html',
            params: {query: "", isSelected: false},
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', 'Config.LocaleService'
                    , function ($translate, $translatePartialLoader, LocaleService) {
                    $translatePartialLoader.addPart('common');
                    $translatePartialLoader.addPart('search');
                    $translate.addDataDictFromLabels(LocaleService.getLabelResources(["common", "search"], "en"));
                    return $translate.refresh();
                }]
            }
        })
    }
]);