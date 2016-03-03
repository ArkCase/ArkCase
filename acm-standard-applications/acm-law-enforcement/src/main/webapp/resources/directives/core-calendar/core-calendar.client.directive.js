/**
 * Created by dragan.simonovski on 3/3/2016.
 */
'use strict';
/**
 * @ngdoc directive
 * @name global.directive:coreCalendar
 * @restrict E
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/core-calendar/core-calendar.client.directive.js directives/core-calendar/core-calendar.client.directive.js}
 *
 * The "Core-Calendar" calendar functionality to the view
 *
 * @param {Object} config object containing configuration items for current component
 * @param {string} folderId string that is folderId from outlook calendar
 *
 * @example
 <example>
 <file name="index.html">
 <core-calendar config="config" folder-id="folderId"/>
 </file>
 <file name="app.js">
 angular.module('AppModule').controller('AppController', ['$scope', 'ConfigService'
 , function ($scope, ConfigService) {
        $scope.folderId = 'somefolderid';
        $scope.config = {
                "dateFormat": "MM/DD/YYYY HH:mm"
            }
    }
 ]);
 </file>
 </example>
 */
angular.module('directives').directive('coreCalendar', ['$compile', '$translate', 'uiCalendarConfig',
    'Object.CalendarService', 'UtilService',
    function ($compile, $translate, uiCalendarConfig, CalendarService, Util) {
        return {
            restrict: 'E',
            scope: {
                folderId: '=',
                config: '='
            },
            link: function (scope) {

                scope.$watch('folderId', function (folderId, oldValue) {
                    if (folderId && scope.config) {
                        getDataAndRenderCalendar();
                    }
                });

                /* Event source that contains calendar events */
                scope.events = [];

                /* Event sources array */
                scope.eventSources = [scope.events];

                /* Render calendar widget */
                scope.renderCalendar = function (calendar) {
                    if (uiCalendarConfig.calendars[calendar]) {
                        uiCalendarConfig.calendars[calendar].fullCalendar('render');
                    }
                };

                /* Render Popover */
                scope.eventRender = function (event, element, view) {
                    element.attr({
                        'popover-html-unsafe': event.detail,
                        'popover-title': event.title,
                        'popover-trigger': 'mouseenter'
                    });
                    $compile(element)(scope);
                };


                /* Calendar config object */
                scope.uiConfig = {
                    calendar: {
                        height: 450,
                        editable: false,
                        header: {
                            left: 'month agendaWeek agendaDay',
                            center: 'title',
                            right: 'today prev,next'
                        },
                        buttonText: {
                            today: 'Today',
                            month: 'Month',
                            week: 'Week',
                            day: 'Day'
                        },
                        eventRender: scope.eventRender
                    }
                };

                scope.onClickRefresh = function () {
                    //reset cache first
                    CalendarService.resetCalendarEventsCache(scope.folderId);
                    getDataAndRenderCalendar();
                };

                var getDataAndRenderCalendar = function () {
                    CalendarService.queryCalendarEvents(scope.folderId).then(function (calendarEvents) {
                        if (calendarEvents.items) {
                            scope.events.splice(0, scope.events.length);
                            for (var i = 0; i < calendarEvents.items.length; i++) {
                                var calendarEvent = {};
                                calendarEvent.id = calendarEvents.items[i].id;
                                calendarEvent.title = calendarEvents.items[i].subject;
                                calendarEvent.start = calendarEvents.items[i].startDate;
                                calendarEvent.end = calendarEvents.items[i].endDate;
                                calendarEvent.detail = makeDetail(calendarEvents.items[i]);
                                calendarEvent.className = "b-l b-2x b-info";
                                calendarEvent.allDay = calendarEvents.items[i].allDayEvent;
                                scope.events.push(calendarEvent);
                            }
                            scope.renderCalendar('coreCalendar');
                        }
                    });
                };

                var makeDetail = function (calendarItem) {
                    var dateFormat = scope.config.dateFormat;
                    var startLabel = $translate.instant('common.directive.coreCalendar.start.label');
                    var endLabel = $translate.instant('common.directive.coreCalendar.end.label');
                    var body = Util.goodValue(calendarItem.body, '');
                    var startDateTime = Util.getDateTimeFromDatetime(calendarItem.startDate, dateFormat);
                    var endDateTime = Util.getDateTimeFromDatetime(calendarItem.endDate, dateFormat);
                    var detail = body + '</br>' + startLabel + startDateTime + '</br>' + endLabel + endDateTime;
                    return detail;
                };
            },
            templateUrl: 'directives/core-calendar/core-calendar.client.view.html'
        };
    }
]);