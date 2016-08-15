'use strict';

angular.module('dashboard.new-cases', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('newCases', {
                title: 'New Cases',
                description: 'Displays new cases',
                controller: 'Dashboard.NewCasesController',
                controllerAs: 'newCases',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/new-cases.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/new-cases-edit.client.view.html'
                }
            });
    })
