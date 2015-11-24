'use strict';
angular.module("dashboard.news", ["adf.provider"]).config(["dashboardProvider",
    function (dashboardProvider) {
        dashboardProvider.widget("news", {
            title: 'News',
            description: 'Displays a RSS/Atom feed',
            templateUrl: "modules/dashboard/views/components/news.client.view.html",
            reload: true,
            controller: "Dashboard.NewsController",
            resolve: {
                url: function (config) {
                    return config.url ? config.url : void 0
                }
            },
            edit: {
                templateUrl: "modules/dashboard/views/components/news-edit.client.view.html"
            }
        })
    }
]).service("newsService", ["$q", "$http",
    function ($q, $http) {
        return {
            get: function (newsUrl, url) {
                var deferred = $q.defer();
                return $http.jsonp(newsUrl + encodeURIComponent(url)).success(function (data) {
                    data && data.responseData && data.responseData.feed ? deferred.resolve(data.responseData.feed) : deferred.reject()
                }).error(function () {
                    deferred.reject()
                }), deferred.promise
            }
        }
    }
]).controller("Dashboard.NewsController", ["$scope", "newsService", "url",
    function ($scope, newsService, url) {

        $scope.$on('component-config', applyConfig);
        $scope.$emit('req-component-config', 'news');

        function applyConfig(e, componentId, config) {
            if (componentId == 'news') {
                $scope.config = config;
                $scope.newsUrl = config.url;


                newsService.get($scope.newsUrl, url).then(function (feed) {
                    $scope.feed = feed;
                })
            }
        }
    }
]);