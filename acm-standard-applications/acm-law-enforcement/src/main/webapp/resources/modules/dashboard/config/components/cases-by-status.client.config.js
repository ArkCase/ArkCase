'use strict';

angular.module('dashboard.cases-by-status', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('casesByStatus', {
                title: 'Cases by Status',
                description: 'Displays cases by status',
                controller: 'Dashboard.CasesByStatusController',
                controllerAs: 'casesByStatus',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-status.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/cases-by-status-edit.client.view.html'
                }
            });
    });