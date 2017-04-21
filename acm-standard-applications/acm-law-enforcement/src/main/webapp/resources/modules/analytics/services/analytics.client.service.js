'use strict';

/**
 * @ngdoc service
 * @name analytics.service:Analytics.BuildUrl
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/services/reports.client.service.js modules/reports/services/reports.client.service.js}
 *
 * The BuildUrl is used for building ELK analytics url with given parameters.
 */
angular.module('analytics').factory('Analytics.BuildUrl', ['$sce', '$http', '$browser', '$location',
    function ($sce, $http, $browser, $location) {
        return {

            /**
             * @ngdoc method
             * @name getUrl
             * @methodOf analytics.service:Analytics.BuildUrl
             *
             * @description
             * This function builds analytics ELK url with given parameters
             *
             * @param {Object} params Data required form generation ELK URL
             * @param {String} params.elkHost Represents ELK server URL
             * @param {String} params.elkPort Represents ELK server port
             * @param {String} params.elkUser The ELK user name
             * @param {String} params.elkPassword The ELK password
             * @param {String} params.elkDashboard The ELK Dashboard path
             * @returns {Object} Object assigned as trusted for angular to display the ELK in an iFrame
             */
            getUrl: function (params) {

                var elkUrl = params.elkHost + (params.elkPort ? ":" + params.elkPort : "") + params.elkDashboard;
                //var absUrl = $location.absUrl();
                //var baseHref = $browser.baseHref();
                //var appUrl = absUrl.substring(0, absUrl.indexOf(baseHref) + baseHref.length);
                //reportUrl += "&baseUrl=" + encodeURIComponent(appUrl);
                return $sce.trustAsResourceUrl(elkUrl);
            },

            getUrlBanana: function (params) {
                var bananaUrl = params.slkExternalUrl + params.slkDashboard;
                //var bananaUrl = params.slkHost + (params.slkPort ? ":" + params.slkPort : "") + params.slkDashboard;
                return $sce.trustAsResourceUrl(bananaUrl); //https://acm-arkcase/arkcase/undefined/banana
                //return bananaUrl;
            }
        }
    }
]);