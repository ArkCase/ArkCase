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
     * @param {Object} config - Configuration object
     * @returns {HttpPromise} Feature info about returned data from the provided  url.
     */
    function makeRequest(url, config) {
        return $http.jsonp(url, config).then(function(response) {
            return response;
        })["catch"](serviceError);
    }
    /**
     * @ngdoc method
     * @name getNews
     * @methodOf  dashboard.service:Dashboard.WidgetService
     *
     * @description
     * Fetches the RSS feed from the provided RSS URL using YQL query as a parameter to query the yahoo service,
     * and checks the validity of the response.
     *
     * @param {String} url - The base yahoo service api URL from where the RSS feed will be fetched
     * @param {String} query - YQL query that is sent to the yahoo api to ask for RSS data. In this query
     * the RSS url is included! Use the following link https://developer.yahoo.com/yql/guide/yql-tutorials.html as a
     * starting point for creating a YQL queries.
     *
     * @returns {HttpPromise} Feature info about returned RSS data fetched from the provided RSS - url
     */
    function getNews(url, query) {
        var configObj = {
            params: {
                callback: "JSON_CALLBACK",
                format: "json",
                q: query
            }
        };
        return makeRequest(url, configObj).then(function(response) {
            return (response && response.data && response.data.query.results) ? response.data.query.results.rss.channel : null;
        });
    }
    return data;

    function serviceError(errorResponse) {
        $log.error("JSONP request failed for Dashboard.WidgetService");
        $log.error(errorResponse);
        return $q.reject(errorResponse);
    }
} ]);
