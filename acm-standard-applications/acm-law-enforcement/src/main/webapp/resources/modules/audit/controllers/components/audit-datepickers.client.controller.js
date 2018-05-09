'use strict';

angular.module('audit').controller('Audit.DatepickersController', [ '$scope', 'UtilService', 'Util.DateService', function($scope, Util, UtilDateService) {
    $scope.dateFrom = new Date();
    $scope.dateTo = new Date();

    $scope.dateFromChanged = function() {
        var validateDate = UtilDateService.validateFromDate($scope.dateFrom, $scope.dateTo);
        $scope.dateFrom = validateDate.from;
        $scope.dateTo = validateDate.to;

        $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo);
    };

    $scope.dateToChanged = function() {
        var validateDate = UtilDateService.validateToDate($scope.dateFrom, $scope.dateTo);
        $scope.dateFrom = validateDate.from;
        $scope.dateTo = validateDate.to;

        $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo);
    };

    $scope.dateFromChanged();
    $scope.dateToChanged();
    $scope.opened = {};
    $scope.opened.openedStart = false;
    $scope.opened.openedEnd = false;
} ]);
