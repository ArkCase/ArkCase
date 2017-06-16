'use strict';

angular.module('dashboard.my-complaints', ['adf.provider']).config(function (ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('myComplaints', {
        title: 'dashboard.widgets.myComplaints.title',
        description: 'dashboard.widgets.myComplaints.description',
        controller: 'Dashboard.MyComplaintsController',
        controllerAs: 'myComplaints',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-complaints.client.view.html'
    });
});