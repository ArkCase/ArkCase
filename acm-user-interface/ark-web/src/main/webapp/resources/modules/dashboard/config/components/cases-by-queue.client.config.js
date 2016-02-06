'use strict';

angular.module('dashboard.cases-by-queue', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('casesByQueue', {
                title: 'Cases by Queue',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CasesByQueueController',
                controllerAs: 'casesByQueue',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-queue.client.view.html'
            });
    });