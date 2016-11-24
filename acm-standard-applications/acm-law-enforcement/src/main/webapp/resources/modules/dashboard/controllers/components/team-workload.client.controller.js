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
                        labels.push(tasksByUserIter.user);
                        data.push(tasksByUserIter.taskCount);
                    });
                    vm.showChart = data.length > 0 ? true : false;
                    vm.data = data;
                    vm.labels = labels;
                    vm.chartTitle = chartTitle;
                 });
            }
        }
    }
]);