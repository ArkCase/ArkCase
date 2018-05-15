'use strict';

angular.module('my-documents').controller('MyDocuments.MainController', [ '$scope', 'Helper.DashboardService', function($scope, DashboardHelper) {

    new DashboardHelper.Dashboard({
        scope: $scope,
        moduleId: "my-documents",
        dashboardName: "MY_DOC_REPO"
    });

} ]);