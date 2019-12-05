'use strict';

angular.module('directives').controller(
        'Directives.CoreCalendarSetEventRecurrenceController',
        [
                '$scope',
                '$params',
                '$modal',
                '$modalInstance',
                '$translate',
                'Object.CalendarService',
                'MessageService',
                'Util.DateService',
                'Directives.CalendarUtilService',
                'Helper.LocaleService',
                'UtilService',
                function($scope, $params, $modal, $modalInstance, $translate, CalendarService, MessageService, DateService, CalendarUtilService, LocaleHelper, Util) {
                    new LocaleHelper.Locale({
                        scope: $scope
                    });

                    /*Init dropdown select options*/
                    $scope.repeatsOptions = CalendarUtilService.RECURRENCE_OPTIONS;
                    $scope.daysOfTheWeekOptions = CalendarUtilService.DAYS_WEEK_OPTIONS;
                    $scope.monthsOptions = CalendarUtilService.MONTHS_OPTIONS;
                    $scope.relativeDayOptions = CalendarUtilService.RELATIVE_RECURRENCE_DAY_OPTIONS;
                    $scope.dayOuccurrenceInMonthOptions = CalendarUtilService.DAY_OCCURRENCE_IN_MONTH_OPTIONS;

                    /*Init variables*/
                    var eventDataModel = $params.eventDataModel;
                    $scope.recurrentEvent = eventDataModel.recurrenceDetails.recurrenceType === 'ONLY_ONCE' ? false : true;

                    $scope.selectedDayOfTheWeek = [];

                    var eventDurationInMinutes = moment(eventDataModel.end).diff(moment(eventDataModel.start)) / 60000;
                    $scope.validDuration = true;

                    $scope.recurrenceTmpModel = {
                        start: eventDataModel.start,
                        end: eventDataModel.end,
                        minStartDate: new Date(),
                        minEndDate: eventDataModel.start
                    };

                    /*Invoked when date and time of event recurrence is changed*/
                    $scope.onTimeChanged = function(start, end) {
                        var startTime = start ? start.getTime() : 0;
                        var endTime = end ? end.getTime() : 0;

                        if (startTime > endTime) {
                            $scope.recurrenceTmpModel.end = start;
                        }

                        eventDurationInMinutes = moment($scope.recurrenceTmpModel.end).diff(moment($scope.recurrenceTmpModel.start)) / 60000;
                        $scope.validDuration = true;
                    };

                    $scope.onDateChanged = function(start, end) {
                        $scope.recurrenceTmpModel.minEndDate = start;

                        var startTime = start ? start.getTime() : 0;
                        var endTime = end ? end.getTime() : 0;

                        if (startTime > endTime) {
                            $scope.recurrenceTmpModel.end = start;
                        }

                        eventDurationInMinutes = moment($scope.recurrenceTmpModel.end).diff(moment($scope.recurrenceTmpModel.start)) / 60000;
                        $scope.validDuration = true;

                        switch ($scope.recurrenceType) {
                        case 'DAILY':
                            setDefaultDailyRecurrencePattern();
                            break;
                        case 'WEEKLY':
                            setDefaultWeeklyRecurrencePattern();
                            break;
                        case 'MONTHLY':
                            setDefaultMonthlyRecurrencePattern();
                            break;
                        case 'YEARLY':
                            setDefaultYearlyRecurrencePattern();
                            break;
                        }
                    };

                    /**
                     * Invoked when some of the inputs for recurrence pattern and range is changed. Based on their values it calculates
                     * the recurrence start date and the end of the recurrence series.
                     */
                    $scope.onDailyRecurrencePatternChange = function(dailyRecurrencePatternType) {
                        var recurrenceRange = {};

                        if (dailyRecurrencePatternType === 'EVERY_DAY') {
                            recurrenceRange = CalendarUtilService.getEveryDayRecurrenceRange($scope.recurrenceTmpModel.start, $scope.dailyRecurrence.interval, $scope.recurrenceTmpModel.endAfterOccurrances);

                        } else if (dailyRecurrencePatternType === 'EVERY_WEEKDAY') {
                            recurrenceRange = CalendarUtilService.getEveryWeekdayRecurrenceRange($scope.recurrenceTmpModel.start, $scope.recurrenceTmpModel.endAfterOccurrances);
                        }

                        $scope.recurrenceTmpModel.recurrenceEndBy = recurrenceRange.endBy;
                        $scope.recurrenceTmpModel.start = recurrenceRange.start;
                        $scope.recurrenceTmpModel.end = moment($scope.recurrenceTmpModel.start).add(eventDurationInMinutes, 'minutes').toDate();
                        $scope.recurrenceTmpModel.minEndDate = $scope.recurrenceTmpModel.start;
                        $scope.validDuration = true;
                    };

                    $scope.onWeeklyRecurrencePatternChange = function(day) {
                        if (day) {
                            var idx = $scope.selectedDayOfTheWeek.indexOf(day.value);

                            if (idx > -1) {
                                $scope.selectedDayOfTheWeek.splice(idx, 1);
                            } else {
                                $scope.selectedDayOfTheWeek.push(day.value);
                            }
                        }
                        var recurrenceRange = CalendarUtilService.getWeeklyRecurrenceRange(new Date($scope.recurrenceTmpModel.start), $scope.selectedDayOfTheWeek, $scope.weeklyRecurrence.interval, $scope.recurrenceTmpModel.endAfterOccurrances);
                        $scope.recurrenceTmpModel.recurrenceEndBy = recurrenceRange.endBy;
                        $scope.recurrenceTmpModel.start = recurrenceRange.start;
                        $scope.recurrenceTmpModel.end = moment($scope.recurrenceTmpModel.start).add(eventDurationInMinutes, 'minutes').toDate();
                        $scope.recurrenceTmpModel.minEndDate = $scope.recurrenceTmpModel.start;
                        $scope.validDuration = true;
                    };

                    $scope.onMonthlyRecurrencePatternChange = function(monthlyRecurrencePatternType) {
                        var recurrenceRange = {};

                        if (monthlyRecurrencePatternType === 'MONTHLY_ABSOLUTE') {
                            recurrenceRange = CalendarUtilService.getAbsoluteMonthlyRecurrenceRange($scope.recurrenceTmpModel.start, $scope.absoluteMonthlyRecurrence.day, $scope.absoluteMonthlyRecurrence.interval, $scope.recurrenceTmpModel.endAfterOccurrances);
                        } else if (monthlyRecurrencePatternType === 'MONTHLY_RELATIVE') {
                            recurrenceRange = CalendarUtilService.getRelativeMonthlyRecurrenceRange($scope.recurrenceTmpModel.start, $scope.relativeMonthlyRecurrence.dayOuccurrenceInMonth, $scope.relativeMonthlyRecurrence.day, $scope.relativeMonthlyRecurrence.interval,
                                    $scope.recurrenceTmpModel.endAfterOccurrances);
                        }

                        $scope.recurrenceTmpModel.recurrenceEndBy = recurrenceRange.endBy;
                        $scope.recurrenceTmpModel.start = recurrenceRange.start;
                        $scope.recurrenceTmpModel.end = moment($scope.recurrenceTmpModel.start).add(eventDurationInMinutes, 'minutes').toDate();
                        $scope.recurrenceTmpModel.minEndDate = $scope.recurrenceTmpModel.start;
                        $scope.validDuration = true;
                    };

                    $scope.onYearlyRecurrencePatternChange = function(yearlyRecurrencePatternType, monthValue) {
                        var recurrenceRange = {};

                        if (monthValue) {
                            var monthIndex = _.findIndex($scope.monthsOptions, function(month) {
                                return month.value === monthValue;
                            });
                            $scope.maxDaysInMonth = CalendarUtilService.calculateDaysInMonth(monthIndex, $scope.recurrenceTmpModel.start.getFullYear());
                        }

                        if (yearlyRecurrencePatternType === 'YEARLY_ABSOLUTE') {
                            recurrenceRange = CalendarUtilService.getAbsoluteYearlyRecurrenceRange($scope.recurrenceTmpModel.start, $scope.absoluteYearlyRecurrence.month, $scope.absoluteYearlyRecurrence.day, $scope.yearlyRecurrence.interval, $scope.recurrenceTmpModel.endAfterOccurrances);
                        } else if (yearlyRecurrencePatternType === 'YEARLY_RELATIVE') {
                            recurrenceRange = CalendarUtilService.getRelativeYearlyRecurrenceRange($scope.recurrenceTmpModel.start, $scope.relativeYearlyRecurrence.dayOuccurrenceInMonth, $scope.relativeYearlyRecurrence.day, $scope.relativeYearlyRecurrence.month, $scope.yearlyRecurrence.interval,
                                    $scope.recurrenceTmpModel.endAfterOccurrances);
                        }

                        $scope.recurrenceTmpModel.recurrenceEndBy = recurrenceRange.endBy;
                        $scope.recurrenceTmpModel.start = recurrenceRange.start;
                        $scope.recurrenceTmpModel.end = moment($scope.recurrenceTmpModel.start).add(eventDurationInMinutes, 'minutes').toDate();
                        $scope.recurrenceTmpModel.minEndDate = $scope.recurrenceTmpModel.start;
                        $scope.validDuration = true;
                    };

                    $scope.onRecurrenceCountChange = function(recurrenceType, recurrencePattern) {
                        switch (recurrenceType) {
                        case 'DAILY':
                            $scope.onDailyRecurrencePatternChange(recurrencePattern);
                            break;
                        case 'WEEKLY':
                            $scope.onWeeklyRecurrencePatternChange();
                            break;
                        case 'MONTHLY':
                            $scope.onMonthlyRecurrencePatternChange(recurrencePattern);
                            break;
                        case 'YEARLY':
                            $scope.onYearlyRecurrencePatternChange(recurrencePattern);
                            break;
                        }
                    };

                    /*Apply the recurrence pattern and recurrence range when user edits an existing recurrent event*/
                    var applyExistingDailyRecurrencePattern = function(recurrenceDetails) {
                        $scope.recurrenceType = 'DAILY';
                        if (!recurrenceDetails.everyWeekDay) {
                            $scope.recurrenceTmpModel.recurrencePattern = 'EVERY_DAY';
                            $scope.dailyRecurrence = {
                                interval: recurrenceDetails.interval
                            };
                        } else {
                            $scope.recurrenceTmpModel.recurrencePattern = 'EVERY_WEEKDAY';
                            $scope.dailyRecurrence = {
                                interval: 1
                            };
                        }

                        if (!recurrenceDetails.endBy) {
                            $scope.onDailyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                        }
                    };

                    var applyExistingWeeklyRecurrencePattern = function(recurrenceDetails) {
                        $scope.recurrenceType = 'WEEKLY';
                        $scope.recurrenceTmpModel.recurrencePattern = 'WEEKLY';

                        $scope.weeklyRecurrence = {
                            interval: recurrenceDetails.interval
                        };
                        $scope.selectedDayOfTheWeek = recurrenceDetails.days;

                        if (!recurrenceDetails.endBy) {
                            $scope.onWeeklyRecurrencePatternChange();
                        }

                    };

                    var applyExistingMonthlyRecurrencePattern = function(recurrenceDetails) {
                        $scope.recurrenceType = 'MONTHLY';
                        if (!recurrenceDetails.weekOfMonth) {
                            $scope.recurrenceTmpModel.recurrencePattern = 'MONTHLY_ABSOLUTE';
                            $scope.absoluteMonthlyRecurrence = {
                                day: recurrenceDetails.day,
                                interval: recurrenceDetails.interval
                            };
                            $scope.relativeMonthlyRecurrence = {
                                dayOuccurrenceInMonth: CalendarUtilService.calculateDayOccurrenceInMonth($scope.recurrenceTmpModel.start.getDate()),
                                day: $scope.relativeDayOptions[$scope.recurrenceTmpModel.start.getDay() + 3].value,
                                interval: recurrenceDetails.interval
                            };

                        } else {
                            $scope.recurrenceTmpModel.recurrencePattern = 'MONTHLY_RELATIVE';
                            $scope.absoluteMonthlyRecurrence = {
                                day: $scope.recurrenceTmpModel.start.getDate(),
                                interval: recurrenceDetails.interval
                            };
                            $scope.relativeMonthlyRecurrence = {
                                dayOuccurrenceInMonth: recurrenceDetails.weekOfMonth,
                                day: recurrenceDetails.dayOfWeek,
                                interval: recurrenceDetails.interval
                            };
                        }

                        if (!recurrenceDetails.endBy) {
                            $scope.onMonthlyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                        }
                    };

                    var applyExistingYearlyRecurrencePattern = function(recurrenceDetails) {
                        // for some reason, the interval for yearlyRecurrencePattern could not be set
                        // by current Microsoft Exchange implementation in ArkCase
                        if (!recurrenceDetails.interval) {
                            recurrenceDetails.interval = 1;
                        }

                        $scope.recurrenceType = 'YEARLY';
                        $scope.yearlyRecurrence = {
                            interval: recurrenceDetails.interval
                        };
                        if (!recurrenceDetails.weekOfMonth) {
                            $scope.recurrenceTmpModel.recurrencePattern = 'YEARLY_ABSOLUTE';
                            $scope.absoluteYearlyRecurrence = {
                                month: recurrenceDetails.month,
                                day: recurrenceDetails.dayOfMonth
                            };
                            $scope.relativeYearlyRecurrence = {
                                dayOuccurrenceInMonth: CalendarUtilService.calculateDayOccurrenceInMonth($scope.recurrenceTmpModel.start.getDate()),
                                day: $scope.relativeDayOptions[$scope.recurrenceTmpModel.start.getDay() + 3].value,
                                month: $scope.monthsOptions[$scope.recurrenceTmpModel.start.getMonth()].value
                            };

                            $scope.maxDaysInMonth = CalendarUtilService.calculateDaysInMonth(recurrenceDetails.dayOfMonth, $scope.recurrenceTmpModel.start.getFullYear());
                        } else {
                            $scope.recurrenceTmpModel.recurrencePattern = 'YEARLY_RELATIVE';
                            $scope.absoluteYearlyRecurrence = {
                                month: $scope.monthsOptions[$scope.recurrenceTmpModel.start.getMonth()].value,
                                day: $scope.recurrenceTmpModel.start.getDate()
                            };
                            $scope.relativeYearlyRecurrence = {
                                dayOuccurrenceInMonth: recurrenceDetails.weekOfMonth,
                                day: recurrenceDetails.dayOfWeek,
                                month: recurrenceDetails.month
                            };
                        }
                        if (!recurrenceDetails.endBy) {
                            $scope.onYearlyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                        }
                    };

                    var applyExistingRecurrenceRange = function(recurrenceDetails) {
                        if (recurrenceDetails.endBy) {
                            $scope.recurrenceTmpModel.recurrenceRangeType = 'END_BY';
                            $scope.recurrenceTmpModel.endAfterOccurrances = 10;
                            $scope.recurrenceTmpModel.recurrenceEndBy = recurrenceDetails.endBy;
                            $scope.recurrenceTmpModel.minEndDate = $scope.recurrenceTmpModel.start;
                        }

                        if (recurrenceDetails.endAfterOccurrances) {
                            $scope.recurrenceTmpModel.recurrenceRangeType = 'END_AFTER';
                            $scope.recurrenceTmpModel.endAfterOccurrances = recurrenceDetails.endAfterOccurrances;
                        }

                        if (!recurrenceDetails.endBy && !recurrenceDetails.endAfterOccurrances) {
                            $scope.recurrenceTmpModel.recurrenceRangeType = 'NO_END_DATE';
                            $scope.recurrenceTmpModel.endAfterOccurrances = 10;
                        }
                    };

                    /* Set the default recurrence pattern. The default recurrence pattern is based on the event start and end date*/
                    var setDefaultDailyRecurrencePattern = function() {
                        $scope.recurrenceTmpModel.recurrencePattern = 'EVERY_DAY';
                        $scope.dailyRecurrence = {
                            interval: 1
                        };

                        $scope.onDailyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                    };

                    var setDefaultWeeklyRecurrencePattern = function() {
                        $scope.recurrenceTmpModel.recurrencePattern = 'WEEKLY';
                        $scope.weeklyRecurrence = {
                            interval: 1
                        };
                        $scope.selectedDayOfTheWeek = [ $scope.daysOfTheWeekOptions[new Date($scope.recurrenceTmpModel.start).getDay()].value ];

                        $scope.onWeeklyRecurrencePatternChange();
                    };

                    var setDefaultMonthlyRecurrencePattern = function() {
                        $scope.recurrenceTmpModel.recurrencePattern = 'MONTHLY_ABSOLUTE';
                        $scope.absoluteMonthlyRecurrence = {
                            day: $scope.recurrenceTmpModel.start.getDate(),
                            interval: 1
                        };
                        $scope.relativeMonthlyRecurrence = {
                            dayOuccurrenceInMonth: CalendarUtilService.calculateDayOccurrenceInMonth($scope.recurrenceTmpModel.start.getDate()),
                            day: $scope.relativeDayOptions[$scope.recurrenceTmpModel.start.getDay() + 3].value,
                            interval: 1
                        };

                        $scope.onMonthlyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                    };

                    var setDefaultYearlyRecurrencePattern = function() {
                        $scope.recurrenceTmpModel.recurrencePattern = 'YEARLY_ABSOLUTE';
                        $scope.yearlyRecurrence = {
                            interval: 1
                        };
                        $scope.absoluteYearlyRecurrence = {
                            month: $scope.monthsOptions[$scope.recurrenceTmpModel.start.getMonth()].value,
                            day: $scope.recurrenceTmpModel.start.getDate()
                        };
                        $scope.relativeYearlyRecurrence = {
                            dayOuccurrenceInMonth: CalendarUtilService.calculateDayOccurrenceInMonth($scope.recurrenceTmpModel.start.getDate()),
                            day: $scope.relativeDayOptions[$scope.recurrenceTmpModel.start.getDay() + 3].value,
                            month: $scope.monthsOptions[$scope.recurrenceTmpModel.start.getMonth()].value
                        };

                        $scope.maxDaysInMonth = CalendarUtilService.calculateDaysInMonth($scope.recurrenceTmpModel.start.getMonth(), $scope.recurrenceTmpModel.start.getFullYear());

                        $scope.onYearlyRecurrencePatternChange($scope.recurrenceTmpModel.recurrencePattern);
                    };

                    $scope.onRecurrenceTypeChange = function(recurrenceType) {
                        switch (recurrenceType) {
                        case 'DAILY':
                            setDefaultDailyRecurrencePattern();
                            break;
                        case 'WEEKLY':
                            setDefaultWeeklyRecurrencePattern();
                            break;
                        case 'MONTHLY':
                            setDefaultMonthlyRecurrencePattern();
                            break;
                        case 'YEARLY':
                            setDefaultYearlyRecurrencePattern();
                            break;
                        }
                    };

                    /*Apply the default values for all inputs when the modal is loaded*/
                    if (eventDataModel.recurrenceDetails.recurrenceType === 'ONLY_ONCE') {
                        $scope.recurrenceType = 'WEEKLY';
                        $scope.recurrenceTmpModel.recurrenceRangeType = 'NO_END_DATE';
                        $scope.recurrenceTmpModel.endAfterOccurrances = 10;
                        setDefaultWeeklyRecurrencePattern();
                    } /*If the user edit event with existing recurrence, apply the values of the existing recurrence*/
                    else if (eventDataModel.recurrenceDetails.recurrenceType !== 'ONLY_ONCE') {

                        /*Apply the existing recurrence range*/
                        applyExistingRecurrenceRange(eventDataModel.recurrenceDetails);

                        /*Apply the existing recurrence pattern*/
                        switch (eventDataModel.recurrenceDetails.recurrenceType) {
                        case 'DAILY':
                            applyExistingDailyRecurrencePattern(eventDataModel.recurrenceDetails);
                            break;
                        case 'WEEKLY':
                            applyExistingWeeklyRecurrencePattern(eventDataModel.recurrenceDetails);
                            break;
                        case 'MONTHLY':
                            applyExistingMonthlyRecurrencePattern(eventDataModel.recurrenceDetails);
                            break;
                        case 'YEARLY':
                            applyExistingYearlyRecurrencePattern(eventDataModel.recurrenceDetails);
                            break;
                        }
                    }

                    /*=============== FORM ACTIONS ========================*/

                    var checkValidEventDuration = function(eventDuration, recurrencePattern) {
                        var patternDuration = 0;

                        switch (recurrencePattern) {
                        case 'EVERY_DAY':
                            patternDuration = $scope.dailyRecurrence.interval * 1440;
                            break;
                        case 'EVERY_WEEKDAY':
                            patternDuration = $scope.dailyRecurrence.interval * 1440;
                            break;
                        case 'WEEKLY':
                            patternDuration = $scope.weeklyRecurrence.interval * 10080;
                            break;
                        case 'MONTHLY_ABSOLUTE':
                            patternDuration = $scope.absoluteMonthlyRecurrence.interval * 43800;
                            break;
                        case 'MONTHLY_RELATIVE':
                            patternDuration = $scope.relativeMonthlyRecurrence.interval * 43800;
                            break;
                        case 'YEARLY_ABSOLUTE':
                            patternDuration = $scope.yearlyRecurrence.interval * 525600;
                            break;
                        case 'YEARLY_RELATIVE':
                            patternDuration = $scope.yearlyRecurrence.interval * 525600;
                            break;
                        }
                        if (patternDuration < eventDuration) {
                            $scope.validDuration = false;
                        } else {
                            $scope.validDuration = true;
                        }
                    };

                    /*invoked before save to build the model based on the selected values*/
                    var buildRecurrenceDataModel = function(recurrenceType, recurrencePattern, recurrenceRangeType) {

                        var recurrenceDetailsDataModel = {};

                        recurrenceDetailsDataModel.recurrenceType = recurrenceType;

                        switch (recurrencePattern) {
                        case 'EVERY_DAY':
                            recurrenceDetailsDataModel.recurrenceType = 'DAILY';
                            recurrenceDetailsDataModel.interval = $scope.dailyRecurrence.interval;
                            break;
                        case 'EVERY_WEEKDAY':
                            recurrenceDetailsDataModel.recurrenceType = 'DAILY';
                            recurrenceDetailsDataModel.everyWeekDay = true;
                            break;
                        case 'WEEKLY':
                            recurrenceDetailsDataModel.recurrenceType = 'WEEKLY';
                            recurrenceDetailsDataModel.interval = $scope.weeklyRecurrence.interval;
                            recurrenceDetailsDataModel.days = $scope.selectedDayOfTheWeek;
                            break;
                        case 'MONTHLY_ABSOLUTE':
                            recurrenceDetailsDataModel.recurrenceType = 'MONTHLY';
                            recurrenceDetailsDataModel.interval = $scope.absoluteMonthlyRecurrence.interval;
                            recurrenceDetailsDataModel.day = $scope.absoluteMonthlyRecurrence.day;
                            break;
                        case 'MONTHLY_RELATIVE':
                            recurrenceDetailsDataModel.recurrenceType = 'MONTHLY';
                            recurrenceDetailsDataModel.interval = $scope.relativeMonthlyRecurrence.interval;
                            recurrenceDetailsDataModel.weekOfMonth = $scope.relativeMonthlyRecurrence.dayOuccurrenceInMonth;
                            recurrenceDetailsDataModel.dayOfWeek = $scope.relativeMonthlyRecurrence.day;
                            break;
                        case 'YEARLY_ABSOLUTE':
                            recurrenceDetailsDataModel.recurrenceType = 'YEARLY';
                            recurrenceDetailsDataModel.interval = $scope.yearlyRecurrence.interval;
                            recurrenceDetailsDataModel.month = $scope.absoluteYearlyRecurrence.month;
                            recurrenceDetailsDataModel.dayOfMonth = $scope.absoluteYearlyRecurrence.day;
                            break;
                        case 'YEARLY_RELATIVE':
                            recurrenceDetailsDataModel.recurrenceType = 'YEARLY';
                            recurrenceDetailsDataModel.interval = $scope.yearlyRecurrence.interval;
                            recurrenceDetailsDataModel.month = $scope.relativeYearlyRecurrence.month;
                            recurrenceDetailsDataModel.dayOfWeek = $scope.relativeYearlyRecurrence.day;
                            recurrenceDetailsDataModel.weekOfMonth = $scope.relativeYearlyRecurrence.dayOuccurrenceInMonth;
                            break;
                        }

                        recurrenceDetailsDataModel.startAt = DateService.dateToIso(new Date($scope.recurrenceTmpModel.start));

                        switch (recurrenceRangeType) {
                        case 'NO_END_DATE':
                            break;
                        case 'END_AFTER':
                            recurrenceDetailsDataModel.endAfterOccurrances = $scope.recurrenceTmpModel.endAfterOccurrances;
                            recurrenceDetailsDataModel.endBy = $scope.recurrenceTmpModel.recurrenceEndBy;
                            break;
                        case 'END_BY':
                            recurrenceDetailsDataModel.endBy = $scope.recurrenceTmpModel.recurrenceEndBy;
                            break;
                        }

                        return recurrenceDetailsDataModel;
                    };

                    $scope.setEventRecurrence = function() {
                        checkValidEventDuration(eventDurationInMinutes, $scope.recurrenceTmpModel.recurrencePattern);
                        if ($scope.validDuration) {
                            var modalActionData = {
                                recurrenceDataModel: buildRecurrenceDataModel($scope.recurrenceType, $scope.recurrenceTmpModel.recurrencePattern, $scope.recurrenceTmpModel.recurrenceRangeType),
                                eventStartDate: $scope.recurrenceTmpModel.start,
                                eventEndDate: $scope.recurrenceTmpModel.end,
                                modalAction: 'setEventRecurrence'
                            };

                            $modalInstance.close(modalActionData);
                        }
                    };

                    $scope.removeCurrentRecurrence = function() {
                        var modalActionData = {
                            modalAction: 'removeCurrentRecurrence',
                            recurrenceDataModel: {
                                recurrenceType: 'ONLY_ONCE'
                            },
                            eventStartDate: $scope.recurrenceTmpModel.start,
                            eventEndDate: $scope.recurrenceTmpModel.end
                        };

                        $modalInstance.close(modalActionData);
                    };

                    $scope.$watch("recurrenceTmpModel.start", function(newValue, oldValue, scope) {
                        if(!moment(newValue).isSame(oldValue)) {
                            var dates = DateService.fixStartAndEndDirectiveDates($scope.recurrenceTmpModel.start, $scope.recurrenceTmpModel.end, oldDate, newDate, false);

                            if(dates.start){
                                $scope.recurrenceTmpModel.start = dates.start;
                            }
                            if(dates.end){
                                $scope.recurrenceTmpModel.end = dates.end
                            }
                            $scope.minEndDate = $scope.recurrenceTmpModel.start;
                        }
                    });

                    $scope.$watch("recurrenceTmpModel.end", function(newValue, oldValue, scope) {
                        var endDate = DateService.fixDirectiveEndDate($scope.recurrenceTmpModel.start, $scope.recurrenceTmpModel.end);
                        if(endDate){
                            $scope.recurrenceTmpModel.end = endDate;
                        }
                    });

                    /*Cancel the modal dialog*/
                    $scope.cancel = function() {
                        $modalInstance.dismiss();
                    };
                } ]);