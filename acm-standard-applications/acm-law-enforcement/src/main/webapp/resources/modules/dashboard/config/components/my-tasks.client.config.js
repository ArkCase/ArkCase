'use strict';

angular.module('dashboard.my-tasks', ['adf.provider'])
    .config(function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider
            .widget('myTasks', {
                    title: 'dashboard.widgets.myTasks.title',
                    description: 'dashboard.widgets.myTasks.description',
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

