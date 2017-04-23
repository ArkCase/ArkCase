'use strict';

angular.module('dashboard.cases-by-status', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('casesByStatus', {
                title: 'dashboard.widgets.casesByStatus.title',
                description: 'dashboard.widgets.casesByStatus.description',
                controller: 'Dashboard.CasesByStatusController',
                controllerAs: 'casesByStatus',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-status.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/cases-by-status-edit.client.view.html'
                }
            });
    });