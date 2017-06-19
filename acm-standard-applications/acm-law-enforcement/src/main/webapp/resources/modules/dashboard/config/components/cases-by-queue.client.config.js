'use strict';

angular.module('dashboard.cases-by-queue', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('casesByQueue', {
                title: 'dashboard.widgets.casesByQueue.title',
                description: 'dashboard.widgets.casesByQueue.description',
                controller: 'Dashboard.CasesByQueueController',
                controllerAs: 'casesByQueue',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-queue.client.view.html'
            });
    });