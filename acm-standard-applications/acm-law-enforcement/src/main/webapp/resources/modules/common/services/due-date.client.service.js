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
        dueDateWorkingDays : dueDateWorkingDays
    });

    function dueDateWorkingDays(startDate, days, holidays) {
        var momentObject = moment(startDate).utc();
        var count = 0;
        while (count < days) {
            momentObject.add(1, 'days');

            if (momentObject.isoWeekday() !== 6 && momentObject.isoWeekday() !== 7) {
                var holidayDate = _.find(holidays, function(holiday) {
                    return holiday.holidayDate === momentObject.format("YYYY-MM-DD");
                });
                if (holidayDate === undefined) {
                    count += 1;
                }
            }

        }
        return momentObject.format("YYYY-MM-DD");
    }

});
