'use strict';

angular.module('dashboard.time', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('time', {
                title: 'Time',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.TimeController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/time.client.view.html'
            });
    })
    .controller('Dashboard.TimeController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'time');

            $scope.config = null;
            $scope.chartConfig = null;

            function onBarClick(e) {
                if ($scope.config.redirectSettings) {
                    var redirectObj = $scope.config.redirectSettings[this.name];
                    if (redirectObj) {
                        $state.go(redirectObj.state, redirectObj.params)
                    }
                }
            }

            function applyConfig(e, componentId, config) {
                if (componentId == 'main') {
                    $scope.config = config;

                    // Load Cost info and render chart
                    /****************************************************
                     *Change this with correct calls for cost stuff
                     ****************************************************/
                    DashboardService.queryCasesByQueue(function (cases) {

                        var data = [];

                        _.forEach(cases, function (value, key) {
                            if (key.length > 0 && key[0] != '$') {
                                data.push({
                                    name: _.get($scope.config, 'redirectSettings[' + key + '].title') || key,
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
                            noData: $translate.instant('dashboard.widgets.time.noDataMessage'),
                            xAxis: {
                                type: 'category',
                                title: {
                                    text: $translate.instant('dashboard.widgets.time.xAxis')
                                }
                            },
                            yAxis: {
                                title: {
                                    text: $translate.instant('dashboard.widgets.time.yAxis')
                                }
                            },
                            series: [{
                                type: 'column',
                                dataLabels: {
                                    enabled: true,
                                    format: '{point.y}'
                                },
                                name: $translate.instant('dashboard.widgets.time.title'),
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
                }
            }
        }
    ]);