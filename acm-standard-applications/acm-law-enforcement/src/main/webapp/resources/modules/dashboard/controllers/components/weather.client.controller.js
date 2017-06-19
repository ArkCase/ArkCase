'use strict';

angular.module("dashboard.weather").controller("Dashboard.WeatherController", ["$scope", "$window", "location"
    , "Dashboard.WidgetService", "ConfigService"
    , function ($scope, $window, location
        , WidgetService, ConfigService
    ) {
        var vm = this;
        ConfigService.getComponentConfig("dashboard", "weather").then(function (config) {
            var url = $window.location.origin + '/arkcase/weather';
            var appid = config.APPID;
            var units = config.units;

            vm.units = config.units;

            if (location != null) {
                WidgetService.getWeather(url, appid, location, units).then(function (weather) {
                    var oldWeather = JSON.parse($window.localStorage['lastWeatherData'] || '{}');
                    weather != null ? weather : oldWeather;

                    vm.weather = weather;
                });
            }
        });
    }
]);