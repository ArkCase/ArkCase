'use strict';

angular.module("dashboard.weather", ["adf.provider"]).config(["dashboardProvider",
    function (dashboardProvider) {
        dashboardProvider.widget("weather", {
            title: 'Weather',
            description: 'Display the current temperature of a city',
            templateUrl: "modules/dashboard/views/components/weather.client.view.html",
            controller: 'Dashboard.WeatherController',
            controllerAs: 'dashboardWeather',
            reload: true,
            resolve: {
                location: function (config) {
                    return config.location ? config.location : void 0
                }
            },
            edit: {
                templateUrl: 'modules/dashboard/views/components/weather-edit.client.view.html'
            }
        })
    }
])