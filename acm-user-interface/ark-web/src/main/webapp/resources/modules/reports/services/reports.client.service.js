'use strict';

/**
 * @ngdoc service
 * @name reports.service:Reports.BuildUrl
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/services/reports.client.service.js modules/reports/services/reports.client.service.js}
 *
 * The BuildUrl is used for building report url with given parameters.
 */
angular.module('reports').factory('Reports.BuildUrl', ['$sce',
    function ($sce) {
        return {

            /**
             * @ngdoc method
             * @name getUrl
             * @methodOf reports.service:Reports.BuildUrl
             *
             * @description
             * This function builds report url with given parameters
             *
             * @param {Object} params Data required form generation report URL
             * @param {String} params.reportSelected Selected report
             * @param {Array}  params.reports List of reports
             * @param {String} params.reportsHost Represents reports server URL
             * @param {String} params.reportsPort Represents reports server port
             * @param {String} params.reportUri Represents report URL
             * @param {String} params.startDate Represents value for date chosen from dateFrom input
             * @param {String} params.endDate Represents value for date chosen from dateTo input
             * @param {String} params.reportDateFormat Represents report server date format
             * @param {String} params.stateSelected Represents report server date format
             * @returns {Object} Object assigned as trusted for angular to display the report in an iFrame
             */
            getUrl: function (params) {
                var reportUrl = params.reportsHost + params.reportsPort + params.reports[params.reportSelected]
                    + "&startDate=" + moment(params.startDate).format(params.dateFormat)
                    + "&endDate=" + moment(params.endDate).format(params.dateFormat)
                    + "&dateFormat=" + encodeURIComponent(params.reportDateFormat);
                if(params.stateSelected){
                    reportUrl += "&caseStatus=" + params.stateSelected;
                }
                return $sce.trustAsResourceUrl(reportUrl);
            }
        }
    }
]);