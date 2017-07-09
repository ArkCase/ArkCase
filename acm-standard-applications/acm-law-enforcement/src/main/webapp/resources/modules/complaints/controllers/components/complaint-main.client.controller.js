'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', 'Helper.DashboardService'
    , function ($scope, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "complaints"
            , dashboardName: "COMPLAINT"
        });

    }
]);