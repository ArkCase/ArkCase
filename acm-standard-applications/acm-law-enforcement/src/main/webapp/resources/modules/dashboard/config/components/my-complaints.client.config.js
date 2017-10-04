'use strict';

angular.module('dashboard.my-complaints', ['adf.provider']).config(function (ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('myComplaints', {
        title: 'dashboard.widgets.myComplaints.title',
        description: 'dashboard.widgets.myComplaints.description',
        controller: 'Dashboard.MyComplaintsController',
        controllerAs: 'myComplaints',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-complaints.client.view.html',
        resolve: {
            params: function (config) {
                return config;
            }
        },
        edit: {
            templateUrl: 'modules/dashboard/views/components/my-complaints-edit.client.view.html'
        }

    });
});