'use strict';

angular.module('dashboard.cases-by-queue', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('casesByQueue', {
                title: 'Cases by Queue',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CasesByQueueController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-queue.client.view.html'
            });
    })
    .controller('Dashboard.CasesByQueueController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'casesByQueue');

            $scope.config = null;
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
                        data: data,
                        cursor: 'pointer',
                        point: {
                            events: {
                                click: onBarClick
                            }
                        }
                    }]
                }
            });

            function onBarClick(e) {
                if ($scope.config.redirectSettings) {
                    var redirectObj = $scope.config.redirectSettings[this.name];
                    if (redirectObj) {
                        $state.go(redirectObj.state, redirectObj.params)
                    }
                }
            }

            function applyConfig(e, componentId, config) {
                if (componentId == 'casesByQueue') {
                    $scope.config = config;
                }
            }
        }
    ]);