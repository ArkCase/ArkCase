'use strict';

angular.module('dashboard.time', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('time', {
                title: 'dashboard.widgets.time.title',
                description: 'dashboard.widgets.time.description',
                controller: 'Dashboard.TimeController',
                controllerAs: 'time',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/time-widget.client.view.html',
                commonName: 'time'
            });
    })
    .controller('Dashboard.TimeController', ['$scope', 'config', '$state', '$stateParams', '$translate'
        , 'Dashboard.DashboardService', 'Helper.ObjectBrowserService', 'UtilService', 'Object.TimeService',
        function ($scope, config, $state, $stateParams, $translate, DashboardService, HelperObjectBrowserService
            , Util, ObjectTimeService) {

            var vm = this;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if(Util.goodPositive(currentObjectId, false)) {
                ObjectTimeService.queryTimesheets($stateParams.type, currentObjectId).then(
                    function (timesheets) {
                        for (var i = 0; i < timesheets.length; i++) {
                            timesheets[i].acm$_formName = $translate.instant("cases.comp.time.formNamePrefix")
                                + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
                            timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                return total + Util.goodValue(n.value, 0);
                            }, 0);
                        }

                        var data = {};
                        var chartData = [];
                        var labels = [];

                        _.forEach(timesheets, function (timeIter) {
                            labels.push(timeIter.user.fullName);
                            chartData.push(timeIter.acm$_hours);
                        });

                        vm.showChart = chartData.length > 0;
                        vm.data = [chartData];
                        vm.labels = labels;
                    }
                );
            }
        }

    ]);