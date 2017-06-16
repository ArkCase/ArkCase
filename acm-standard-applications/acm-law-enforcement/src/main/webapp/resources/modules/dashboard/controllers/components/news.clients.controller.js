'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", ["$scope", "Dashboard.WidgetService", "url", "ConfigService",
    function ($scope, WidgetService, url, ConfigService) {

        var vm = this;

        ConfigService.getComponentConfig("dashboard", "news").then(function (config) {
            var baseURL = config.url;
            var q = config.query;
            var query = q.replace("$1", url);

            WidgetService.getNews(baseURL, query).then(function (feed) {
                vm.feed = feed;
            });
        });
    }]);