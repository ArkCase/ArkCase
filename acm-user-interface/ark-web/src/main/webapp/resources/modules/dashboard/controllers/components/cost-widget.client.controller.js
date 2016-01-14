'use strict';

angular.module('dashboard.cost', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('cost', {
                title: 'Cost',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CostController',
                controllerAs: 'cost',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/cost-widget.client.view.html'
            });
    })
    .controller('Dashboard.CostController', ['$scope', 'config', '$state', '$stateParams', '$translate', 'Dashboard.DashboardService', 'Helper.ObjectBrowserService', 'UtilService', 'Object.CostService',
        function ($scope, config, $state, $stateParams, $translate, DashboardService, HelperObjectBrowserService, Util, ObjectCostService) {

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

                        var data = [];
                        var labels = [];

                        angular.forEach(costsheets, function (costIter) {
                            labels.push(costIter.user.fullName);
                            data.push(costIter.acm$_costs);
                        })

                        vm.showChart = data.length > 0 ? true : false;
                        vm.data = data;
                        vm.labels = labels;
                    }
                );
            }
        }
    ]);