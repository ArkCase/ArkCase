'use strict';

angular.module('dashboard.cost', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('cost', {
                title: 'dashboard.widgets.cost.title',
                description: 'dashboard.widgets.cost.description',
                controller: 'Dashboard.CostController',
                controllerAs: 'cost',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cost-widget.client.view.html',
                commonName: 'cost'
            });
    })
    .controller('Dashboard.CostController', ['$scope', 'config', '$state', '$stateParams', '$translate'
        , 'Dashboard.DashboardService', 'Helper.ObjectBrowserService', 'UtilService', 'Object.CostService'
        , function ($scope, config, $state, $stateParams, $translate
            , DashboardService, HelperObjectBrowserService, Util, ObjectCostService
        ) {

            var vm = this;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                ObjectCostService.queryCostsheets($stateParams.type, currentObjectId).then(
                    function (costsheets) {
                        for (var i = 0; i < costsheets.length; i++) {
                            costsheets[i].acm$_formName = $translate.instant("cases.comp.cost.formNamePrefix") + " " + Util.goodValue(costsheets[i].parentNumber);
                            costsheets[i].acm$_costs = _.reduce(Util.goodArray(costsheets[i].costs), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        var chartData = [];
                        var labels = [];

                        _.forEach(costsheets, function (costIter) {
                            labels.push(costIter.user.fullName);
                            chartData.push(costIter.acm$_costs);
                        });

                        vm.showChart = chartData.length > 0;
                        vm.data = [chartData];
                        vm.labels = labels;
                    }
                );
            }
        }
    ]);