'use strict';

angular.module('audit').config(['$stateProvider',
    function ($stateProvider) {
        $stateProvider
            .state('audit', {
                url: '/audit',
                templateUrl: 'modules/audit/views/audit.client.view.html',
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('common');
                        $translatePartialLoader.addPart('audit');
                        $translatePartialLoader.addPart('dashboard');
                        return $translate.refresh();
                    }]
            }
        });
    }
]).run(['Helper.DashboardService', function (DashboardHelper) {
    DashboardHelper.addLocales();
}]);