'use strict';

/**
 * @ngdoc service
 * @name audit.service:AuditController.BuildUrl
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/audit/services/audit.client.service.js modules/audit/services/audit.client.service.js}
 *
 * The BuildUrl is used for building audit report url with given parametars.
 */
angular.module('audit').factory('AuditController.BuildUrl', ['$sce', '$location', '$browser', 'Util.DateService',
    function ($sce, $location, $browser,UtilDateService) {
        return {

            /**
             * @ngdoc method
             * @name getUrl
             * @methodOf audit.service:AuditController.BuildUrl
             *
             * @description
             * This function builds audit report url with given parametars
             *
             * @param {String} pentahoHost String that represents Pentaho server URL
             * @param {String} pentahoPort String that represents Pentaho server port
             * @param {String} auditReportUri String that represents audit report URL
             * @param {String} startDate String that represents value for date chosen from dateFrom input
             * @param {String} endDate String that represents value for date chosen from dateTo input
             * @param {String} objectType String that represents selected value from audit dropdown(default is ALL)
             * @param {String} objectId String that represents value from text input(default is empty string "")
             * @param {String} dateFormat String that represents pentaho date format
             * @param {Boolean} useBaseUrl boolean that represent should baseUrl should be generated and sent as parameter
             * @param {String} pentahoUser Pentaho user name
             * @param {String} pentahoPassword Pentaho password
             * @returns {String} Builded url for audit report url that will be shown in iframe
             */
            getUrl: function (pentahoHost, pentahoPort, auditReportUri, startDate, endDate, objectType, objectId,
                              dateFormat, useBaseUrl, pentahoUser, pentahoPassword) {
                var useUrl = useBaseUrl || false;
                var amendedPentahoPort = "";
                if (pentahoPort && pentahoPort.length > 0) {
                    amendedPentahoPort = pentahoPort;
                    if (pentahoPort.charAt(0) != ':' && pentahoHost.charAt(pentahoHost.length - 1) != ':') {
                        amendedPentahoPort = ':' + amendedPentahoPort;
                    }
                }

                var reportUrl = pentahoHost + amendedPentahoPort + auditReportUri
                    + "?startDate=" + startDate
                    + "&endDate=" + endDate
                    + "&objectType=" + objectType
                    + "&objectId=" + objectId
                    + "&dateFormat=" + encodeURIComponent(dateFormat)
                    + "&timeZone=" + encodeURIComponent(UtilDateService.getTimeZoneOffset());
                if (useUrl) {
                    var absUrl = $location.absUrl();
                    var baseHref = $browser.baseHref();
                    var appUrl = absUrl.substring(0, absUrl.indexOf(baseHref) + baseHref.length);
                    reportUrl += "&baseUrl=" + encodeURIComponent(appUrl);
                }
                return $sce.trustAsResourceUrl(reportUrl);
            }
        }
    }
]);