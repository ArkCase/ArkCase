'use strict';

angular.module('dashboard.my-overdue-requests', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
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
            templateUrl: 'modules/dashboard/views/components/my-tasks-edit.client.view.html'
        }

    });
});