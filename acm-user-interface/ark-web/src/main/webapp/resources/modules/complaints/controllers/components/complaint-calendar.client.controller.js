'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope','$timeout', '$compile', 'uiCalendarConfig', 'Object.CalendarService','Complaint.InfoService',
    function($scope,$timeout, $compile, uiCalendarConfig, CalendarService,ComplaintInfoService) {

        $scope.$emit('req-component-config', 'calendar');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'calendar') {
                $scope.config = config;
            }
        });

        $scope.$on('complaint-updated', function (e, data) {
            if (ComplaintInfoService.validateComplaintInfo(data)) {
                $scope.complaintInfo = data;
            }
        });

        /* Calendar config object */
        $scope.uiConfig = {
            calendar:{
                height: 450,
                editable: true,
                header:{
                    left: 'month agendaWeek agendaDay',
                    center: 'title',
                    right: 'today prev,next'
                },
                buttonText: {
                    today:    'Today',
                    month:    'Month',
                    week:     'Week',
                    day:      'Day'
                }
            }
        };

        /* Event source that contains calendar events */
        $scope.events = [];

        /* Event sources array */
        $scope.eventSources = [$scope.events];

        $scope.$watchCollection('complaintInfo', function(newValue, oldValue){
            if(newValue && newValue.container){
                CalendarService.queryCalendarEvents(newValue.container.calendarFolderId).then(function(calendarEvents){
                    if(calendarEvents.items){
                        for(var i = 0; i < calendarEvents.items.length; i++){
                            var calendarEvent = {};
                            calendarEvent.id = calendarEvents.items[i].id;
                            calendarEvent.title = calendarEvents.items[i].subject;
                            calendarEvent.start = calendarEvents.items[i].startDate;
                            calendarEvent.end = calendarEvents.items[i].endDate;
                            //calendarEvent.detail = Calendar.View.OutlookCalendar.makeDetail(calendarEvents.items[i]);
                            calendarEvent.className = "b-l b-2x b-info";
                            calendarEvent.allDay = calendarEvents.items[i].allDayEvent;
                            $scope.events.push(calendarEvent);
                        }
                    }
                });
            }
        });

        /* Render calendar widget */
        $scope.renderCalender = function(calendar) {
            if(uiCalendarConfig.calendars[calendar]){
                uiCalendarConfig.calendars[calendar].fullCalendar('render');
            }
        };

        $timeout(function () {
            $scope.renderCalender('complaintsCalendar');
        }, 1000);
    }
]);