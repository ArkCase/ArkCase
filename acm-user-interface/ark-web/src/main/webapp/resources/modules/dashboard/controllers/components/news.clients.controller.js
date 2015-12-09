'use strict';

angular.module("dashboard.news").controller("Dashboard.NewsController", ["$scope", "Dashboard.WidgetService", "url",
    function ($scope, WidgetService, url) {

        var vm = this;

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'news');

        function applyConfig(e, componentId, config) {
            if (componentId == 'news') {

                var baseURL = config.url;
                var q = config.query;
                var query = q.replace("$1", url);

                WidgetService.getNews(baseURL, query).then(function (feed) {
                    vm.feed = feed;
                })
            }
        }
    }]);