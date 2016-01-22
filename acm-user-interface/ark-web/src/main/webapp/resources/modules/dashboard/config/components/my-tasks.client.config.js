'use strict';

angular.module('dashboard.my-tasks', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('myTasks', {
                    title: 'My Tasks',
                    description: 'Displays my tasks',
                    controller: 'Dashboard.MyTasksController',
                    controllerAs: 'myTasks',
                    reload: true,
                    templateUrl: 'modules/dashboard/views/components/my-tasks.client.view.html',
                    edit: {
                        templateUrl: 'modules/dashboard/views/components/my-tasks-edit.client.view.html'
                    }
                }
            );
    });