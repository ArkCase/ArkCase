'use strict';

angular.module('audit').controller('Audit.DatepickersController', ['$scope', 'UtilService', function ($scope, Util) {
        $scope.dateFrom = new Date();
        $scope.dateTo = new Date();
        var todayDate = new Date();

        $scope.dateFromChanged = function () {
            var todayDate = new Date();
            if(Util.isEmpty($scope.dateFrom)){
                $scope.dateFrom = moment()
                    .year(todayDate.getFullYear())
                    .month(todayDate.getMonth())
                    .date(todayDate.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            } else {
                $scope.dateFrom = moment()
                    .year($scope.dateFrom.getFullYear())
                    .month($scope.dateFrom.getMonth())
                    .date($scope.dateFrom.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            }

            if(moment($scope.dateFrom).isAfter($scope.dateTo)){
                $scope.dateTo = moment()
                    .year($scope.dateFrom.getFullYear())
                    .month($scope.dateFrom.getMonth())
                    .date($scope.dateFrom.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            }

            $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo, true);
        };

        $scope.dateToChanged = function () {
            var todayDate = new Date();
            if(Util.isEmpty($scope.dateTo)){
                $scope.dateTo = moment()
                    .year($scope.dateFrom.getFullYear())
                    .month($scope.dateFrom.getMonth())
                    .date($scope.dateFrom.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            } else {
                $scope.dateTo = moment()
                    .year($scope.dateTo.getFullYear())
                    .month($scope.dateTo.getMonth())
                    .date($scope.dateTo.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            }

            if(moment($scope.dateTo).isBefore($scope.dateFrom)){
                $scope.dateTo = moment()
                    .year($scope.dateFrom.getFullYear())
                    .month($scope.dateFrom.getMonth())
                    .date($scope.dateFrom.getDate())
                    .hours(todayDate.getHours())
                    .minutes(todayDate.getMinutes())
                    .seconds(todayDate.getSeconds())._d;
            }

            $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo, true);
        };

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
    }
]);