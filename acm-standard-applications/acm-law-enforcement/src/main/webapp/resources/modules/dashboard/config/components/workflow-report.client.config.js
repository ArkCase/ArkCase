'use strict';

angular.module('dashboard.workflow-report', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('workflowReport', {
                title: 'dashboard.widgets.workflowReport.title',
                description: 'dashboard.widgets.workflowReport.title.description',
                controller: 'Dashboard.WorkflowReportController',
                controllerAs: 'workflowReport',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/workflow-report.client.view.html',
                resolve: {
                    params: function (config) {
                        return config;
                    }
                },
                edit: {
                    templateUrl: 'modules/dashboard/views/components/workflow-report-edit.client.view.html'
                }
            });
    });