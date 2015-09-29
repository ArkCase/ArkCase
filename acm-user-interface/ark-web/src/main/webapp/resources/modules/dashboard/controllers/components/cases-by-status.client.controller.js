'use strict';

angular.module('dashboard.cases-by-status', ['adf.provider'])
    .config(function(dashboardProvider){
        dashboardProvider
            .widget('casesByStatusSummary', {
                title: 'Cases by Status',
                description: 'Displays cases by status',
                controller: 'Dashboard.CasesByStatusController',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cases-by-status.client.view.html',
                edit: {
                    templateUrl: 'modules/dashboard/views/components/cases-by-status-edit.client.view.html'
                }
            });
    })
    .controller('Dashboard.CasesByStatusController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService',
        function($scope, config, $translate, DashboardService){
            $scope.chartConfig = null;
            if (!config.period) {
                config.period = 'all';
            }

            // Load Cases info and render chart
            DashboardService.queryCasesByStatus({period: config.period}, function(cases){

                var chartTitle = '';
                switch(config.period) {
                    case 'all':
                        chartTitle = $translate.instant('dashboard.widgets.casesByStatus.timePeriod.all');
                        break;
                    case 'lastWeek':
                        chartTitle = $translate.instant('dashboard.widgets.casesByStatus.timePeriod.lastWeek');
                        break;
                    case 'lastMonth':
                        chartTitle = $translate.instant('dashboard.widgets.casesByStatus.timePeriod.lastMonth');
                        break;
                    case 'lastYear':
                        chartTitle = $translate.instant('dashboard.widgets.casesByStatus.timePeriod.lastYear');
                        break;
                }

                var seriesData = [];
                _.forEach(cases, function(caseIter){
                    seriesData.push([caseIter.status, caseIter.count])
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
    }]);