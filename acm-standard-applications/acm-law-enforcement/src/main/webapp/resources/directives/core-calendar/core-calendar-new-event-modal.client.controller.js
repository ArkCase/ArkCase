'use strict';

angular.module('directives').controller('Directives.CoreCalendarNewEventModalController', ['$scope', '$modalInstance', 'Object.CalendarService', 'MessageService', 'Util.DateService', 'coreCalendarConfig', '$modal', 'Directives.CalendarUtilService',
    function($scope, $modalInstance, CalendarService, MessageService, DateService, coreCalendarConfig, $modal, CalendarUtilService) {

        $scope.formStep = 1;
        $scope.formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepOneTitle';

        $scope.priorityOptions = CalendarUtilService.PRIORITY_OPTIONS;
        $scope.reminderOptions = CalendarUtilService.REMINDER_OPTIONS;

        $scope.summernoteOptions = {
            focus: true,
            dialogsInBody: true,
            height: 300
        };

        $scope.timePickerModel = {};
        $scope.attachmentModel = {
            files: []
        };

        var requiredAttendees = [];
        var optionalAttendees = [];

        /*Make start/end time with intervals of 30 minutes*/
        var setInitialStartEndTime = function() {
            var currentMinutes = $scope.eventDataModel.start.getMinutes();
            var currentHours = $scope.eventDataModel.start.getHours();

            if (currentMinutes < 30) {
                $scope.eventDataModel.start.setMinutes(30);
                $scope.eventDataModel.end.setMinutes(0);
                $scope.eventDataModel.end.setHours(currentHours + 1);
            } else {
                $scope.eventDataModel.start.setMinutes(0);
                $scope.eventDataModel.start.setHours(currentHours + 1);
                $scope.eventDataModel.end.setMinutes(30);
                $scope.eventDataModel.end.setHours(currentHours + 1);
            }
            $scope.eventDataModel.start.setSeconds(0);
            $scope.eventDataModel.end.setSeconds(0);
        };

        $scope.onTimeChanged = function(start, end) {
            var startTime = start ? start.getTime() : 0;
            var endTime = end ? end.getTime() : 0;

            if(startTime > endTime) {
                $scope.eventDataModel.end = start;
            }
        };

        /*Set minimum End Date*/
        $scope.onDateChanged = function(start, end) {
            $scope.minEndDate = start;

            var startTime = start.getTime();
            var endTime = end.getTime();

            if(startTime > endTime) {
                $scope.eventDataModel.end = start;
            }
        };

        var buildAttendeesViewModel = function(attendees) {
            var attendeesViewModel = '';
            _.forEach(attendees, function(attendee, index){
                if(index === 0) {
                    attendeesViewModel = attendee.email;
                } else {
                    attendeesViewModel = attendeesViewModel + '; ' + attendee.email;
                }
            });

            return attendeesViewModel;
        };

        var splitAttendeesByType = function(attendees) {
            requiredAttendees = _.filter(attendees, function(attendee) {
                return attendee.type === 'REQUIRED';
            });
            $scope.requiredAttendeesViewModel = buildAttendeesViewModel(requiredAttendees);

            optionalAttendees = _.filter(attendees, function(attendee) {
                return attendee.type === 'OPTIONAL';
            });
            $scope.optionalAttendeesViewModel = buildAttendeesViewModel(optionalAttendees);
        };

        $scope.chooseAttendees = function(attendeeType) {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'directives/core-calendar/core-calendar-choose-event-attendees-modal.client.view.html',
                controller: 'Directives.CoreCalendarChooseEventAttendeesController',
                size: 'lg',
                resolve: {
                    $config: function() {
                        return coreCalendarConfig.chooseEventAttendeesDialog.dialogUserPicker;
                    },
                    attendeeType: function() {
                        return attendeeType;
                    },
                    attendees: function() {
                        return attendeeType === 'REQUIRED' ? requiredAttendees : optionalAttendees;
                    }
                }
            });

            modalInstance.result.then(function(attendees) {
                if(attendeeType === 'REQUIRED') {
                    requiredAttendees = attendees;
                    $scope.requiredAttendeesViewModel = buildAttendeesViewModel(requiredAttendees);
                } else {
                    optionalAttendees = attendees;
                    $scope.optionalAttendeesViewModel = buildAttendeesViewModel(optionalAttendees);
                }

            }, function() {

            });
        };

        $scope.setEventRecurrence = function() {
            var params = {
                eventDataModel: $scope.eventDataModel
            };

            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'directives/core-calendar/core-calendar-set-event-recurrence-modal.client.view.html',
                controller: 'Directives.CoreCalendarSetEventRecurrenceController',
                size: 'lg',
                resolve: {
                    $params: function() {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function(modalData) {
                if(modalData.modalAction === 'setEventRecurrence') {
                    $scope.eventDataModel.recurrenceDetails = modalData.recurrenceDataModel;
                    $scope.eventDataModel.start = modalData.eventStartDate;
                    $scope.eventDataModel.end = modalData.eventEndDate;
                    var recurrenceEnd = modalData.recurrenceEnd;
                    $scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString($scope.eventDataModel, $scope.eventDataModel.start, recurrenceEnd);
                    $scope.recurrentEvent = true;
                } else if(modalData.modalAction === 'removeCurrentRecurrence') {
                    $scope.eventDataModel.recurrenceDetails = modalData.recurrenceDataModel;
                    $scope.eventDataModel.start = modalData.eventStartDate;
                    $scope.eventDataModel.end = modalData.eventEndDate;
                    $scope.minEndDate = $scope.eventDataModel.start;
                    $scope.recurrentEvent = false;
                }
            }, function() {

            });
        };

        $scope.removeSelectedFile = function(fileIndex) {
            $scope.attachmentModel.files.splice(fileIndex, 1);
        };

        $scope.removeAttachedFile = function(fileIndex) {
            $scope.eventDataModel.files.splice(fileIndex, 1);
        };

        // $scope.existingEvent = {
        //     "subject" : "test",
        //     "location" : "Armedia",
        //     "start" : new Date(),
        //     "end" : new Date(),
        //     "allDayEvent" : false,
        //     "recurrenceDetails" : {
        //         "recurrenceType" : "ONLY_ONCE"
        //     },
        //     "details" : "details",
        //     "remindIn" : 30,
        //     "privateEvent" : false,
        //     "priority" : "LOW",
        //     "sendEmails" : false,
        //     "invitees" : [ "aron@armedia.com", "bob@armedia.com", "charlie@armedia.com" ],
        //     "files": []
        // };

        if(!$scope.existingEvent) {
            /*Set initial Event data*/
            $scope.eventDataModel = {
                start: new Date(),
                end: new Date(),
                recurrenceDetails: {
                    recurrenceType: 'ONLY_ONCE'
                },
                priority: 'NORMAL',
                allDayEvent: false,
                privateEvent: false,
                remindIn: 'NONE',
                files: [
                    {
                        name: 'file_name_1.jpg'
                    },
                    {
                        name: 'file_name_2.jpg'
                    },
                    {
                        name: 'file_name_3.jpg'
                    }
                ]
            };

            $scope.minStartDate = new Date();
            $scope.minEndDate = new Date();
            $scope.recurrentEvent = false;

            setInitialStartEndTime();
        } else {
            $scope.eventDataModel = angular.copy($scope.existingEvent);

            $scope.eventDataModel.start = DateService.isoToDate($scope.eventDataModel.start);
            $scope.eventDataModel.end = DateService.isoToDate($scope.eventDataModel.end);

            if($scope.eventDataModel.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {
                $scope.recurrentEvent = true;
                $scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString($scope.eventDataModel, $scope.eventDataModel.start);
            }

            splitAttendeesByType($scope.eventDataModel.attendees);

            setInitialStartEndTime();
        }

        /*Form navigation*/
        var changeFormTitle = function(formStep) {
            var formTitle = '';

            switch (formStep) {
                case 1:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepOneTitle';
                    break;
                case 2:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepTwoTitle';
                    break;
                case 3:
                    formTitle = 'common.directive.coreCalendar.addNewEventDialog.stepThreeTitle';
                    break;
            }

            return formTitle;
        };

        $scope.previousFormStep = function() {
            $scope.formStep -= 1;
            $scope.formTitle = changeFormTitle($scope.formStep);
        };

        $scope.nextFormStep = function() {
            $scope.formStep += 1;
            $scope.formTitle = changeFormTitle($scope.formStep);
        };

        /*Perform adding of the event to the calendar*/
        $scope.addEvent = function() {
            $scope.eventDataModel.start = DateService.dateToIso($scope.eventDataModel.start);
            $scope.eventDataModel.end = DateService.dateToIso($scope.eventDataModel.end);
            $scope.eventDataModel.recurrenceDetails.endBy = DateService.dateToIso($scope.eventDataModel.recurrenceDetails.endBy);
            $scope.eventDataModel.attendees = requiredAttendees.concat(optionalAttendees);
            CalendarService.createNewEvent($scope.eventDataModel, $scope.attachmentModel.files).then(function(res) {
                //Handle success when backend service is completed
                MessageService.succsessAction();
                $modalInstance.close('eventAdded');
            }, function(err) {
                //TO DO
                MessageService.errorAction();
            });
        };

        /*Cancel the modal dialog*/
        $scope.cancel = function() {
            $modalInstance.dismiss();
        };
    }
]);