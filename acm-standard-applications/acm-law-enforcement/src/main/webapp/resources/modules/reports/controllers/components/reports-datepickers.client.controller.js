'use strict';

angular.module('reports').controller('Reports.DatepickersController', ['$scope', 'UtilService', 'Util.DateService', function ($scope, Util, UtilDateService) {

    $scope.startDateChanged = function () {
        var todayDate = new Date();
        if (Util.isEmpty($scope.data.startDate)) {
            $scope.data.startDate = new Date();
        } else {
            $scope.data.startDate = UtilDateService.convertToCurrentTime($scope.data.startDate);
        }

        if (moment($scope.data.startDate).isAfter(todayDate)) {
            $scope.data.startDate = UtilDateService.convertToCurrentTime(todayDate);
        }

        if (moment($scope.data.startDate).isAfter($scope.data.endDate)) {
            $scope.data.endDate = UtilDateService.convertToCurrentTime($scope.data.startDate);
        }

        $scope.data.startDate = UtilDateService.setSameTime($scope.data.startDate, $scope.data.endDate);
    };

    $scope.endDateChanged = function () {
        if (Util.isEmpty($scope.data.endDate)) {
            $scope.data.endDate = UtilDateService.convertToCurrentTime($scope.data.startDate);
        } else {
            $scope.data.endDate = UtilDateService.convertToCurrentTime($scope.data.endDate);
        }

        if (moment($scope.data.endDate).isBefore($scope.data.startDate)) {
            $scope.data.endDate = UtilDateService.convertToCurrentTime($scope.data.startDate);
        }

        $scope.data.endDate = UtilDateService.setSameTime($scope.data.endDate, $scope.data.startDate);
    };

    $scope.opened = {};
    $scope.opened.openedStart = false;
    $scope.opened.openedEnd = false;
}
]);