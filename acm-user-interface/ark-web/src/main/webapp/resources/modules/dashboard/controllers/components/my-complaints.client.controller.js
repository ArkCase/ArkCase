'use strict';

angular.module('dashboard.my-complaints', ['adf.provider'])
    .config(function(dashboardProvider){
        dashboardProvider
            .widget('myComplaints', {
                title: 'My Complaints',
                description: 'Displays my complaints',
                controller: 'Dashboard.MyComplaintsController',
                controllerAs: 'myComplaints',
                templateUrl: 'modules/dashboard/views/components/my-complaints.client.view.html'
            });
    })
    .controller('Dashboard.MyComplaintsController', function($scope){

    });