'use strict'

/**
 * @ngdoc filter
 * @name dashboard.my-tasks.filter:filterColumn
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/filters/tasks.columnid.client.filter.js modules/dashboard/filters/tasks.columnid.client.filter.js}
 *
 * Filters the input value of attachedToObjectName to determine if it is id. If it is it returns the value to the id column
 * of task widget. If it is not, the id column is left blank.
 * */

angular.module("dashboard.my-tasks").filter('filterColumn', function ($filter) {
    return function (input, char) {
        if (input == null) {
            return;
        }
        if (input.indexOf(char) > -1) {
            return input;
        }
        return "";
    };
})
