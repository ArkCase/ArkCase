'use strict';

angular.module('dashboard.team-workload', ['adf.provider'])
    .config(function(dashboardProvider){
        dashboardProvider
            .widget('teamTaskWorkload', {
                title: 'Team Tasks Workload',
                description: 'Displays team tasks workload',
                controller: 'Dashboard.TeamWorkloadController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/team-workload.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/team-workload-edit.client.view.html'
                }
            });
    })
    .controller('Dashboard.TeamWorkloadController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService',
        function($scope, config, $translate, DashboardService){
            $scope.chartConfig = null;
            if (!config.due) {
                config.due = 'all';
            }

            // Load Cases info and render chart
            DashboardService.queryTeamWorkload({due: config.due}, function(tasks){

                var chartTitle = '';
                switch(config.due) {
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

                // Count number of assigned tasks for users
                var tasksData = {};
                _.forEach(tasks, function(taskIter){
                    var user = taskIter.assignee;
                    tasksData[user] ? tasksData[user]++ : tasksData[user] = 1;
                });


                var seriesData = [];
                _.forEach(tasksData, function(count, user){
                    seriesData.push([user, count])
                });

                if (seriesData.length > 0) {
                    seriesData.sort(function(a, b){
                        return b[1] - a[1];
                    });

                    var s = seriesData[0];
                    seriesData[0] = {
                        name: s[0],
                        y: s[1],
                        sliced: true,
                        selected: true
                    };

                    $scope.chartConfig = {
                        chart: {
                            plotBackgroundColor: null,
                            plotBorderWidth: null,
                            plotShadow: false
                        },
                        title: {
                            text: chartTitle
                        },
                        plotOptions: {
                            pie: {
                                allowPointSelect: true,
                                cursor: 'pointer'
                            }
                        },
                        series: [{
                            type: 'pie',
                            name: chartTitle,
                            data: seriesData
                        }]
                    }
                };
            });


        }
    ]);