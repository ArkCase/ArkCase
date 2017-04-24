'use strict';

/**
 * @ngdoc service
 * @name analytics.service:Analytics.BuildUrl
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/analytics/services/analytics.client.service.js modules/analytics/services/analytics.client.service.js}
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

            /**
             * @ngdoc method
             * @name getUrlBanana
             * @methodOf analytics.service:Analytics.BuildUrl
             *
             * @description
             * This function builds url of Banana visualization for Data Analytics with given parameters
             * @param {String} params.slkExternalUrl The ArkCase url (assuming proxy config)
             * @param {String} params.slkDashboard The Banana url for the dashboard
             * @returns {Object} Object assigned as trusted for angular to display the Banana url in an iFrame
             */
            getUrlBanana: function (params) {
                var bananaUrl = params.slkExternalUrl + params.slkDashboard;
                //var bananaUrl = params.slkHost + (params.slkPort ? ":" + params.slkPort : "") + params.slkDashboard;
                return $sce.trustAsResourceUrl(bananaUrl);
            }
        }
    }
]);