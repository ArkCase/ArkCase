'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", ["$scope", "Dashboard.WidgetService", "params", "ConfigService", "UtilService",
    function ($scope, WidgetService, params, ConfigService, Util) {

        var vm = this;

        if(!Util.isEmpty( params.description)) {
            $scope.$parent.model.description = " - " + params.description;
        }
        else {
            $scope.$parent.model.description = "";
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