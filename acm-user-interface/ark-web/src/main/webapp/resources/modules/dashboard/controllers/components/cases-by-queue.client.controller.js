'use strict';

angular.module('dashboard.cases-by-queue', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('caseFilesByQueue', {
                title: 'Cases by Queue',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CasesByQueueController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-queue.client.view.html'
            });
    })
    .controller('Dashboard.CasesByQueueController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $translate, DashboardService) {
            $scope.chartConfig = null;

            // Load Cases info and render chart
            DashboardService.queryCasesByQueue(function (cases) {

                var data = [];

                _.forEach(cases, function (value, key) {
                    if (key.length > 0 && key[0] != '$') {
                        data.push({
                            name: key,
                            y: value,
                            drilldown: key
                        });
                    }
                });

                data.sort(function(a, b){
                    return b.y - a.y;
                });

                $scope.chartConfig = {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: ' '
                    },
                    noData: $translate.instant('dashboard.widgets.casesByQueue.noDataMessage'),
                    xAxis: {
                        type: 'category',
                        title: {
                            text: $translate.instant('dashboard.widgets.casesByQueue.xAxis')
                        }
                    },
                    yAxis: {
                        title: {
                            text: $translate.instant('dashboard.widgets.casesByQueue.yAxis')
                        }
                    },
                    series: [{
                        type: 'column',
                        dataLabels: {
                            enabled: true,
                            format: '{point.y}'
                        },
                        name: $translate.instant('dashboard.widgets.casesByQueue.title'),
                        data: data
                    }]
                }
            });
        }
    ]);