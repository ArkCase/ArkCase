'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', '$stateParams', '$timeout'
    , 'ConfigService', 'uiCalendarConfig', 'Object.CalendarService', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, $timeout
        , ConfigService, uiCalendarConfig, CalendarService, ComplaintInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "complaints"
            , componentId: "calendar"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onObjectInfoRetrieved: function (complaintInfo) {
                $scope.complaintInfo = complaintInfo;
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