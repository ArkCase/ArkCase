/**
 * Created by nick.ferguson on 1/8/2016.
 */
'use strict';

angular.module('dashboard.calendar', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('calendar', {
                title: 'Calendar',
                description: 'Displays cases files by queue',
                controller: 'Dashboard.CalendarController',
                controllerAs: 'calendar',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/calendar-widget.client.view.html',
                commonName: 'calendar'
            });
    })
    .controller('Dashboard.CalendarController', ['$scope', '$stateParams', 'Case.InfoService', 'Complaint.InfoService'
        , 'Helper.ObjectBrowserService', 'Object.CalendarService', 'ObjectService',
        function ($scope, $stateParams, CaseInfoService, ComplaintInfoService
            , HelperObjectBrowserService, CalendarService, ObjectService) {

            var vm = this;

            var modules = [
                {
                    name: "CASE_FILE",
                    configName: "cases",
                    getInfo: CaseInfoService.getCaseInfo,
                    objectType: ObjectService.ObjectTypes.CASE_FILE,
                    validateInfo: CaseInfoService.validateCaseInfo
                }
                , {
                    name: "COMPLAINT",
                    configName: "complaints",
                    getInfo: ComplaintInfoService.getComplaintInfo,
                    objectType: ObjectService.ObjectTypes.COMPLAINT,
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
                var calendarFolderId = objectInfo.container.calendarFolderId;
                CalendarService.queryCalendarEvents(calendarFolderId)
                    .then(function (calendarEvents) {
                        var events = [];
                        if (calendarEvents.items) {
                            for (var i = 0; i < calendarEvents.items.length; i++) {
                                var calendarEvent = {};
                                calendarEvent.id = calendarEvents.items[i].id;
                                calendarEvent.title = calendarEvents.items[i].subject;
                                calendarEvent.start = calendarEvents.items[i].startDate;
                                calendarEvent.end = calendarEvents.items[i].endDate;
                                events.push(calendarEvent);
                            }

                            /**
                             * create initial data
                             */
                            var calendarChartData = [];
                            var today = new Date();
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
        }
    ]);