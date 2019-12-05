'use strict';

angular.module('directives').controller(
        'Directives.CoreCalendarNewEventModalController',
        [ '$scope', '$modal', '$modalInstance', '$translate', 'Object.CalendarService', 'MessageService', 'Util.DateService', 'coreCalendarConfig', 'Directives.CalendarUtilService', 'params', 'Helper.LocaleService', 'UtilService',
                function($scope, $modal, $modalInstance, $translate, CalendarService, MessageService, DateService, coreCalendarConfig, CalendarUtilService, params, LocaleHelper, Util) {
                    new LocaleHelper.Locale({
                        scope: $scope
                    });

                    $scope.objectId = params.objectId;
                    $scope.objectType = params.objectType;
                    $scope.updateMaster = params.updateMaster;

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
                        filesToAttach: [],
                        attachedFiles: []
                    };

                    var requiredAttendees = [];
                    var optionalAttendees = [];

                    CalendarService.getCalendar($scope.objectType, $scope.objectId).then(function(res) {
                        $scope.calendarId = res.data.calendarId;
                    });

                    $scope.onTimeChanged = function(start, end) {
                        var todayDate = new Date();
                        var todayTime = todayDate.getTime();
                        var startTime = start ? start.getTime() : 0;
                        var endTime = end ? end.getTime() : 0;

                        if (startTime < todayTime) {
                            $scope.eventDataModel.start = start;
                        }

                        if (startTime > endTime) {
                            $scope.eventDataModel.end = start;
                        }
                    };

                    /*Set minimum End Date*/
                    $scope.onDateChanged = function(start, end) {
                        $scope.minEndDate = start;

                        var startTime = start.getTime();
                        var endTime = end.getTime();

                        if (startTime > endTime) {
                            $scope.eventDataModel.end = start;
                        }
                    };

                    var buildAttendeesViewModel = function(attendees) {
                        var attendeesViewModel = '';
                        _.forEach(attendees, function(attendee, index) {
                            if (index === 0) {
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
                            backdrop: 'static',
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
                            if (attendeeType === 'REQUIRED') {
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
                        var tmpEventDataModel = $scope.eventDataModel;
                        tmpEventDataModel.start = DateService.isoToLocalDateTime(tmpEventDataModel.start);
                        tmpEventDataModel.end = DateService.isoToLocalDateTime(tmpEventDataModel.end);
                        var params = {
                            eventDataModel: tmpEventDataModel
                        };

                        var modalInstance = $modal.open({
                            animation: $scope.animationsEnabled,
                            templateUrl: 'directives/core-calendar/core-calendar-set-event-recurrence-modal.client.view.html',
                            controller: 'Directives.CoreCalendarSetEventRecurrenceController',
                            size: 'lg',
                            backdrop: 'static',
                            resolve: {
                                $params: function() {
                                    return params;
                                }
                            }
                        });

                        modalInstance.result.then(function(modalData) {
                            if (modalData.modalAction === 'setEventRecurrence') {
                                $scope.eventDataModel.recurrenceDetails = modalData.recurrenceDataModel;
                                $scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString($scope.eventDataModel);
                                $scope.recurrentEvent = true;
                            } else if (modalData.modalAction === 'removeCurrentRecurrence') {
                                $scope.eventDataModel.recurrenceDetails = modalData.recurrenceDataModel;
                                $scope.recurrentEvent = false;
                            }
                        }, function() {

                        });
                    };

                    $scope.removeSelectedFile = function(fileIndex) {
                        $scope.attachmentModel.filesToAttach.splice(fileIndex, 1);
                    };

                    $scope.removeAttachedFile = function(fileIndex, file) {
                        $scope.eventDataModel.files.push(file);
                        $scope.attachmentModel.attachedFiles.splice(fileIndex, 1);
                    };

                    if (!$scope.existingEvent) {
                        /*Set initial Event data*/
                        $scope.eventDataModel = {
                            start: new Date(),
                            end: new Date(),
                            recurrenceDetails: {
                                recurrenceType: 'ONLY_ONCE'
                            },
                            priority: 'NORMAL',
                            allDayEvent: false,
                            sensitivity: 'NORMAL',
                            remindIn: -1
                        };

                        $scope.minEndDate = new Date();
                        $scope.recurrentEvent = false;

                    } else {
                        $scope.eventDataModel = angular.copy($scope.existingEvent);
                        $scope.attachmentModel.attachedFiles = angular.copy($scope.eventDataModel.files);
                        $scope.eventDataModel.files = [];
                        $scope.eventDataModel.start = DateService.isoToDate($scope.eventDataModel.start);
                        $scope.eventDataModel.end = DateService.isoToDate($scope.eventDataModel.end);
                        if ($scope.eventDataModel.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {
                            $scope.eventDataModel.recurrenceDetails.startAt = moment($scope.eventDataModel.recurrenceDetails.startAt).toDate();
                            $scope.eventDataModel.recurrenceDetails.endBy = moment($scope.eventDataModel.recurrenceDetails.endBy).toDate();
                            $scope.recurrentEvent = $scope.updateMaster;
                            $scope.recurrenceDescription = CalendarUtilService.buildEventRecurrenceString($scope.eventDataModel);
                        } else {
                            $scope.originiallyNotRecurrent = true;
                        }

                        splitAttendeesByType($scope.eventDataModel.attendees);
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

                    var processEventDataModel = function() {
                        $scope.eventDataModel.start = DateService.dateToIso(new Date($scope.eventDataModel.start));
                        $scope.eventDataModel.end = DateService.dateToIso(new Date($scope.eventDataModel.end));
                        if ($scope.eventDataModel.recurrenceDetails.startAt && $scope.eventDataModel.recurrenceDetails.startAt instanceof Date) {
                            $scope.eventDataModel.recurrenceDetails.startAt = DateService.dateToIso($scope.eventDataModel.recurrenceDetails.startAt);
                        }
                        if ($scope.eventDataModel.recurrenceDetails.endBy) {
                            $scope.eventDataModel.recurrenceDetails.endBy = DateService.dateToIso($scope.eventDataModel.recurrenceDetails.endBy);
                        }
                        $scope.eventDataModel.attendees = requiredAttendees.concat(optionalAttendees);
                        $scope.eventDataModel.objectId = $scope.objectId;
                        $scope.eventDataModel.objectType = $scope.objectType;
                        $scope.eventDataModel.calendarId = $scope.calendarId;
                    };

                    /*Perform adding of the event to the calendar*/
                    $scope.addEvent = function() {
                        processEventDataModel();
                        CalendarService.createNewEvent($scope.calendarId, $scope.eventDataModel, $scope.attachmentModel.filesToAttach).then(function(res) {
                            MessageService.succsessAction();
                            $modalInstance.close('ADD_EVENT');
                        }, function(err) {
                            $scope.eventDataModel.start = DateService.isoToDate($scope.eventDataModel.start);
                            $scope.eventDataModel.end = DateService.isoToDate($scope.eventDataModel.end);
                            if ($scope.eventDataModel.recurrenceDetails.startAt) {
                                $scope.eventDataModel.recurrenceDetails.startAt = DateService.isoToDate($scope.eventDataModel.recurrenceDetails.startAt);
                            }
                            if ($scope.eventDataModel.recurrenceDetails.endBy) {
                                $scope.eventDataModel.recurrenceDetails.endBy = DateService.isoToDate($scope.eventDataModel.recurrenceDetails.endBy);
                            }
                            MessageService.errorAction();
                        });
                    };

                    /*Perform editing of the event*/
                    $scope.editEvent = function() {
                        processEventDataModel();
                        CalendarService.updateEvent($scope.eventDataModel, $scope.attachmentModel.filesToAttach, $scope.updateMaster).then(function(res) {
                            MessageService.succsessAction();
                            $modalInstance.close('EDIT_EVENT');
                        }, function(err) {
                            $scope.eventDataModel.start = DateService.isoToDate($scope.eventDataModel.start);
                            $scope.eventDataModel.end = DateService.isoToDate($scope.eventDataModel.end);
                            if ($scope.eventDataModel.recurrenceDetails.startAt) {
                                $scope.eventDataModel.recurrenceDetails.startAt = DateService.isoToDate($scope.eventDataModel.recurrenceDetails.startAt);
                            }
                            if ($scope.eventDataModel.recurrenceDetails.endBy) {
                                $scope.eventDataModel.recurrenceDetails.endBy = DateService.isoToDate($scope.eventDataModel.recurrenceDetails.endBy);
                            }

                            MessageService.errorAction();
                        });
                    };

                    $scope.$watch("eventDataModel.start", function(newValue, oldValue, scope) {

                        var dates = DateService.fixStartAndEndDirectiveDates($scope.eventDataModel.start, $scope.eventDataModel.end, oldValue, newValue, $scope.eventDataModel.allDayEvent);

                        if(dates.start){
                            $scope.eventDataModel.start = dates.start;
                        }
                        if(dates.end){
                            $scope.eventDataModel.end = dates.end
                        }
                        $scope.minEndDate = $scope.eventDataModel.start;
                    });

                    $scope.$watch("eventDataModel.end", function(newValue, oldValue, scope) {
                        var endDate = DateService.fixDirectiveEndDate($scope.eventDataModel.start, $scope.eventDataModel.end);
                        if(endDate){
                            $scope.eventDataModel.end = endDate;
                        }
                    });

                    /*Cancel the modal dialog*/
                    $scope.cancel = function() {
                        $modalInstance.dismiss();
                    };
                } ]);