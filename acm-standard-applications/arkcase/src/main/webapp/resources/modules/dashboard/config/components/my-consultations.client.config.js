'use strict';

angular.module('dashboard.my-consultations', [ 'adf.provider' ]).config(function(ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget('myConsultations', {
        title: 'dashboard.widgets.myConsultations.title',
        description: 'dashboard.widgets.myConsultations.description',
        controller: 'Dashboard.MyConsultationsController',
        controllerAs: 'myConsultations',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-consultations.client.view.html',
        resolve: {
            params: function(config) {
                return config;
            }
        },
        edit: {
            templateUrl: 'modules/dashboard/views/components/my-consultations-edit.client.view.html'
        }
    });
});