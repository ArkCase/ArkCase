'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', 'Helper.DashboardService'
    , function ($scope, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "cases"
            , dashboardName: "CASE"
        });
    }
]);