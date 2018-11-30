'use strict';

angular.module('dashboard.my-overdue-case', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('myOverdueCase', {
        title: 'dashboard.widgets.myOverdueCases.title',
        description: 'dashboard.widgets.myOverdueCases.description',
        controller: 'Dashboard.MyOverdueCasesController',
        controllerAs: 'myOverdueCase',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/my-overdue-case.client.view.html'

    });
});