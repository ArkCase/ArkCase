'use strict';

/**
 *@ngdoc service
 *@name dashboard.service:Dashboard.WidgetService
 *
 *@description
 *
 *{@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/services/dashboard.widgets.service.js modules/dashboard/services/dashboard.widgets.service.js}
 *
 *  The WidgetService is used for fetching data from weather api service and for fetching RSS data.
 */
angular.module('dashboard').factory('Dashboard.WidgetService', ['$http', '$log', '$window',
    function ($http, $log, $window) {

        var data = {
            'getNews': getNews,
            'getWeather': getWeather
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
            return $http.jsonp(url, config).then(function (response) {
                return response;
            }).catch(serviceError);
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
            return makeRequest(url, configObj).then(function (response) {
                return ( response && response.data && response.data.query.results ) ?
                    response.data.query.results.rss.channel : null;
            });
        }

        /**
         * @ngdoc method
         * @name getWeather
         * @methodOf dashboard.service:Dashboard.WidgetService
         *
         * @description
         * This method make a HTTP GET request to the weather service, get needed data from the JSON response and
         * puts it in the weather object that is returned and is used by the widget.
         *
         * @param {String} url - The base url of the weather service api without parameters.
         * @param {String} appid - the APPID ( unique application ID, provided by the weather service )
         * @param {String} location - The location for which the method is going to fetch a weather data.
         * @param {String} units - units in which retrieved data will be represented ( metric, imperial )
         *
         * @returns {HttpPromise} Feature info about weather object created in this method and filed with the needed
         * data fetched from the weather api service.
         */
        function getWeather(url, appid, location, units) {

            var configObj = {
                params: {
                    callback: "JSON_CALLBACK",
                    units: units,
                    q: location,
                    APPID: appid
                }
            };

            return makeRequest(url, configObj).then(function (response) {
                var weather = {location: {}, temp: {}, clouds: null};
                if (response.data ) {
                    if (response.data.main) {

                        weather.temp.current = response.data.main.temp;
                        weather.temp.min = response.data.main.temp_min;
                        weather.temp.max = response.data.main.temp_max;
                        weather.location.city = response.data.name;
                        weather.location.country = response.data.sys.country;

                    }

                    weather.imgId = response.data.weather[0].icon ? response.data.weather[0].icon : undefined;

                    $window.localStorage['lastWeatherData'] = JSON.stringify(weather);

                    return response.data && 200 === response.data.cod ? weather : null;

                } else {
                    return JSON.parse($window.localStorage['lastWeatherData'] || '{}');
                }
            });
        }

        return data;

        function serviceError(errorResponse) {
            $log.error("JSONP request failed for Dashboard.WidgetService");
            $log.error(errorResponse);
            return $q.reject(errorResponse);
        }
    }
]);
