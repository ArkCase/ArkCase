'use strict';

angular.module('dashboard.cost', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('cost', {
                title: 'Cost',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CostController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cost.client.view.html'
            });
    })
    .controller('Dashboard.CostController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'cost');

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
                            noData: $translate.instant('dashboard.widgets.cost.noDataMessage'),
                            xAxis: {
                                type: 'category',
                                title: {
                                    text: $translate.instant('dashboard.widgets.cost.xAxis')
                                }
                            },
                            yAxis: {
                                title: {
                                    text: $translate.instant('dashboard.widgets.cost.yAxis')
                                }
                            },
                            series: [{
                                type: 'column',
                                dataLabels: {
                                    enabled: true,
                                    format: '{point.y}'
                                },
                                name: $translate.instant('dashboard.widgets.cost.title'),
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