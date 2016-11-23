'use strict';

angular.module('dashboard.team-workload').
controller('Dashboard.TeamWorkloadController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService',
    function ($scope, config, $translate, DashboardService) {

        var vm = this;

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'teamWorkload');

        if (!config.due) {
            config.due = 'all';
        }
        function applyConfig(e, componentId, configuration) {
            if (componentId == 'teamWorkload') {
                DashboardService.queryTeamWorkload({due: config.due}, function (solrData) {

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

                    vm.chartTitle = chartTitle;

                    var tasksData = {};
                    var data = [];
                    var labels = [];

                    // Count number of assigned tasks for users
                    angular.forEach(solrData.response.docs, function (task) {
                        if (task.assignee_id_lcs) {
                            var user = task.assignee_id_lcs;
                            tasksData[user] ? tasksData[user]++ : tasksData[user] = 1;
                        }
                    });

                    angular.forEach(tasksData, function (count, user) {
                        data.push(count);
                        labels.push(user);
                    });

                    vm.showChart = labels.length > 0 && data.length > 0 ? true : false;
                    vm.data = data;
                    vm.labels = labels;
                });
            }
        }
    }
]);