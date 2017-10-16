'use strict';

angular.module("dashboard.news", ["adf.provider"]).config(function (ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget("news", {
        title: 'dashboard.widgets.news.title',
        description: 'dashboard.widgets.news.description',
        templateUrl: "modules/dashboard/views/components/news.client.view.html",
        reload: true,
        controller: "Dashboard.NewsController",
        controllerAs: "dashboardNews",
        resolve: {
            params: function (config) {
                return config;
            }
        },
        edit: {
            templateUrl: "modules/dashboard/views/components/news-edit.client.view.html"
        }
    })
});
