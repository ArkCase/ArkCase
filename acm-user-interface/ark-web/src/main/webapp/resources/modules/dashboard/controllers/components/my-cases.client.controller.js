'use strict';

angular.module('dashboard.my-cases', ['adf.provider'])
    .config(function(dashboardProvider){
        dashboardProvider
            .widget('myCases', {
                title: 'My Cases',
                description: 'Displays my cases',
                controller: 'Dashboard.MyCasesController',
                controllerAs: 'myCases',
                templateUrl: 'modules/dashboard/views/components/my-cases.client.view.html'
            });
    })
    .controller('Dashboard.MyCasesController', function($scope){

    });