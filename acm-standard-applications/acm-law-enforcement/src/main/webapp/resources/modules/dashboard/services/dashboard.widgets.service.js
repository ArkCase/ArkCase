'use strict';

/**
 *@ngdoc service
 *@name dashboard.service:Dashboard.WidgetService
 *
 *@description
 *
 *{@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/services/dashboard.widgets.service.js modules/dashboard/services/dashboard.widgets.service.js}
 *
 *  The WidgetService is used for fetching data from news api service and for fetching RSS data.
 */
angular.module('dashboard').factory('Dashboard.WidgetService', [ '$http', function($http) {

    var data = {
        'getNews': getNews
    };

    /**
     * @ngdoc method
     * @name makeRequest
     * @methodOf dashboard.service:Dashboard.WidgetService
     *
     * @description
     *   General method for performing a http GET request for any provided URL using JSONP.
     *
     * @param {String} url  - Relative or absolute URL specifying the destination of the request
     * @returns {HttpPromise} Feature info about returned data from the provided  url.
     */
    function makeRequest(url) {
        return $http.jsonp(url).then(function (response) {
            return response;
        })["catch"](serviceError);
    }
    /**
     * @ngdoc method
     * @name getNews
     * @methodOf  dashboard.service:Dashboard.WidgetService
     *
     * @description
     * Fetches the RSS feed from the provided RSS URL using as a parameter to query the yahoo service,
     * and checks the validity of the response.
     *
     * @param {String} url - The base yahoo service api URL from where the RSS feed will be fetched
     * @param {String} rssUrl - RSS URL that is sent to the rss2json api to ask for RSS data.
     *
     * @returns {HttpPromise} Feature info about returned RSS data fetched from the provided RSS - url
     */
    function getNews(url, rssUrl) {
        var requestQuery = url + encodeURIComponent(rssUrl);
        return makeRequest(requestQuery).then(function (response) {
            return (response && response.data) ? response.data : null;
        });
    }
    return data;

    function serviceError(errorResponse) {
        $log.error("JSONP request failed for Dashboard.WidgetService");
        $log.error(errorResponse);
        return $q.reject(errorResponse);
    }
} ]);
