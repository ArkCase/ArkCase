/**
 * Created by nick.ferguson on 1/8/2016.
 */
'use strict';

angular.module('dashboard.calendar', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('calendar', {
                title: 'Calendar',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CalendarController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/calendar.client.view.html'
            });
    })
    .controller('Dashboard.CalendarController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'calendar');

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
                            noData: $translate.instant('dashboard.widgets.calendar.noDataMessage'),
                            xAxis: {
                                type: 'category',
                                title: {
                                    text: $translate.instant('dashboard.widgets.calendar.xAxis')
                                }
                            },
                            yAxis: {
                                title: {
                                    text: $translate.instant('dashboard.widgets.calendar.yAxis')
                                }
                            },
                            series: [{
                                type: 'column',
                                dataLabels: {
                                    enabled: true,
                                    format: '{point.y}'
                                },
                                name: $translate.instant('dashboard.widgets.calendar.title'),
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