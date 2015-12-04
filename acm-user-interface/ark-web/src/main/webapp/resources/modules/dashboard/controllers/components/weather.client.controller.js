'use strict';

angular.module("dashboard.weather").controller("Dashboard.WeatherController", ["$scope", "location", "Dashboard.WidgetService",
    function ($scope, location, WidgetService) {
        var vm = this;

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'weather');

        function applyConfig(e, componentId, config) {
            if (componentId == 'weather') {

                //var configObj = {
                //    params: {
                //        units: config.units,
                //        callback: "JSON_CALLBACK",
                //        APPID: config.APPID,
                //        q: location
                //    }
                //}

                var appid = config.APPID;
                var units = config.units;

                vm.units = config.units;

                var url = "http://api.openweathermap.org/data/2.5/weather";

                WidgetService.getWeather(url, appid, location, units).then(function (weather) {
                    vm.weather = weather;
                });
            }
        }
    }
])