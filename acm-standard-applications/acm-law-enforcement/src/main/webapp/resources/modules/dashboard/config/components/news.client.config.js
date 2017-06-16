'use strict'

angular.module("dashboard.news", ["adf.provider"]).config(["dashboardProvider", function (ArkCaseDashboardProvider) {
    ArkCaseDashboardProvider.widget("news", {
        title: 'dashboard.widgets.news.title',
        description: 'dashboard.widgets.news.description',
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
}]);