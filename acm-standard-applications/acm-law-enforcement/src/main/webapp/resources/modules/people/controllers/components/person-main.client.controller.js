'use strict';

angular.module('people').controller('People.MainController', ['$scope', 'Helper.DashboardService'
    , function ($scope, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "people"
            , dashboardName: "PERSON"
        });

    }
]);