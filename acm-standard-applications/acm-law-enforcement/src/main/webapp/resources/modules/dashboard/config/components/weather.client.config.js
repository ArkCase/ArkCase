'use strict';

angular.module("dashboard.weather", ["adf.provider"]).config(["dashboardProvider",
    function (ArkCaseDashboardProvider) {
        ArkCaseDashboardProvider.widget("weather", {
            title: 'dashboard.widgets.weather.title',
            description: 'dashboard.widgets.weather.description',
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