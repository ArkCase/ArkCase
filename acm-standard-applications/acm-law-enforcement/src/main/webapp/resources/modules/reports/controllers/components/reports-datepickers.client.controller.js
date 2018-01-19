'use strict';

angular.module('reports').controller('Reports.DatepickersController',
        [ '$scope', 'UtilService', 'Util.DateService', function($scope, Util, UtilDateService) {

            $scope.startDateChanged = function() {
                var validateDate = UtilDateService.validateFromDate($scope.data.startDate, $scope.data.endDate);
                $scope.data.startDate = validateDate.from;
                $scope.data.endDate = validateDate.to;
            };

            $scope.endDateChanged = function() {
                var validateDate = UtilDateService.validateToDate($scope.data.startDate, $scope.data.endDate);
                $scope.data.startDate = validateDate.from;
                $scope.data.endDate = validateDate.to;
            };

            $scope.opened = {};
            $scope.opened.openedStart = false;
            $scope.opened.openedEnd = false;
        } ]);