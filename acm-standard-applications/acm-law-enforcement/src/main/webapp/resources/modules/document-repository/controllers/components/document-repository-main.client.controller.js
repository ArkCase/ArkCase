'use strict';

angular.module('document-repository').controller('DocumentRepository.MainController', ['$scope', 'Helper.DashboardService'
    , function ($scope, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "document-repository"
            , dashboardName: "DOC_REPO"
        });

    }
]);