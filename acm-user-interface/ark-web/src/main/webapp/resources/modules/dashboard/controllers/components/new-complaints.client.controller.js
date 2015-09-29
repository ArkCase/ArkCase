'use strict';

angular.module('dashboard.new-complaints', ['adf.provider'])
    .config(function(dashboardProvider){
        dashboardProvider
            .widget('newComplaints', {
                title: 'New Complaints',
                description: 'Displays new complaints',
                controller: 'Dashboard.NewComplaintsController',
                controllerAs: 'newComplaints',
                templateUrl: 'modules/dashboard/views/components/new-complaints.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/new-complaints-edit.client.view.html'
                }
            });
    })
    .controller('Dashboard.NewComplaintsController', function($scope){

    });