'use strict';

angular.module('dashboard.team-workload', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('teamWorkload', {
                title: 'dashboard.widgets.teamWorkload.title',
                description: 'dashboard.widgets.teamWorkload.description',
                controller: 'Dashboard.TeamWorkloadController',
                controllerAs: 'teamWorkload',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/team-workload.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/team-workload-edit.client.view.html'
                }
            });
    });
