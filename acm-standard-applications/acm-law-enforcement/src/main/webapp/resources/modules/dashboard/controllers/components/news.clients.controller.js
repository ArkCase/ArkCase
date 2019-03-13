'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", ["$scope", "$translate", "Dashboard.WidgetService", "params", "ConfigService", "UtilService", "MessageService",
    function ($scope, $translate, WidgetService, params, ConfigService, Util, MessageService) {

    if (!Util.isEmpty(params.description)) {
        $scope.$parent.model.description = " - " + params.description;
    } else {
        $scope.$parent.model.description = "";
    }

    ConfigService.getComponentConfig("dashboard", "news").then(function(config) {
        var baseURL = config.url;
        var rssURL = params.url;

        if (rssURL) {
            WidgetService.getNews(baseURL, rssURL).then(function (feed) {
                $scope.feed = feed.items;
                MessageService.succsessAction()
            }, function (error) {
                MessageService.error($translate.instant("dashboard.widgets.news.message.error"));
                return error;
            });
        }
    });
} ]);