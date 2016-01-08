'use strict';

angular.module('dashboard.expenses', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('expenses', {
                title: 'Expenses',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.ExpensesController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/expenses.client.view.html'
            });
    })
    .controller('Dashboard.ExpensesController', ['$scope', 'config', '$state', '$translate', 'Dashboard.DashboardService',
        function ($scope, config, $state, $translate, DashboardService) {
            $scope.$on('component-config', applyConfig);
            $scope.$emit('req-component-config', 'expenses');

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
                            noData: $translate.instant('dashboard.widgets.expenses.noDataMessage'),
                            xAxis: {
                                type: 'category',
                                title: {
                                    text: $translate.instant('dashboard.widgets.expenses.xAxis')
                                }
                            },
                            yAxis: {
                                title: {
                                    text: $translate.instant('dashboard.widgets.expenses.yAxis')
                                }
                            },
                            series: [{
                                type: 'column',
                                dataLabels: {
                                    enabled: true,
                                    format: '{point.y}'
                                },
                                name: $translate.instant('dashboard.widgets.expenses.title'),
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