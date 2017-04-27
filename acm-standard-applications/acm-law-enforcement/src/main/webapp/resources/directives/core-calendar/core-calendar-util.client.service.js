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

angular.module('directives').factory('Directives.CalendarUtilService', ['$filter', '$translate', 'UtilService',
    function ($filter, $translate, UtilService) {

        /**
         * Options for select priority dropdown
         * @type {Array}
         */
        var PRIORITY_OPTIONS = [
            {
                'value': 'NORMAL',
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.priorityOptions.normalImportance'
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

        /**
         * Options for select reminder dropdown
         * @type {Array}
         */
        var REMINDER_OPTIONS = [
            {
                'value': -1,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.none'
            },
            {
                'value': 0,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.0Minutes'
            },
            {
                'value': 5,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.5Minutes'
            },
            {
                'value': 10,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.10Minutes'
            },
            {
                'value': 15,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.15Minutes'
            },
            {
                'value': 30,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.30Minutes'
            },
            {
                'value': 60,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Hours'
            },
            {
                'value': 120,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Hours'
            },
            {
                'value': 180,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.3Hours'
            },
            {
                'value': 240,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.4Hours'
            },
            {
                'value': 300,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.5Hours'
            },
            {
                'value': 360,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.6Hours'
            },
            {
                'value': 420,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.7Hours'
            },
            {
                'value': 480,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.8Hours'
            },
            {
                'value': 540,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.9Hours'
            },
            {
                'value': 600,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.10Hours'
            },
            {
                'value': 660,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.11Hours'
            },
            {
                'value': 720,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.halfDay'
            },
            {
                'value': 1080,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.18Hours'
            },
            {
                'value': 1440,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Days'
            },
            {
                'value': 4320,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Days'
            },
            {
                'value': 5760,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.3Days'
            },
            {
                'value': 7200,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.4Days'
            },
            {
                'value': 10080,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.1Week'
            },
            {
                'value': 20160,
                'label': 'common.directive.coreCalendar.addNewEventDialog.form.reminderOptions.2Weeks'
            }
        ];

        /**
         * Options for select month dropdown
         * @type {Array}
         */
        var MONTHS_OPTIONS = [
            {
                'value': 'JANUARY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.january'
            },
            {
                'value': 'FEBRUARY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.february'
            },
            {
                'value': 'MARCH',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.march'
            },
            {
                'value': 'APRIL',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.april'
            },
            {
                'value': 'MAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.may'
            },
            {
                'value': 'JUNE',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.june'
            },
            {
                'value': 'JULY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.july'
            },
            {
                'value': 'AUGUST',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.august'
            },
            {
                'value': 'SEPTEMBER',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.september'
            },
            {
                'value': 'OCTOBER',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.october'
            },
            {
                'value': 'NOVEMBER',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.november'
            },
            {
                'value': 'DECEMBER',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monthOptions.december'
            }
        ];

        /**
         * Options for select days of the week checkboxes
         * @type {Array}
         */
        var DAYS_WEEK_OPTIONS = [
            {
                'value': 'SUNDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.sunday.checkbox'
            },
            {
                'value': 'MONDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.monday.checkbox'
            },
            {
                'value': 'TUESDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.tuesday.checkbox'
            },
            {
                'value': 'WEDNESDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.wednesday.checkbox'
            },
            {
                'value': 'THURSDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.thursday.checkbox'
            },
            {
                'value': 'FRIDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.friday.checkbox'
            },
            {
                'value': 'SATURDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.saturday.checkbox'
            }
        ];

        /**
         * Options for select recurrence type dropdown
         * @type {Array}
         */
        var RECURRENCE_OPTIONS = [
            {
                'value': 'DAILY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.repeatsOptions.daily'
            },
            {
                'value': 'WEEKLY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.repeatsOptions.weekly'
            },
            {
                'value': 'MONTHLY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.repeatsOptions.monthly'
            },
            {
                'value': 'YEARLY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.repeatsOptions.yearly'
            }
        ];

        /**
         * Options for select relative day recurrence in month dropdown
         * @type {Array}
         */
        var RELATIVE_RECURRENCE_DAY_OPTIONS = [
            {
                'value': 'DAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.day'
            },
            {
                'value': 'WEEKDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.weekDay'
            },
            {
                'value': 'WEEKEND_DAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.weekendDay'
            },
            {
                'value': 'SUNDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.sunday'
            },
            {
                'value': 'MONDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.monday'
            },
            {
                'value': 'TUESDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.tuesday'
            },
            {
                'value': 'WEDNESDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.wednesday'
            },
            {
                'value': 'THURSDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.thursday'
            },
            {
                'value': 'FRIDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.friday'
            },
            {
                'value': 'SATURDAY',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOptions.saturday'
            }
        ];

        /**
         * Options for select day ocurrence in month dropdown
         * @type {Array}
         */
        var DAY_OCCURRENCE_IN_MONTH_OPTIONS = [
            {
                'value': 'FIRST',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOccurrenceOptions.first'
            },
            {
                'value': 'SECOND',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOccurrenceOptions.second'
            },
            {
                'value': 'THIRD',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOccurrenceOptions.third'
            },
            {
                'value': 'FOURTH',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOccurrenceOptions.fourth'
            },
            {
                'value': 'LAST',
                'label': 'common.directive.coreCalendar.recurrencePatternDialog.dayOccurrenceOptions.last'
            },
        ];

        /**
         * @ngdoc method
         * @name calculateDaysInMonth
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates how many days are in a month of a given year. 
         *
         * @param {Int} month - the month of the year, indexed: 0-11.
         * @param {Int} year - the year in format YYYY
         *
         * @Returns {Int} the number of days
         */
        var calculateDaysInMonth = function(month, year) {
            switch (month) {
                case 1 :
                    return (year % 4 == 0 && year % 100) || year % 400 == 0 ? 29 : 28;
                case 8 : case 3 : case 5 : case 10 :
                return 30;
                default :
                    return 31;
            }
        };
        /**
         * @ngdoc method
         * @name calculateDayOccurrenceInMonth
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates in which week of the month is the given day. 
         *
         * @param {Int} day - the day indexed: 1-31.
         *
         * @Returns {String} the week in the month. Possible return values are DAY_OCCURRENCE_IN_MONTH_OPTIONS.
         */
        var calculateDayOccurrenceInMonth = function(day) {
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

        /**
         * @ngdoc method
         * @name mapDayOccurrenceToRRule
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Maps the DAY_OCCURRENCE_IN_MONTH_OPTIONS to RRule values.
         *
         * @param {String} dayOuccurrenceInMonth - DAY_OCCURRENCE_IN_MONTH_OPTIONS values.
         *
         * @Returns {Int} values for bysetpos parameter in RRule library.
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name mapDayInMonthToRRule
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Maps the RELATIVE_RECURRENCE_DAY_OPTIONS to RRule values.
         *
         * @param {String} dayInMonth - RELATIVE_RECURRENCE_DAY_OPTIONS values.
         *
         * @Returns {Int} values for byweekday parameter in RRule library.
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
        var mapDayInMonthToRRule = function(dayInMonth) {
            var byweekday = null;

            switch (dayInMonth){
                case 'DAY':
                    byweekday = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR, RRule.SA, RRule.SU];
                    break;
                case 'WEEKDAY':
                    byweekday = [RRule.MO, RRule.TU, RRule.WE, RRule.TH, RRule.FR];
                    break;
                case 'WEEKEND_DAY':
                    byweekday = [RRule.SA, RRule.SU];
                    break;
                case 'SUNDAY':
                    byweekday = [RRule.SU];
                    break;
                case 'MONDAY':
                    byweekday = [RRule.MO];
                    break;
                case 'TUESDAY':
                    byweekday = [RRule.TU];
                    break;
                case 'WEDNESDAY':
                    byweekday = [RRule.WE];
                    break;
                case 'THURSDAY':
                    byweekday = [RRule.TH];
                    break;
                case 'FRIDAY':
                    byweekday = [RRule.FR];
                    break;
                case 'SATURDAY':
                    byweekday = [RRule.SA];
                    break;
            }

            return byweekday;
        };

        /**
         * @ngdoc method
         * @name mapDayInWeekToRRule
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Maps the DAYS_WEEK_OPTIONS to RRule values.
         *
         * @param {Array} selectedDaysOfWeek -  array of DAYS_WEEK_OPTIONS values.
         *
         * @Returns {Array}  values for byweekday parameter in RRule library.
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name mapMonthInYearToRRule
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Maps the MONTHS_OPTIONS to RRule values.
         *
         * @param {String} month - MONTHS_OPTIONS values.
         *
         * @Returns {Int}  values for bymonth parameter in RRule library.
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name getEveryDayRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Every Day recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name getEveryWeekdayRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Every Weekday recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name getWeeklyRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Weekly recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {Array} selectedDaysOfWeek -  array of DAYS_WEEK_OPTIONS values.
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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


        /**
         * @ngdoc method
         * @name getAbsoluteMonthlyRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Absolute Monthly recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {Int} monthDayNum - the day in month indexed: 1-31.
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name getRelativeMonthlyRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Relative Monthly recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {String} dayOuccurrenceInMonth - DAY_OCCURRENCE_IN_MONTH_OPTIONS values.
         * @param {String} dayInMonth - RELATIVE_RECURRENCE_DAY_OPTIONS values
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name getAbsoluteYearlyRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Absolute Yearly recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {String} month - MONTHS_OPTIONS values.
         * @param {Int} monthDayNum - the day in month indexed: 1-31
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
        var getAbsoluteYearlyRecurrenceRange = function(startDate, month, monthDayNum, interval, count) {

            var byMonth = mapMonthInYearToRRule(month);

            var rule = new RRule({
                freq: RRule.YEARLY,
                count: count,
                interval: interval,
                bymonth: byMonth,
                bymonthday: monthDayNum
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

        /**
         * @ngdoc method
         * @name getRelativeYearlyRecurrenceRange
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Calculates the recurrenge range for Relative Yearly recurrence type using the RRule library.
         *
         * @param {Date} startDate - the start date of the recurrence.
         * @param {String} dayOuccurrenceInMonth - DAY_OCCURRENCE_IN_MONTH_OPTIONS values
         * @param {String} dayInMonth - RELATIVE_RECURRENCE_DAY_OPTIONS values
         * @param {String} month - MONTHS_OPTIONS values
         * @param {Int} interval - the interval between each recurrence iteration
         * @param {Int} count - how many occurrences of the recurrence range should be generated
         *
         * @Returns {Object} recurrenceRange
         *          {Date} recurrenceRange.start - the start date of the recurrence
         *          {Date} recurrenceRange.endBy - the end of the recurrence
         * For more information about RRule library please visit https://github.com/jakubroztocil/rrule
         */
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

        /**
         * @ngdoc method
         * @name buildPopoverTemplate
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Builds the html for the event popover template.
         *
         * @param {Object} calendarEvent - the calendar event object containing the event details
         *
         * @Returns {String} popoverTemplate - the html of the popover template
         */
        var buildPopoverTemplate = function(calendarEvent) {
            var dateFormat = $translate.instant('mm/dd/yyyy h:MM a');
            var startLabel = $translate.instant('common.directive.coreCalendar.start.label');
            var endLabel = $translate.instant('common.directive.coreCalendar.end.label');
            var startDateTime = UtilService.getDateTimeFromDatetime(calendarEvent.start, dateFormat);
            var endDateTime = UtilService.getDateTimeFromDatetime(calendarEvent.end, dateFormat);
            var popoverTemplate = '<label>' + startLabel + '</label>' + startDateTime + '</br>' + '<label>' + endLabel + '</label>' + endDateTime;

            return popoverTemplate;
        };


        /**
         * @ngdoc method
         * @name buildEventRecurrenceString
         * @methodOf services:Directives.CalendarUtilService
         *
         * @description
         * Builds the event recurrence description string.
         *
         * @param {Object} eventDetails - the details of the event
         *
         * @Returns {String} event recurrence description string
         */
        var buildEventRecurrenceString = function(eventDetails) {
            var recurrenceDetails = eventDetails.recurrenceDetails;
            var startDateString = '';
            var endDateString = '';
            var durationString = '';
            var patternString = '';

            /*build start date string*/
            var startDateFormatedString = $filter('date')(eventDetails.start, 'MM/dd/yyyy');
            startDateString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.effective') + ' ' + startDateFormatedString;

            /*build end date string*/
            if(recurrenceDetails.endBy || recurrenceDetails.endAfterOccurrances) {
                var endDateFormatedString = $filter('date')(eventDetails.recurrenceDetails.endBy, 'MM/dd/yyyy');
                endDateString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.until') + ' ' + endDateFormatedString;
            }

            /*build event duration string*/
            var startTimeFormatedString = $filter('date')(eventDetails.start, 'h:mm a');
            var endTimeFormatedString = $filter('date')(eventDetails.end, 'h:mm a');
            durationString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.from') + ' ' +
             startTimeFormatedString + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.to') + ' ' + endTimeFormatedString;

            /*build pattern string*/
            patternString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.occurs');
            switch (recurrenceDetails.recurrenceType) {
                case 'DAILY':
                    if (!recurrenceDetails.everyWeekDay) {
                        if(recurrenceDetails.interval === 1) {
                            patternString = patternString + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') +
                            ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.day');
                        } else {
                            patternString = patternString + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') +
                             ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.days');
                        }
                    } else {
                        patternString = patternString + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.weekday');
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
                                daysString = daysString + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.and') + 
                                ' ' + $translate.instant(dayOption.label);
                            } else {
                                daysString = daysString + ',' + ' ' + $translate.instant(dayOption.label);
                            }
                        }
                    });
                    if(recurrenceDetails.interval === 1) {
                        weekNumString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every');
                    } else {
                        weekNumString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                        ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.weeks') + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.on');
                    }
                    patternString = patternString + ' ' + weekNumString + ' ' + daysString;
                    break;
                case 'MONTHLY':
                    if (!recurrenceDetails.weekOfMonth) {
                        var absoluteMonthlyPatternString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.day') + 
                        ' ' + recurrenceDetails.day + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.of') + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                        ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.months');
                        patternString = patternString + ' ' + absoluteMonthlyPatternString;
                    } else {
                        var weekOfMonth = _.find(DAY_OCCURRENCE_IN_MONTH_OPTIONS, function(o) { return o.value === recurrenceDetails.weekOfMonth; });
                        var dayOfWeek = _.find(RELATIVE_RECURRENCE_DAY_OPTIONS, function(o) { return o.value === recurrenceDetails.dayOfWeek; });
                        var relativeMonthlyPatternString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.the') + 
                        ' ' + $translate.instant(weekOfMonth.label) + ' ' + $translate.instant(dayOfWeek.label) + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.of') + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                        ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.months');
                        patternString = patternString + ' ' + relativeMonthlyPatternString;
                    }
                    break;
                case 'YEARLY':
                    var numYearString = '';
                    var month = _.find(MONTHS_OPTIONS, function(o) { return o.value === recurrenceDetails.month; });

                    if (!recurrenceDetails.weekOfMonth) {
                        if(recurrenceDetails.interval !== 1) {
                            numYearString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                            ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.years') + 
                            ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.on');
                        } else {
                            numYearString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every');
                        }
                        var absoluteYearlyPatternString = $translate.instant(month.label) + ' ' + recurrenceDetails.dayOfMonth;
                        patternString = patternString + ' ' + numYearString + ' ' + absoluteYearlyPatternString;
                    } else {
                        if(recurrenceDetails.interval !== 1) {
                            numYearString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.every') + 
                            ' ' + recurrenceDetails.interval + ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.years') + 
                            ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.on');
                        } else {
                            numYearString = '';
                        }
                        var weekOfMonth = _.find(DAY_OCCURRENCE_IN_MONTH_OPTIONS, function(o) { return o.value === recurrenceDetails.weekOfMonth; });
                        var dayOfWeek = _.find(RELATIVE_RECURRENCE_DAY_OPTIONS, function(o) { return o.value === recurrenceDetails.dayOfWeek; });
                        var relativeYearlyPatternString = $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.the') + 
                        ' ' + $translate.instant(weekOfMonth.label) + ' ' + $translate.instant(dayOfWeek.label) + 
                        ' ' + $translate.instant('common.directive.coreCalendar.recurrencePatternDialog.descriptionString.of') + 
                        ' ' +$translate.instant(month.label);
                        patternString = patternString + ' ' + numYearString + ' ' + relativeYearlyPatternString;
                    }
                    break;
            }

            return patternString + ' ' + startDateString + ' ' + endDateString + ' ' + durationString;
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
            calculateDayOccurrenceInMonth: calculateDayOccurrenceInMonth,
            buildEventRecurrenceString: buildEventRecurrenceString,
            getEveryDayRecurrenceRange: getEveryDayRecurrenceRange,
            getEveryWeekdayRecurrenceRange: getEveryWeekdayRecurrenceRange,
            getWeeklyRecurrenceRange: getWeeklyRecurrenceRange,
            getAbsoluteMonthlyRecurrenceRange: getAbsoluteMonthlyRecurrenceRange,
            getRelativeMonthlyRecurrenceRange: getRelativeMonthlyRecurrenceRange,
            getAbsoluteYearlyRecurrenceRange: getAbsoluteYearlyRecurrenceRange,
            getRelativeYearlyRecurrenceRange: getRelativeYearlyRecurrenceRange,
            buildPopoverTemplate: buildPopoverTemplate
        };
    }
]);