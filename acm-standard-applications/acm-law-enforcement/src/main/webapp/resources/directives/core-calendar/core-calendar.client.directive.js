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
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/core-calendar/core-calendar.client.directive.js directives/core-calendar/core-calendar.client.directive.js}
 *
 * The "Core-Calendar" calendar functionality to the view
 *
 * @param {string} objectType string that is the type of the object
 * @param {int} objectId the id of the object
 *
 */
angular.module('directives').directive('coreCalendar', ['$compile', '$translate', 'uiCalendarConfig',
    'Object.CalendarService', '$modal', 'ConfigService', 'MessageService', 'Directives.CalendarUtilService', 'Util.DateService',
    function($compile, $translate, uiCalendarConfig, CalendarService, $modal, ConfigService, MessageService, CalendarUtilService, DateService) {
        return {
            restrict: 'E',
            templateUrl: 'directives/core-calendar/core-calendar.client.view.html',
            scope: {
                objectId: '=',
                objectType: '='
            },
            link: function(scope) {
                /* Event sources array */
                scope.eventSources = [];

                /* Render Popover */
                scope.eventRender = function(event, element, view) {
                    element.attr({
                        'popover-html-unsafe': event.popoverTemplate,
                        'popover-title': event.title,
                        'popover-trigger': 'mouseenter',
                        'popover-append-to-body': true
                    });
                    $compile(element)(scope);
                };

                ConfigService.getModuleConfig('common').then(function(moduleConfig) {
                    scope.coreCalendarConfig = moduleConfig.coreCalendar;
                });

                /* Add Event Modal */
                scope.addNewEvent = function() {
                    var modalInstance = $modal.open({
                        animation: true,
                        templateUrl: 'directives/core-calendar/core-calendar-new-event-modal.client.view.html',
                        controller: 'Directives.CoreCalendarNewEventModalController',
                        size: 'lg',
                        backdrop: 'static',
                        resolve: {
                            coreCalendarConfig: function() {
                                return scope.coreCalendarConfig;
                            },
                            params: function() {
                                return {
                                    objectType: scope.objectType,
                                    objectId: scope.objectId
                                };
                            }
                        }
                    });

                    modalInstance.result.then(function(result) {
                        scope.onClickRefresh();
                    }, function() {

                    });
                };

                scope.eventClick = function(event, element, view) {
                    CalendarService.getCalendarEventDetails(scope.objectType, scope.objectId, event.id, false).then(function(res) {
                        var modalInstance = $modal.open({
                            animation: true,
                            templateUrl: 'directives/core-calendar/core-calendar-event-details-modal.client.vew.html',
                            controller: 'Directives.CoreCalendarEventDetailsModalController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                coreCalendarConfig: function() {
                                    return scope.coreCalendarConfig;
                                },
                                eventDetails: function() {
                                    return res.data;
                                },
                                params: function() {
                                    return {
                                        objectType: scope.objectType,
                                        objectId: scope.objectId
                                    };
                                }
                            }
                        });

                        modalInstance.result.then(function(result) {
                            scope.onClickRefresh();
                        }, function() {

                        });
                    }, function(err) {
                        MessageService.errorAction();
                    });
                };

                scope.onClickRefresh = function() {
                    //reset cache first
                    CalendarService.resetCalendarEventsCache(scope.objectType, scope.objectId);
                    uiCalendarConfig.calendars.coreCalendar.fullCalendar('refetchEvents');
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
                        eventRender: scope.eventRender,
                        eventClick: scope.eventClick,
                        timeFormat: 'h:mm A',
                        timezone: "local",
                        ignoreTimezone: false
                    }
                };

                scope.uiConfig.calendar.events = function(start, end, timezone, callback) {
                    CalendarService.getCalendarEvents(DateService.dateToIso(start.toDate()), DateService.dateToIso(end.toDate()), scope.objectType, scope.objectId)
                        .then(function(res) {
                            var events = [];
                            _.forEach(res.data, function(value, key) {
                                events.push({
                                    id: value.eventId,
                                    title: value.subject,
                                    start: value.start,
                                    end: value.end,
                                    className: 'b-l b-2x b-info cursor-pointer',
                                    allDay: value.allDayEvent,
                                    popoverTemplate: CalendarUtilService.buildPopoverTemplate(value)
                                });
                            });
                            callback(events);
                        });
                };

            }
        };
    }
]);