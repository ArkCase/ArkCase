'use strict';

angular.module('dashboard.new-complaints', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('newComplaints', {
                title: 'dashboard.widgets.newComplaints.title',
                description: 'dashboard.widgets.newComplaints.description',
                controller: 'Dashboard.NewComplaintsController',
                controllerAs: 'newComplaints',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/new-complaints.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/new-complaints-edit.client.view.html'
                }
            });
    });