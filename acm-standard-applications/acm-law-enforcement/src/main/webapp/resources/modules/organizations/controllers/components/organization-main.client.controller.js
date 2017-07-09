'use strict';

angular.module('organizations').controller('Organizations.MainController', ['$scope', 'Helper.DashboardService'
    , function ($scope, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "organizations"
            , dashboardName: "ORGANIZATION"
        });

    }
]);