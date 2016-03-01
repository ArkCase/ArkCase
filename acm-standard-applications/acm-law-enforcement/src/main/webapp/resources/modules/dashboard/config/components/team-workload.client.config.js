'use strict';

angular.module('dashboard.team-workload', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('teamWorkload', {
                title: 'Team Tasks Workload',
                description: 'Displays team tasks workload',
                controller: 'Dashboard.TeamWorkloadController',
                controllerAs: 'teamWorkload',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/team-workload.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/team-workload-edit.client.view.html'
                }
            });
    });
