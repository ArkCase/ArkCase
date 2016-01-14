'use strict';

angular.module('dashboard.hourssummary', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('hourssummary', {
                title: 'Hours Summary Widget',
                description: 'Displays a summary of hours',
                controller: 'Dashboard.HoursSummaryController',
                controllerAs: 'hourssummary',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/hours-summary-widget.client.view.html'
            });
    })
    .controller('Dashboard.HoursSummaryController', ['$scope', '$translate', '$stateParams', 'UtilService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService',
        function ($scope, $translate, $stateParams, Util, TimeTrackingInfoService, HelperObjectBrowserService) {

            var vm = this;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(
                    function (timesheetInfo) {

                        var data = {};
                        var chartData = [];
                        var labels = [];

                        var times = [];
                        var i = 0;
                        if (timesheetInfo.times.length > 7) {
                            var i = timesheetInfo.times.length - 7;
                        }
                        for (i; i < timesheetInfo.times.length; i++) {
                            times.push(timesheetInfo.times[i]);
                        }

                        angular.forEach(times, function (timeIter) {
                            //reformat date to MM-DD-YYYY from (example) "2016-01-10T00:00:00.000-0500"
                            var date = timeIter.date.substring(0, 10);
                            //date = YYYY-MM-DD
                            var year = date.substring(0, 4);
                            var month = date.substring(5, 7);
                            var day = date.substring(8, 10);

                            date = month + "-" + day + "-" + year;

                            labels.push(date);
                            chartData.push(timeIter.value);
                        })

                        vm.showChart = chartData.length > 0 ? true : false;
                        vm.data = [chartData];
                        vm.labels = labels;
                    }
                );
            }
        }
    ]);
