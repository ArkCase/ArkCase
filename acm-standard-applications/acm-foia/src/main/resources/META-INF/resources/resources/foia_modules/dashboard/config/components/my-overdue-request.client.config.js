'use strict';

angular.module('dashboard.my-overdue-requests', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('myOverdueRequests', {
        title: 'dashboard.widgets.myOverdueRequests.title',
        description: 'dashboard.widgets.myOverdueRequests.description',
        controller: 'Dashboard.MyOverdueRequestsController',
        controllerAs: 'myOverdueRequests',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-overdue-request.client.view.html'

    });
});