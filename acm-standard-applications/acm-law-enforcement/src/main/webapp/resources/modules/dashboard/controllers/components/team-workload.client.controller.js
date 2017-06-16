'use strict';

angular.module('dashboard.team-workload').
controller('Dashboard.TeamWorkloadController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService', 'ConfigService',
    function ($scope, config, $translate, DashboardService, ConfigService) {

        var vm = this;

        if (!config.due) {
            config.due = 'all';
        }

        ConfigService.getComponentConfig("dashboard", "teamWorkload").then(function (config) {
            DashboardService.queryTeamWorkload({due: config.due}, function (tasksByUser) {

                var chartTitle = '';
                switch (config.due) {
                    case 'all':
                        chartTitle = $translate.instant('dashboard.widgets.teamWorkload.dueDate.all');
                        break;
                    case 'pastDue':
                        chartTitle = $translate.instant('dashboard.widgets.teamWorkload.dueDate.pastDue');
                        break;
                    case 'dueTomorrow':
                        chartTitle = $translate.instant('dashboard.widgets.teamWorkload.dueDate.dueTomorrow');
                        break;
                    case 'dueWeek':
                        chartTitle = $translate.instant('dashboard.widgets.teamWorkload.dueDate.dueWeek');
                        break;
                    case 'dueMonth':
                        chartTitle = $translate.instant('dashboard.widgets.teamWorkload.dueDate.dueMonth');
                        break;

                }

                var data = [];
                var labels = [];
                angular.forEach(tasksByUser, function (tasksByUserIter) {
                    if (tasksByUserIter.user) {
                        labels.push(tasksByUserIter.user);
                        data.push(tasksByUserIter.taskCount);
                    }
                });
                vm.showChart = data.length > 0 ? true : false;
                vm.data = data;
                vm.labels = labels;
                vm.chartTitle = chartTitle;
             });
        });
    }
]);