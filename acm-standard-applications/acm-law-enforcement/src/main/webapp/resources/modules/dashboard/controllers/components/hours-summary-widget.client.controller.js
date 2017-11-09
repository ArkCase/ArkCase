'use strict';

angular.module('dashboard.hoursSummary', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('hoursSummary', {
                title: 'dashboard.widgets.hoursSummary.title',
                description: 'dashboard.widgets.hoursSummary.description',
                controller: 'Dashboard.HoursSummaryController',
                controllerAs: 'hoursSummary',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/hours-summary-widget.client.view.html',
                commonName: 'hoursSummary'
            });
    })
    .controller('Dashboard.HoursSummaryController', ['$scope', '$translate', '$stateParams', '$filter'
        , 'UtilService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', 'ConfigService', 'moment'
        , function ($scope, $translate, $stateParams, $filter
            , Util, TimeTrackingInfoService, HelperObjectBrowserService, ConfigService, moment) {

            var vm = this;
            ConfigService.getModuleConfig("preference").then(function (preferences) {
                var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
                if (Util.goodPositive(currentObjectId, false)) {
                    TimeTrackingInfoService.getTimesheetInfo(currentObjectId).then(
                        function (timesheetInfo) {

                            var chartData = [];
                            var labels = [];

                            var times = [];
                            var i = 0;
                            if (timesheetInfo.times.length > 7) {
                                i = timesheetInfo.times.length - 7;
                            }
                            for (i; i < timesheetInfo.times.length; i++) {
                                times.push(timesheetInfo.times[i]);
                            }

                            _.forEach(times, function (timeIter) {

                                // //reformat date to MM-DD-YYYY from (example) "2016-01-10T00:00:00.000-0500"
                                // var date = new moment(timeIter.date);
                                // var formattedDate = date.format(preferences.timeFormat);

                                // Above code is not i18n compliant. `timeFormat` is removed from preferences.
                                // Temporary quick fix for now:
                                var formattedDate = $filter('date')(timeIter.date, "shortDate");

                                labels.push(formattedDate);
                                chartData.push(timeIter.value);
                            });

                            vm.showChart = chartData.length > 0;
                            vm.data = [chartData];
                            vm.labels = labels;
                        }
                    );
                }
            });
        }
    ]);
