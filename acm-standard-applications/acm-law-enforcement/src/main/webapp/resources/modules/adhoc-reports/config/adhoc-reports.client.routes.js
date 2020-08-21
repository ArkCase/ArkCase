'use strict';

angular.module('adhoc-reports').config(['$stateProvider', function ($stateProvider) {
    $stateProvider.state('adhoc-reports', {
        url: '/adhoc-reports',
        templateUrl: 'modules/adhoc-reports/views/adhoc-reports.client.view.html',
        resolve: {
            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                $translatePartialLoader.addPart('common');
                $translatePartialLoader.addPart('adhoc-reports');
                return $translate.refresh();
            }]
        }
    });
}]);