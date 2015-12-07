'use strict'

angular.module("dashboard.news", ["adf.provider"]).config(["dashboardProvider", function (dashboardProvider) {
    dashboardProvider.widget("news", {
        title: 'News',
        description: 'Displays a RSS/Atom feed',
        templateUrl: "modules/dashboard/views/components/news.client.view.html",
        reload: true,
        controller: "Dashboard.NewsController",
        controllerAs: "dashboardNews",
        resolve: {
            url: function (config) {
                return config.url ? config.url : void 0
            }
        },
        edit: {
            templateUrl: "modules/dashboard/views/components/news-edit.client.view.html"
        }
    })
}])