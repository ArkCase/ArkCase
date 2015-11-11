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
             * @param {String} reportsHost String that represents reports server URL
             * @param {String} reportsPort String that represents reports server port
             * @param {String} reportUri String that represents report URL
             * @param {String} startDate String that represents value for date chosen from dateFrom input
             * @param {String} endDate String that represents value for date chosen from dateTo input
             * @param {String} dateFormat String that represents report server date format
             * @returns {Object} Object assigned as trusted for angular to display the report in an iFrame
             */
            getUrl: function (reportsHost, reportsPort, reportUri, stateSelected, startDate, endDate, dateFormat) {
                var reportUrl = reportsHost + reportsPort + reportUri
                    + "&startDate=" + startDate
                    + "&endDate=" + endDate
                    + "&dateFormat=" + encodeURIComponent(dateFormat);
                if(stateSelected){
                    reportUrl += "&caseStatus=" + stateSelected;
                }
                return $sce.trustAsResourceUrl(reportUrl);
            }
        }
    }
]);