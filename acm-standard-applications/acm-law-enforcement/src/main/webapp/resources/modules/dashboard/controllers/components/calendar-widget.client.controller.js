/**
 * Created by nick.ferguson on 1/8/2016.
 */
'use strict';

angular.module('dashboard.calendar', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('calendar', {
                title: 'dashboard.widgets.calendar.title',
                description: 'dashboard.widgets.calendar.description',
                controller: 'Dashboard.CalendarController',
                controllerAs: 'calendar',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/calendar-widget.client.view.html',
                commonName: 'calendar'
            });
    })
    .controller('Dashboard.CalendarController', ['$scope', '$stateParams', 'Case.InfoService', 'Complaint.InfoService'
        , 'Helper.ObjectBrowserService', 'Object.CalendarService', 'ObjectService', 'Admin.CalendarConfigurationService', 'Util.DateService',
        function ($scope, $stateParams, CaseInfoService, ComplaintInfoService
            , HelperObjectBrowserService, CalendarService, ObjectService, CalendarConfigurationService, DateService) {

            var vm = this;

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    objectType: ObjectService.ObjectTypes.CASE_FILE,
                    objectIdPropertyName: "id",
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    objectType: ObjectService.ObjectTypes.COMPLAINT,
                    objectIdPropertyName: "complaintId",
                    validateInfo: ComplaintInfoService.validateComplaintInfo
                }
            ];

            var module = _.find(modules, function (module) {
                return module.name == $stateParams.type;
            });

            new HelperObjectBrowserService.Component({
                scope: $scope
                , stateParams: $stateParams
                , moduleId: module.configName
                , componentId: "main"
                , retrieveObjectInfo: module.getInfo
                , validateObjectInfo: module.validateInfo
                , onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var onObjectInfoRetrieved = function (objectInfo) {
                var chartData = [];
                var labels = [];
                var today = new Date();
                CalendarService.getCalendarEvents(DateService.dateToIso(today), DateService.dateToIso(today + 7), module.objectType, objectInfo[module.objectIdPropertyName])
                    .then(function (res) {
                        var events = [];
                        _.forEach(res.data, function(value, key) {
                            events.push({
                                id: value.eventId,
                                title: value.subject,
                                start: value.start,
                                end: value.end
                            });
                        });
                          
                        if (events) {
                            /**
                             * create initial data
                             */
                            var calendarChartData = [];
                            var targetDays = getRange(today, today + 7);
                            _.forEach(targetDays, function (day) {
                                calendarChartData.push({day: day, count: 0})
                            });

                            /**
                             * For each of the days we want to look at:
                             * 1. Check if any of the dates of the event are our target dates
                             * 2. If they are, add to count for that day
                             *
                             * TODO: Woefully inefficient, should invert the logic to be:
                             * TODO: 1. Get range of days for calendar event
                             * TODO: 2. Loop through range and add to our target date if the days are the same
                             * TODO: This would make it so you only have to loop through range for every event once,
                             * TODO: but for short events it doesn't matter. This was just
                             * TODO: the quick and dirty way to do it with the amount of time I had.
                             */
                            _.forEach(calendarChartData, function (dataPoint, index) {
                                var dayTime = dataPoint.day.getTime();
                                var count = dataPoint.count;
                                _.forEach(events, function (event) {
                                    var rangeOfEvent = getRange(event.start, event.end);
                                    _.forEach(rangeOfEvent, function (day) {
                                        if (dayTime === day.getTime()) {
                                            count++;
                                        }
                                    });
                                });

                                calendarChartData[index].count = count;
                            });
                        }
                        return calendarChartData ? calendarChartData : null;
                    })
                    .then(function (calendarChartData) {
                        if (calendarChartData) {
                            //TODO: Populate chart data with 'calendarChartData'
                            /** Structure of end data will be:
                             * [
                             *  {day: $day1, count:  $count},
                             *  ...
                             *  {day: $day7, count: $count}
                             * ]
                             **/
                        }
                    });
                vm.showChart = chartData.length > 0;
                vm.data = [chartData];
                vm.labels = labels;
            };

            /**
             * credit: http://stackoverflow.com/a/4413991
             */
            var getRange = function (startDate, endDate, addFn, interval) {

                addFn = addFn || Date.prototype.addDays;
                interval = interval || 1;

                var retVal = [];
                var current = new Date(startDate);

                while (current <= endDate) {
                    retVal.push(new Date(current));
                    current = addFn.call(current, interval);
                }

                return retVal;

            };
            
            CalendarConfigurationService.getCurrentCalendarConfiguration().then(function (calendarAdminConfigRes) {
                vm.isCalendarIntegrationEnabled = calendarAdminConfigRes.data.configurationsByType[module.name].integrationEnabled;
            });
        }
    ]);