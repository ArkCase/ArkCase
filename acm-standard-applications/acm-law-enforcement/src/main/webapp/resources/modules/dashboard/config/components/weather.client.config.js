'use strict';

angular.module("dashboard.weather", ["adf.provider"]).config(function (ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget("weather", {
        title: 'dashboard.widgets.weather.title',
        description: 'dashboard.widgets.weather.description',
        templateUrl: "modules/dashboard/views/components/weather.client.view.html",
        controller: 'Dashboard.WeatherController',
        controllerAs: 'dashboardWeather',
        reload: true,
        resolve: {
            params: function (config) {
                return config;
            }
        },
        edit: {
            templateUrl: 'modules/dashboard/views/components/weather-edit.client.view.html'
        }
    })
});