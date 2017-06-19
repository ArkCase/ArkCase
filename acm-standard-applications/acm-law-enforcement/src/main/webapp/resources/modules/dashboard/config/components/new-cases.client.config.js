'use strict';

angular.module('dashboard.new-cases', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('newCases', {
                title: 'dashboard.widgets.newCases.title',
                description: 'dashboard.widgets.newCases.description',
                controller: 'Dashboard.NewCasesController',
                controllerAs: 'newCases',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/new-cases.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/new-cases-edit.client.view.html'
                }
            });
    });
