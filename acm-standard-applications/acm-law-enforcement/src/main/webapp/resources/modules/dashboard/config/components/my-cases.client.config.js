'use strict';

angular.module('dashboard.my-cases', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('myCases', {
                title: 'dashboard.widgets.myCases.title',
                description: 'dashboard.widgets.myCases.description',
                controller: 'Dashboard.MyCasesController',
                controllerAs: 'myCases',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/my-cases.client.view.html'
            });
    });