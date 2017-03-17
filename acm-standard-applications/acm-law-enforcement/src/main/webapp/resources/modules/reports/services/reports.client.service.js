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
angular.module('reports').factory('Reports.BuildUrl', ['$sce', 'Util.DateService', '$http', '$browser', '$location',
    function ($sce, UtilDateService, $http, $browser, $location) {
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
             * @param {String} params.startDate Represents value for date chosen from dateFrom input
             * @param {String} params.endDate Represents value for date chosen from dateTo input
             * @param {String} params.reportsUser The Pentaho user name
             * @param {String} params.reportsPassword The Pentaho password
             * @param {String} params.stateSelected Represents report server date format
             * @returns {Object} Object assigned as trusted for angular to display the report in an iFrame
             */
            getUrl: function (params) {

                var reportUrl = params.reportsHost + (params.reportsPort ? ":" + params.reportsPort : "") + params.reports[params.reportSelected]
                    + "?startDate=" + UtilDateService.goodIsoDate(params.startDate)
                    + "&endDate=" + UtilDateService.goodIsoDate(params.endDate)
                    + "&dateFormat=" + encodeURIComponent(UtilDateService.defaultDateFormat)
                    + "&timeZone=" + encodeURIComponent(UtilDateService.getTimeZoneOffset());
                if (params.stateSelected) {
                    reportUrl += "&status=" + params.stateSelected;
                }
                var absUrl = $location.absUrl();
                var baseHref = $browser.baseHref();
                var appUrl = absUrl.substring(0, absUrl.indexOf(baseHref) + baseHref.length);
                reportUrl += "&baseUrl=" + encodeURIComponent(appUrl);
                return $sce.trustAsResourceUrl(reportUrl);
            },

            /**
             * @ngdoc method
             * @name getAuthorizedReports
             * @methodOf reports.service:Reports.BuildUrl
             *
             * @description
             * Performs retrieving all reports that particular user has authorized access
             *
             * @returns {HttpPromise} Future info about accessible reports
             */
            getAuthorizedReports: function () {
                return $http({
                    method: "GET",
                    url: "api/latest/plugin/report/authorized"
                });
            }
        }
    }
]);