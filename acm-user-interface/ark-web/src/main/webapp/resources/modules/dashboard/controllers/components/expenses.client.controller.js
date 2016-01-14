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
                templateUrl: 'modules/dashboard/views/components/expenses.client.view.html'
            });
    })
    .controller('Dashboard.ExpensesController', ['$scope', 'config', '$state', '$translate', 'UtilService', 'Dashboard.DashboardService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService',
        function ($scope, config, $state, $translate, Util, DashboardService, CostTrackingInfoService, HelperObjectBrowserService) {

            var vm = this;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                CostTrackingInfoService.getCostsheetInfo(currentObjectId).then(
                    function (costsheetInfo) {

                        var parentNumber = {parentNumber: costsheetInfo.parentNumber};
                        var parentType = {parentType: costsheetInfo.parentType};
                        var parentId = {parentId: costsheetInfo.parentId};
                        var costs = angular.copy(costsheetInfo.costs);
                        costs = costs.map(function (obj) {
                            return angular.extend(obj, parentNumber, parentType, parentId);
                        });

                        var data = {};
                        var chartData = []
                        var labels = [];

                        angular.forEach(costs, function (costIter) {
                            labels.push(costIter.title);
                            chartData.push(costIter.value);
                        })

                        vm.showChart = chartData.length > 0 ? true : false;
                        vm.data = [chartData];
                        vm.labels = labels;
                    }
                );
            }
        }
    ]);
