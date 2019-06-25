'use strict';

/**
 * @ngdoc service
 * @name services:DueDate.Service
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/due-date.client.service.js modules/common/services/due-date.client.service.js}
 *
 * DueDate.Service provides functions for calculation for due date without holidays and weekends
 */
angular.module('services').service('DueDate.Service', [ '$translate', function($translate) {

    return ({
        dueDateWorkingDays: dueDateWorkingDays,
        dueDateWithWeekends: dueDateWithWeekends,
        workingDays: workingDays,
        workingDaysWithWeekends: workingDaysWithWeekends,
        daysLeft: daysLeft,
        daysLeftWithWeekends: daysLeftWithWeekends,
        calculateOverdueDays: calculateOverdueDays,
        calculateOverdueDaysWithWeekends: calculateOverdueDaysWithWeekends
    });

    function dueDateWorkingDays(startDate, days, holidays) {
        var momentObject = moment(startDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var count = 0;
        while (count < days) {
            momentObject.add(1, 'days');

            if (!isWeekend(momentObject) && !isHoliday(holidays, momentObject)) {
                count += 1;
            }

        }
        return momentObject.format($translate.instant("common.frevvo.defaultDateFormat"));
    }

    function dueDateWithWeekends(startDate, days, holidays) {
        var momentObject = moment(startDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var count = 0;
        while (count < days) {
            momentObject.add(1, 'days');

            if (!isHoliday(holidays, momentObject)) {
                count += 1;
            }

        }
        return momentObject.format($translate.instant("common.frevvo.defaultDateFormat"));
    }

    function workingDays(startDate, holidays) {
        var momentObject = moment(startDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var today = moment();
        var days = -1;
        while (momentObject < today) {
            momentObject.add(1, 'days');

            if (!isWeekend(momentObject) && !isHoliday(holidays, momentObject)) {
                days += 1;
            }
        }
        return days;
    }

    function workingDaysWithWeekends(startDate, holidays) {
        var momentObject = moment(startDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var today = moment();
        var days = -1;
        while (momentObject < today) {
            momentObject.add(1, 'days');

            if (!isHoliday(holidays, momentObject)) {
                days += 1;
            }
        }
        return days;
    }

    function daysLeft(holidays, dueDate) {
        var dueDate = moment(dueDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var momentDate = moment();
        var days = 0;
        if (dueDate > momentDate) { //calculate days remaining
            while (momentDate < dueDate) {
                momentDate.add(1, 'days');
                if (!isWeekend(momentDate) && !isHoliday(holidays, momentDate)) {
                    days += 1;
                }
            }
            return {
                days: days,
                isOverdue: false
            }
        } else { //otherwise calculate overdue days
            while (dueDate.isBefore(momentDate, 'day')) {
                dueDate.add(1, 'days');
                if (!isWeekend(dueDate) && !isHoliday(holidays, dueDate)) {
                    days += 1;
                }
            }
            return {
                days: days,
                isOverdue: true
            }
        }
    }


    function daysLeftWithWeekends(holidays, dueDate) {
        var dueDate = moment(dueDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var momentDate = moment();
        var days = 0;
        if(dueDate > momentDate) { //calculate days remaining
            while (momentDate < dueDate) {
                momentDate.add(1, 'days');
                if (!isHoliday(holidays, momentDate)) {
                    days += 1;
                }
            }
            return {
                days: days,
                isOverdue: false
            }
        }
        else { //otherwise calculate overdue days
            while (dueDate.isBefore(today, 'day')) {
                dueDate.add(1, 'days');
                if (!isHoliday(holidays, dueDate)) {
                    days += 1;
                }
            }
            return {
                days: days,
                isOverdue: true
            }
        }
    }

    function isWeekend(momentObject) {
        return momentObject.isoWeekday() === 6 || momentObject.isoWeekday() === 7;
    }

    function isHoliday(holidays, momentObject) {
        return _.find(holidays, function(holiday) {
            return holiday.holidayDate === momentObject.format($translate.instant("common.frevvo.defaultDateFormat"));
        }) !== undefined;
    }

    function calculateOverdueDays(dueDate, remainingDays, holidays){
        var today = moment(new Date());
        var momentDueDate = moment(dueDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var countOverdueDays = 0;
        while (momentDueDate.isBefore(today, 'day')) {
            momentDueDate.add(1, 'days');
            if (!isWeekend(momentDueDate) && !isHoliday(holidays, momentDueDate)) {
                countOverdueDays += 1;
            }
        }
        if(countOverdueDays < 1){
            return false;
        }
        else {
            return {
                countOverdueDays: countOverdueDays,
                isOverdue: true
            };
        }
    }

    function calculateOverdueDaysWithWeekends(dueDate, remainingDays, holidays){
        var today = moment(new Date());
        var momentDueDate = moment(dueDate.replace(/(\d{4})\-(\d{2})\-(\d{2}).*/, '$1/$2/$3'));
        var countOverdueDays = 0;
        while (momentDueDate.isBefore(today, 'day')) {
            momentDueDate.add(1, 'days');
            if (!isHoliday(holidays, momentDueDate)) {
                countOverdueDays += 1;
            }
        }
        if(countOverdueDays < 1){
            return false;
        }
        else {
            return {
                countOverdueDays: countOverdueDays,
                isOverdue: true
            };
        }
    }



} ]);
