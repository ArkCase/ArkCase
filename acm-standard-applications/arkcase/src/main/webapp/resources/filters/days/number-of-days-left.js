'use strict';
/**
 * @ngdoc filter
 * @name number-of-days-left.filter:numberOfDays
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/filters/days/number-of-days-left.client.filter.js filters/days/number-of-days-left.client.filter.js}
 *
 * @param {string} toDate Date string to count the days left;
 *
 * NumberOfDays filter used to calculate days left from today to due date
 */
angular.module('filters').filter('numberOfDaysLeft', function() {

    var millisecondsNumber = (1000 * 60 * 60 * 24);
    var today = new Date();
    today.setHours(0, 0, 0, 0);
    return function(toDateString) {

        var toDate = new Date(toDateString);
        toDate.setHours(0, 0, 0, 0);

        if (toDate && today) {
            var dayDiff = Math.floor((toDate - today) / millisecondsNumber);
            if (angular.isNumber(dayDiff)) {
                if (dayDiff < 0) {
                    dayDiff = 0;
                    return dayDiff;
                } else {
                    return dayDiff + 1;
                }

            }
        }
    };
});