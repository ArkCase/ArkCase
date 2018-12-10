'use strict';

angular.module('dashboard.my-overdue-cases', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('myOverdueCases', {
        title: 'dashboard.widgets.myOverdueCases.title',
        description: 'dashboard.widgets.myOverdueCases.description',
        controller: 'Dashboard.MyOverdueCasesController',
        controllerAs: 'myOverdueCases',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-overdue-case.client.view.html'

    });
});