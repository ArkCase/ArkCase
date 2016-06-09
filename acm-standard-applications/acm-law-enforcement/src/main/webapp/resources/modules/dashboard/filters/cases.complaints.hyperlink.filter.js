'use strict'

/**
 * @ngdoc filter
 * @name dashboard.my-tasks.filter:hyperlinkFilter
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/filters/cases.complaints.hyperlink.filter.js modules/dashboard/filters/cases.complaints.hyperlink.filter.js}
 *
 * Filters the input value of attachedToObjectType to define the hyperlink (/cases/ or /complaints/)
 */

angular.module("dashboard.my-tasks").filter('hyperlinkFilter', function ($filter) {
    return function (input, char) {
        if(input == null) {
            return;
        }
        if(input == 'CASE_FILE'){
            return 'cases';
        }
        if(input == 'COMPLAINT')
        return 'complaints';
    };
})
