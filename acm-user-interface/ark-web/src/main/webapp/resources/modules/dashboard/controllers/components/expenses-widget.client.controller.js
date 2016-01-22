'use strict';

angular.module('dashboard.expenses', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('expenses', {
                title: 'Expenses',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.ExpensesController',
                controllerAs: 'expenses',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/expenses-widget.client.view.html',
                commonName: 'expenses'
            });
    })
    .controller('Dashboard.ExpensesController', ['$scope', 'config', '$state', '$translate', 'UtilService'
        , 'Dashboard.DashboardService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService',
        function ($scope, config, $state, $translate, Util, DashboardService, CostTrackingInfoService, HelperObjectBrowserService) {

            var vm = this;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(
                    function (costsheetInfo) {
                        var costs = _.cloneDeep.copy(costsheetInfo.costs);

                        var chartData = [];
                        var labels = [];

                        _.forEach(costs, function (costIter) {
                            labels.push(costIter.title);
                            chartData.push(costIter.value);
                        });

                        vm.showChart = chartData.length > 0;
                        vm.data = [chartData];
                        vm.labels = labels;
                    }
                );
            }
        }
    ]);
