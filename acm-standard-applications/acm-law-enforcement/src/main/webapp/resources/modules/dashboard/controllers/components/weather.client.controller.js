'use strict';

angular.module("dashboard.weather").controller("Dashboard.WeatherController", ["$scope", "$window", "params"
    , "Dashboard.WidgetService", "ConfigService"
    , function ($scope, $window, params
        , WidgetService, ConfigService
    ) {
        var vm = this;

        if(params.description !== undefined) {
            $scope.$parent.model.description = " - " + params.description;
        }

        ConfigService.getComponentConfig("dashboard", "weather").then(function (config) {
            var url = $window.location.origin + '/arkcase/weather';
            var appid = config.APPID;
            var units = config.units;

            vm.units = config.units;

            if (params.location != null) {
                WidgetService.getWeather(url, appid, params.location, units).then(function (weather) {
                    var oldWeather = JSON.parse($window.localStorage['lastWeatherData'] || '{}');
                    weather != null ? weather : oldWeather;

                    vm.weather = weather;
                });
            }
        });
    }
]);