'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", ["$scope", "Dashboard.WidgetService", "params", "ConfigService",
    function ($scope, WidgetService, params, ConfigService) {

        var vm = this;

        if(params.description !== undefined) {
            $scope.$parent.model.description = " - " + params.description;
        }

        ConfigService.getComponentConfig("dashboard", "news").then(function (config) {
            var baseURL = config.url;
            var q = config.query;
            var query = q.replace("$1", params.url);

            WidgetService.getNews(baseURL, query).then(function (feed) {
                vm.feed = feed;
            });
        });
    }]);