'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", [ "$scope", "Dashboard.WidgetService", "params", "ConfigService", "UtilService", function($scope, WidgetService, params, ConfigService, Util) {

    if (!Util.isEmpty(params.description)) {
        $scope.$parent.model.description = " - " + params.description;
    } else {
        $scope.$parent.model.description = "";
    }

    ConfigService.getComponentConfig("dashboard", "news").then(function(config) {
        var baseURL = config.url;
        var rssURL = params.url;

        WidgetService.getNews(baseURL, rssURL).then(function (feed) {
            $scope.feed = feed.items;
        });
    });
} ]);