'use strict';

angular.module('dashboard.my-tasks', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('myOverdueRequests', {
        title: 'dashboard.widgets.myOverdueRequests.title',
        description: 'dashboard.widgets.myOverdueRequests.description',
        controller: 'Dashboard.MyOverdueRequestsController',
        controllerAs: 'myOverdueRequests',
        reload: true,
        resolve: {
            params: function(config) {
                return config;
            }
        },
        templateUrl: 'modules/dashboard/views/components/my-overdue-request.client.view.html',
        edit: {
            templateUrl: 'modules/dashboard/views/components/my-overdue-request-edit.client.view.html'
        }
    });
});
