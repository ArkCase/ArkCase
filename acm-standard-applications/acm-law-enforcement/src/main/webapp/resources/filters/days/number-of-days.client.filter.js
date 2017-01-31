'use strict';
/**
 * @ngdoc filter
 * @name number-of-days.filter:numberOfDays
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/filters/days/number-of-days.client.filter.js filters/days/number-of-days.client.filter.js}
 *
 * @param {string} fromDateString Date string to start the count of days;
 *
 * NumberOfDays filter used to calculate days from date string until current day
 * hours are not calculated.
 */
angular.module('filters').filter('numberOfDays', function () {

    var millisecondsNumber = (1000 * 60 * 60 * 24);
    var toDate = new Date();
    toDate.setHours(0, 0, 0, 0);
    return function (fromDateString) {

        var fromDate = new Date(fromDateString);
        fromDate.setHours(0, 0, 0, 0);

        if (toDate && fromDate) {
            var dayDiff = Math.floor((toDate - fromDate) / millisecondsNumber);
            if (angular.isNumber(dayDiff)) {
                return dayDiff + 1;
            }
        }
    };
});