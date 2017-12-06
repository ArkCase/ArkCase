'use strict';

angular.module('audit').controller('Audit.DatepickersController', ['$scope', 'UtilService', 'Util.DateService', function ($scope, Util, UtilDateService) {
    $scope.dateFrom = new Date();
    $scope.dateTo = new Date();
    var todayDate = new Date();


    $scope.dateFromChanged = function () {
        var todayDate = new Date();
        if(Util.isEmpty($scope.dateFrom)){
            $scope.dateFrom = new Date();
        } else {
            $scope.dateFrom = UtilDateService.convertToCurrentTime($scope.dateFrom);
        }

        if(moment($scope.dateFrom).isAfter(todayDate)){
            $scope.dateFrom = UtilDateService.convertToCurrentTime(todayDate);
        }

        if(moment($scope.dateFrom).isAfter($scope.dateTo)){
            $scope.dateTo = UtilDateService.convertToCurrentTime($scope.dateFrom);
        }

        $scope.dateFrom = UtilDateService.setSameDateTime($scope.dateFrom, $scope.dateTo);
        $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo, true);
    };

    $scope.dateToChanged = function () {
        var todayDate = new Date();
        if(Util.isEmpty($scope.dateTo)){
            $scope.dateTo = UtilDateService.convertToCurrentTime($scope.dateFrom);
        } else {
            $scope.dateTo = UtilDateService.convertToCurrentTime($scope.dateTo);
        }

        if(moment($scope.dateTo).isBefore($scope.dateFrom)){
            $scope.dateTo = UtilDateService.convertToCurrentTime($scope.dateFrom);
        }

        $scope.dateTo = UtilDateService.setSameDateTime($scope.dateTo, $scope.dateFrom);
        $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo, true);
    };

    $scope.dateFromChanged();
    $scope.dateToChanged();
    $scope.opened = {};
    $scope.opened.openedStart = false;
    $scope.opened.openedEnd = false;
}
]);
