'use strict';

angular.module('dashboard.cases-by-status')
    .controller('Dashboard.CasesByStatusController', ['$scope', 'config', '$translate', 'Dashboard.DashboardService', 'ConfigService', 'params', 'UtilService',
        function ($scope, config, $translate, DashboardService, ConfigService, params, Util) {

            var vm = this;

            if (!config.period) {
                config.period = 'all';
            }

            if(!Util.isEmpty( params.description)) {
                $scope.$parent.model.description = " - " + params.description;
            }
            else {
                $scope.$parent.model.description = "";
            }

            ConfigService.getComponentConfig("dashboard", "casesByStatus").then(function (configuration) {
                // Load Cases info and render chart
                DashboardService.queryCasesByStatus({period: config.period}, function (cases) {

                    var chartTitle = '';
                    switch (config.period) {
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

                    var data = [];
                    var labels = [];
                    angular.forEach(cases, function (caseIter) {
                        labels.push(caseIter.status);
                        data.push(caseIter.count);
                    });
                    vm.showChart = data.length > 0 ? true : false;
                    vm.data = data;
                    vm.labels = labels;
                    vm.chartTitle = chartTitle;
                });
            });
        }
    ]);