'use strict';

angular.module('consultations').controller('Consultations.MainController', [ '$scope', 'Helper.DashboardService', function($scope, DashboardHelper) {

    new DashboardHelper.Dashboard({
        scope: $scope,
        moduleId: "consultations",
        dashboardName: "CONSULTATION"
    });
} ]);