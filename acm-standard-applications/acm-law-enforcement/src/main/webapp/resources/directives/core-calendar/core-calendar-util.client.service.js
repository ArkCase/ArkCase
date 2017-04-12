'use strict';

/**
 * @ngdoc service
 * @name services:Directives.CalendarUtilService
 *
 * @description
 *
 *
 * Utility service for core-calendar directive that includes select options, constants and utility functions.
 */

angular.module('directives').factory('Directives.CalendarUtilService', ['$filter', '$translate',
    function ($filter, $translate) {
        var PRIORITY_OPTIONS = [
            {
                'value': 'NORMAL',
                'label': 'Normal Importance'
            },
            {
                'value': 'HIGH',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.priorityOptions.highImportance'
            },
            {
                'value': 'LOW',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.priorityOptions.lowImportance'
            }
        ];

        var REMINDER_OPTIONS = [
            {
                'value': 'NONE',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.none'
            },
            {
                'value': '0',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.0Minutes'
            },
            {
                'value': '5',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.5Minutes'
            },
            {
                'value': '10',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.10Minutes'
            },
            {
                'value': '15',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.15Minutes'
            },
            {
                'value': '30',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.30Minutes'
            },
            {
                'value': '60',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Hours'
            },
            {
                'value': '120',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Hours'
            },
            {
                'value': '180',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.3Hours'
            },
            {
                'value': '240',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.4Hours'
            },
            {
                'value': '300',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.5Hours'
            },
            {
                'value': '360',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.6Hours'
            },
            {
                'value': '420',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.7Hours'
            },
            {
                'value': '480',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.8Hours'
            },
            {
                'value': '540',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.9Hours'
            },
            {
                'value': '600',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.10Hours'
            },
            {
                'value': '660',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.11Hours'
            },
            {
                'value': '720',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.halfDay'
            },
            {
                'value': '1080',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.18Hours'
            },
            {
                'value': '1440',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Days'
            },
            {
                'value': '4320',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Days'
            },
            {
                'value': '5760',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.3Days'
            },
            {
                'value': '7200',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.4Days'
            },
            {
                'value': '10080',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Week'
            },
            {
                'value': '20160',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Weeks'
            }
        ];

        var MONTHS_OPTIONS = [
            {
                'value': 'JANUARY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.january'
            },
            {
                'value': 'FEBRUARY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.february'
            },
            {
                'value': 'MARCH',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.march'
            },
            {
                'value': 'APRIL',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.april'
            },
            {
                'value': 'MAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.may'
            },
            {
                'value': 'JUNE',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.june'
            },
            {
                'value': 'JULY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.july'
            },
            {
                'value': 'AUGUST',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.august'
            },
            {
                'value': 'SEPTEMBER',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.september'
            },
            {
                'value': 'OCTOBER',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.october'
            },
            {
                'value': 'NOVEMBER',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.november'
            },
            {
                'value': 'DECEMBER',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monthOptions.december'
            }
        ];

        var DAYS_WEEK_OPTIONS = [
            {
                'value': 'SUNDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.sunday.checkbox'
            },
            {
                'value': 'MONDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monday.checkbox'
            },
            {
                'value': 'TUESDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.tuesday.checkbox'
            },
            {
                'value': 'WEDNESDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.wednesday.checkbox'
            },
            {
                'value': 'THURSDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.thursday.checkbox'
            },
            {
                'value': 'FRIDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.friday.checkbox'
            },
            {
                'value': 'SATURDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.saturday.checkbox'
            }
        ];

        var RECURRENCE_OPTIONS = [
            {
                'value': 'DAILY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.repeatsOptions.daily'
            },
            {
                'value': 'WEEKLY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.repeatsOptions.weekly'
            },
            {
                'value': 'MONTHLY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.repeatsOptions.monthly'
            },
            {
                'value': 'YEARLY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.repeatsOptions.yearly'
            }
        ];

        var RELATIVE_RECURRENCE_DAY_OPTIONS = [
            {
                'value': 'DAY',
                'label': 'day'
            },
            {
                'value': 'WEEKDAY',
                'label': 'weekday'
            },
            {
                'value': 'WEEKEND_DAY',
                'label': 'weekend day'
            },
            {
                'value': 'SUNDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.sunday.checkbox'
            },
            {
                'value': 'MONDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.monday.checkbox'
            },
            {
                'value': 'TUESDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.tuesday.checkbox'
            },
            {
                'value': 'WEDNESDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.wednesday.checkbox'
            },
            {
                'value': 'THURSDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.thursday.checkbox'
            },
            {
                'value': 'FRIDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.friday.checkbox'
            },
            {
                'value': 'SATURDAY',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.saturday.checkbox'
            }
        ];

        var DAY_OCCURRENCE_IN_MONTH_OPTIONS = [
            {
                'value': 'FIRST',
                'label': 'first'
            },
            {
                'value': 'SECOND',
                'label': 'second'
            },
            {
                'value': 'THIRD',
                'label': 'third'
            },
            {
                'value': 'FOURTH',
                'label': 'fourth'
            },
            {
                'value': 'LAST',
                'label': 'last'
            },
        ];

        var calculateDaysInMonth = function(month, year) { // m is 0 indexed: 0-11
            switch (month) {
                case 1 :
                    return (year % 4 == 0 && year % 100) || year % 400 == 0 ? 29 : 28;
                case 8 : case 3 : case 5 : case 10 :
                return 30;
                default :
                    return 31;
            }
        };

        var calculateWeekInMonth = function(day) {
            var weekOfMonth = '';

            switch (Math.ceil(day / 7)) {
                case 1:
                    weekOfMonth = 'FIRST';
                    break;
                case 2:
                    weekOfMonth = 'SECOND';
                    break;
                case 3:
                    weekOfMonth = 'THIRD';
                    break;
                case 4:
                    weekOfMonth = 'FOURTH';
                    break;
                case 5:
                    weekOfMonth = 'LAST';
                    break;
            }

            return weekOfMonth;
        };

        var mapDayOccurrenceToRRule = function(dayOuccurrenceInMonth) {
            var bySetpos = null;

            switch (dayOuccurrenceInMonth){
                case 'FIRST':
                    bySetpos = 1;
                    break;
                case 'SECOND':
                    bySetpos = 2;
                    break;
                case 'THIRD':
                    bySetpos = 3;
                    break;
                case 'FOURTH':
                    bySetpos = 4;
                    break;
                case 'LAST':
                    bySetpos = -1;
                    break;
            }

            return bySetpos;
        };

        var mapDayInMonthToRRule = function(dayInMonth) {
            var byWeekDay = null;

            switch (dayInMonth){
                case 'DAY':
                    byWeekDay = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR, RRule.SA, RRule.SU];
                    break;
                case 'WEEKDAY':
                    byWeekDay = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR];
                    break;
                case 'WEEKEND_DAY':
                    byWeekDay = [RRule.SA, RRule.SU];
                    break;
                case 'SUNDAY':
                    byWeekDay = [RRule.SU];
                    break;
                case 'MONDAY':
                    byWeekDay = [RRule.MO];
                    break;
                case 'TUESDAY':
                    byWeekDay = [RRule.TU];
                    break;
                case 'WEDNESDAY':
                    byWeekDay = [RRule.WE];
                    break;
                case 'THURSDAY':
                    byWeekDay = [RRule.TH];
                    break;
                case 'FRIDAY':
                    byWeekDay = [RRule.FR];
                    break;
                case 'SATURDAY':
                    byWeekDay = [RRule.SA];
                    break;
            }

            return byWeekDay;
        };

        var mapDayInWeekToRRule = function(selectedDaysOfWeek) {
            var DAY_RRULE_MAP = {
                'SUNDAY': RRule.SU,
                'MONDAY': RRule.MO,
                'TUESDAY': RRule.TU,
                'WEDNESDAY': RRule.WE,
                'THURSDAY': RRule.TH,
                'FRIDAY': RRule.FR,
                'SATURDAY': RRule.SA,
            };
            var byWeekDay = angular.copy(selectedDaysOfWeek);

            _.forEach(byWeekDay, function(day, index) {
                byWeekDay[index] = DAY_RRULE_MAP[day];
            });

            return byWeekDay;
        };

        var mapMonthInYearToRRule = function(month) {

            var bymonth = null;

            switch (month){
                case 'JANUARY':
                    bymonth = 1;
                    break;
                case 'FEBRUARY':
                    bymonth = 2;
                    break;
                case 'MARCH':
                    bymonth = 3;
                    break;
                case 'APRIL':
                    bymonth = 4;
                    break;
                case 'MAY':
                    bymonth = 5;
                    break;
                case 'JUNE':
                    bymonth = 6;
                    break;
                case 'JULY':
                    bymonth = 7;
                    break;
                case 'AUGUST':
                    bymonth = 8;
                    break;
                case 'SEPTEMBER':
                    bymonth = 9;
                    break;
                case 'OCTOBER':
                    bymonth = 10;
                    break;
                case 'NOVEMBER':
                    bymonth = 11;
                    break;
                case 'DECEMBER':
                    bymonth = 12;
                    break;
            }

            return bymonth;
        };

        var buildEventRecurrenceString = function(eventDetails, recurrenceStart, recurrenceEnd) {
            var recurrenceDetails = eventDetails.recurrenceDetails;
            var startDateString = '';
            var endDateString = '';
            var durationString = '';
            var patternString = '';

            /*build start date string*/
            var startDateFormatedString = $filter('date')(recurrenceStart, 'MM/dd/yyyy');
            startDateString = 'effective' + ' ' + startDateFormatedString;

            /*build end date string*/
            if(recurrenceDetails.endBy || recurrenceDetails.endAfterOccurrances) {
                var endDateFormatedString = $filter('date')(recurrenceEnd, 'MM/dd/yyyy');
                endDateString = 'until' + ' ' + endDateFormatedString;
            }

            /*build event duration string*/
            var startTimeFormatedString = $filter('date')(eventDetails.start, 'h:mm a');
            var endTimeFormatedString = $filter('date')(eventDetails.end, 'h:mm a');
            durationString = 'from' + ' ' + startTimeFormatedString + ' ' + 'to' + ' ' + endTimeFormatedString;

            /*build pattern string*/
            patternString = 'Occurs';
            switch (recurrenceDetails.recurrenceType) {
                case 'DAILY':
                    if (!recurrenceDetails.everyWeekDay) {
                        if(recurrenceDetails.interval === 1) {
                            patternString = patternString + ' ' + 'every day';
                        } else {
                            patternString = patternString + ' ' + 'every' + ' ' + recurrenceDetails.interval + ' ' + 'days';
                        }
                    } else {
                        patternString = patternString + ' ' + 'every weekday';
                    }
                    break;
                case 'WEEKLY':
                    var daysString = '';
                    var weekNumString = '';
                    var successIndex = 0;
                    _.forEach(DAYS_WEEK_OPTIONS, function(dayOption) {
                        var selectedDayIndex = _.findIndex(recurrenceDetails.days, function(day) {
                            return dayOption.value === day;
                        });
                        if(selectedDayIndex !== -1) {
                            successIndex++;
                            if(successIndex === 1) {
                                daysString = $translate.instant(dayOption.label);
                            } else if (successIndex === recurrenceDetails.days.length) {
                                daysString = daysString + ' ' + 'and' + ' ' + $translate.instant(dayOption.label);
                            } else {
                                daysString = daysString + ',' + ' ' + $translate.instant(dayOption.label);
                            }
                        }
                    });
                    if(recurrenceDetails.interval === 1) {
                        weekNumString = 'every';
                    } else {
                        weekNumString = 'every' + ' ' + recurrenceDetails.interval + ' ' + 'week(s)' + ' ' + 'on';
                    }
                    patternString = patternString + ' ' + weekNumString + ' ' + daysString;
                    break;
                case 'MONTHLY':
                    if (!recurrenceDetails.weekOfMonth) {
                        var absoluteMonthlyPatternString = 'day' + ' ' + recurrenceDetails.day + ' ' + 'of every' + ' ' + recurrenceDetails.interval + ' ' + 'month(s)';
                        patternString = patternString + ' ' + absoluteMonthlyPatternString;
                    } else {
                        var weekOfMonth = _.find(DAY_OCCURRENCE_IN_MONTH_OPTIONS, function(o) { return o.value === recurrenceDetails.weekOfMonth; });
                        var dayOfWeek = _.find(RELATIVE_RECURRENCE_DAY_OPTIONS, function(o) { return o.value === recurrenceDetails.dayOfWeek; });
                        var relativeMonthlyPatternString = 'the' + ' ' + $translate.instant(weekOfMonth.label) + ' ' + $translate.instant(dayOfWeek.label) + ' '
                            + 'of every' + ' ' + recurrenceDetails.interval + ' ' + 'month(s)';
                        patternString = patternString + ' ' + relativeMonthlyPatternString;
                    }
                    break;
                case 'YEARLY':
                    var numYearString = '';
                    var month = _.find(MONTHS_OPTIONS, function(o) { return o.value === recurrenceDetails.month; });

                    if (!recurrenceDetails.weekOfMonth) {
                        if(recurrenceDetails.interval !== 1) {
                            numYearString = 'every' + ' ' + recurrenceDetails.interval + ' ' + ' years on';
                        } else {
                            numYearString = 'every';
                        }
                        var absoluteYearlyPatternString = $translate.instant(month.label) + ' ' + recurrenceDetails.dayOfMonth;
                        patternString = patternString + ' ' + numYearString + ' ' + absoluteYearlyPatternString;
                    } else {
                        if(recurrenceDetails.interval !== 1) {
                            numYearString = 'every' + ' ' + recurrenceDetails.interval + ' ' + ' years on';
                        } else {
                            numYearString = '';
                        }
                        var weekOfMonth = _.find(DAY_OCCURRENCE_IN_MONTH_OPTIONS, function(o) { return o.value === recurrenceDetails.weekOfMonth; });
                        var dayOfWeek = _.find(RELATIVE_RECURRENCE_DAY_OPTIONS, function(o) { return o.value === recurrenceDetails.dayOfWeek; });
                        var relativeYearlyPatternString = 'the' + ' ' + $translate.instant(weekOfMonth.label) + ' ' + $translate.instant(dayOfWeek.label) + ' '
                            + 'of' + ' ' +$translate.instant(month.label);
                        patternString = patternString + ' ' + numYearString + ' ' + relativeYearlyPatternString;
                    }
                    break;
            }

            return patternString + ' ' + startDateString + ' ' + endDateString + ' ' + durationString;
        };

        var getEveryDayRecurrenceRange = function(startDate, interval, count) {
            var rule =  new RRule({
                freq: RRule.DAILY,
                count: count,
                interval: interval,
                dtstart: startDate
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            return recurrenceRange;

        };

        var getEveryWeekdayRecurrenceRange = function(startDate, count) {
            var rule =  new RRule({
                freq: RRule.DAILY,
                count: count,
                byweekday: [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR],
                dtstart: startDate
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };


        var getWeeklyRecurrenceRange = function(startDate, selectedDaysOfWeek, interval, count) {

            var byWeekDay = mapDayInWeekToRRule(selectedDaysOfWeek);

            var rule =  new RRule({
                freq: RRule.WEEKLY,
                count: count,
                interval: interval,
                byweekday: byWeekDay,
                dtstart: startDate
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };

        var getAbsoluteMonthlyRecurrenceRange = function(startDate, monthDayNum, interval, count) {
            var rule = new RRule({
                freq: RRule.MONTHLY,
                count: count,
                interval: interval,
                bymonthday: monthDayNum,
                dtstart: startDate
            });


            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };

        var getRelativeMonthlyRecurrenceRange = function(startDate, dayOuccurrenceInMonth, dayInMonth, interval, count) {

            var byWeekDay = mapDayInMonthToRRule(dayInMonth);
            var bySetPos = mapDayOccurrenceToRRule(dayOuccurrenceInMonth);

            var rule = new RRule({
                freq: RRule.MONTHLY,
                count: count,
                interval: interval,
                byweekday: byWeekDay,
                bysetpos: bySetPos,
                dtstart: startDate
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };

        var getAbsoluteYearlyRecurrenceRange = function(startDate, month, dayInMonth, interval, count) {

            var byMonth = mapMonthInYearToRRule(month);

            var rule = new RRule({
                freq: RRule.YEARLY,
                count: count,
                interval: interval,
                bymonth: byMonth,
                bymonthday: dayInMonth
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };

        var getRelativeYearlyRecurrenceRange = function(startDate, dayOuccurrenceInMonth, dayInMonth, month, interval, count) {

            var byWeekDay = mapDayInMonthToRRule(dayInMonth);
            var bySetPos = mapDayOccurrenceToRRule(dayOuccurrenceInMonth);
            var byMonth = mapMonthInYearToRRule(month);

            var rule = new RRule({
                freq: RRule.YEARLY,
                count: count,
                interval: interval,
                byweekday: byWeekDay,
                bymonth: byMonth,
                bysetpos: bySetPos
            });

            var dateRange = rule.all();

            var recurrenceRange = {
                start: new Date(dateRange[0]),
                endBy: new Date(dateRange[dateRange.length - 1])
            };

            recurrenceRange.start.setHours(startDate.getHours());
            recurrenceRange.start.setMinutes(startDate.getMinutes());
            recurrenceRange.start.setSeconds(startDate.getSeconds());

            return recurrenceRange;
        };


        return {
            RECURRENCE_OPTIONS: RECURRENCE_OPTIONS,
            DAYS_WEEK_OPTIONS: DAYS_WEEK_OPTIONS,
            MONTHS_OPTIONS: MONTHS_OPTIONS,
            REMINDER_OPTIONS: REMINDER_OPTIONS,
            PRIORITY_OPTIONS: PRIORITY_OPTIONS,
            RELATIVE_RECURRENCE_DAY_OPTIONS: RELATIVE_RECURRENCE_DAY_OPTIONS,
            DAY_OCCURRENCE_IN_MONTH_OPTIONS: DAY_OCCURRENCE_IN_MONTH_OPTIONS,
            calculateDaysInMonth: calculateDaysInMonth,
            calculateWeekInMonth: calculateWeekInMonth,
            buildEventRecurrenceString: buildEventRecurrenceString,
            getEveryDayRecurrenceRange: getEveryDayRecurrenceRange,
            getEveryWeekdayRecurrenceRange: getEveryWeekdayRecurrenceRange,
            getWeeklyRecurrenceRange: getWeeklyRecurrenceRange,
            getAbsoluteMonthlyRecurrenceRange: getAbsoluteMonthlyRecurrenceRange,
            getRelativeMonthlyRecurrenceRange: getRelativeMonthlyRecurrenceRange,
            getAbsoluteYearlyRecurrenceRange: getAbsoluteYearlyRecurrenceRange,
            getRelativeYearlyRecurrenceRange: getRelativeYearlyRecurrenceRange


        };
    }
]);