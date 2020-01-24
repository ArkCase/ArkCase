'use strict';

angular.module('admin').config([ '$stateProvider', function($stateProvider) {
    $stateProvider.state('admin', {
        url: '/admin',
        templateUrl: 'modules/admin/views/admin.client.view.html',
        resolve: {
            translatePartialLoader: [ '$translate', '$translatePartialLoader', function($translate, $translatePartialLoader) {

                var components = [ 'admin', 'common', 'dashboard', 'audit', 'cases', 'complaints', 'cost-tracking', 'document-details', 'document-repository', 'notifications', 'organizations', 'people', 'profile', 'reports', 'request-info', 'search', 'subscriptions', 'tags', 'tasks', 'time-tracking' ];

                _.forEach(components, function(component) {
                    $translatePartialLoader.addPart(component);
                });

                return $translate.refresh();
            } ]
        }
    })

        .state('admin.view-node', {
            url: '/view-node/:nodeName',
            templateUrl: function($stateParams) {
                return 'modules/admin/views/components/' + $stateParams.nodeName + '.client.view.html';
            }
        })
} ]).run([ 'Helper.DashboardService', function(DashboardHelper) {
    DashboardHelper.addLocales();
} ]);