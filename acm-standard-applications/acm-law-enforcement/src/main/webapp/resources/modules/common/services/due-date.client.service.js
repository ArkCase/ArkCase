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
angular.module('services').service('DueDate.Service', function() {

    return ({
        dueDateWorkingDays : dueDateWorkingDays,
        dueDateWithWeekends : dueDateWithWeekends,
        workingDays : workingDays,
        workingDaysWithWeekends : workingDaysWithWeekends,
        daysLeft : daysLeft,
        daysLeftWithWeekends : daysLeftWithWeekends
    });

    function dueDateWorkingDays(startDate, days, holidays) {
        var momentObject = moment(startDate).utc();
        var count = 0;
        while (count < days) {
            momentObject.add(1, 'days');

            if (!isWeekend(momentObject) && !isHoliday(holidays, momentObject)) {
                count += 1;
            }

        }
        return momentObject.format("YYYY-MM-DD");
    }

    function dueDateWithWeekends(startDate, days, holidays) {
        var momentObject = moment(startDate).utc();
        var count = 0;
        while (count < days) {
            momentObject.add(1, 'days');

            if (!isHoliday(holidays, momentObject)) {
                count += 1;
            }

        }
        return momentObject.format("YYYY-MM-DD");
    }

    function workingDays(startDate, holidays) {
        var momentObject = moment(startDate).utc();
        var today = moment();
        var days = 0;
        while (momentObject < today) {
            momentObject.add(1, 'days');

            if (!isWeekend(momentObject) && !isHoliday(holidays, momentObject)) {
                days += 1;
            }
        }
        return days;
    }

    function workingDaysWithWeekends(startDate, holidays) {
        var momentObject = moment(startDate).utc();
        var today = moment();
        var days = 0;
        while (momentObject < today) {
            momentObject.add(1, 'days');

            if (!isHoliday(holidays, momentObject)) {
                days += 1;
            }
        }
        return days;
    }

    function daysLeft(holidays, dueDate) {
        var dueDate = moment(dueDate).utc();
        var momentDate = moment();
        var days = 0;
        while (momentDate < dueDate) {
            momentDate.add(1, 'days');
            if (!isWeekend(momentDate) && !isHoliday(holidays, momentDate)) {
                days += 1;
            }
        }
        return days;
    }

    function daysLeftWithWeekends(holidays, dueDate) {
        var dueDate = moment(dueDate).utc();
        var momentDate = moment();
        var days = 0;
        while (momentDate < dueDate) {
            momentDate.add(1, 'days');
            if (!isHoliday(holidays, momentDate)) {
                days += 1;
            }
        }
        return days;
    }

    function isWeekend(momentObject) {
        return momentObject.isoWeekday() === 6 || momentObject.isoWeekday() === 7;
    }

    function isHoliday(holidays, momentObject) {
        return _.find(holidays, function(holiday) {
            return holiday.holidayDate === momentObject.format("YYYY-MM-DD");
        }) !== undefined;
    }

});
