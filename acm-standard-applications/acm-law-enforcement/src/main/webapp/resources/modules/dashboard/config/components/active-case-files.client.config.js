'use strict';

angular.module('dashboard.active-case-files', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('activeCaseFiles', {
        title: 'dashboard.widgets.activeCaseFiles.title',
        description: 'dashboard.widgets.activeCaseFiles.description',
        controller: 'Dashboard.ActiveCaseFilesController',
        controllerAs: 'activeCaseFiles',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/active-case-files.client.view.html',
        resolve: {
            params: function(config) {
                return config;
            }
        },
        edit: {
            templateUrl: 'modules/dashboard/views/components/active-case-files-edit.client.view.html'
        }
    });
});