'use strict';

angular.module('dashboard.workflow-report', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('workflowReport', {
                    title: 'Workflow Report',
                    description: 'Displays Workflow Report',
                    controller: 'Dashboard.WorkflowReportController',
                    controllerAs: 'workflowReport',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/workflow-report.client.view.html',
                    edit: {
                        templateUrl: 'modules/dashboard/views/components/workflow-report-edit.client.view.html'
                    }
                }
            );
    });