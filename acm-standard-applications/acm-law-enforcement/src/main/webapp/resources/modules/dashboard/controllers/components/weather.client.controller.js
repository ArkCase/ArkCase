'use strict';

angular.module("dashboard.weather").controller("Dashboard.WeatherController", ["$scope", "$window", "params"
    , "Dashboard.WidgetService", "ConfigService", "UtilService"
    , function ($scope, $window, params
        , WidgetService, ConfigService, Util
    ) {
        var vm = this;

        if(!Util.isEmpty(params.description)) {
            $scope.$parent.model.description = " - " + params.description;
        }
        else {
            $scope.$parent.model.description = "";
        }


        ConfigService.getComponentConfig('dashboard', 'weather').then(function (config) {
            var url = $window.location.origin + '/arkcase/weather';
            var appid = config.APPID;
            var location = params.location;
            var zip = params.zip;
            var units = config.units;
            var type = config.type;

            vm.units = config.units;

            if (params.location != null || params.zip != null) {
                WidgetService.getWeather(url, appid, location, zip, units, type).then(function (weather) {
                    var oldWeather = JSON.parse($window.localStorage['lastWeatherData'] || '{}');
                    weather != null ? weather : oldWeather;

                    vm.weather = weather;
                });
            }
        });
    }
]);