'use strict';

angular.module('audit').controller('Audit.DatepickersController', ['$scope', function ($scope) {

        $scope.dateFrom = new Date();
        $scope.dateTo = new Date();
        $scope.$watchGroup(['dateFrom','dateTo'], function(){
            $scope.$emit('send-date', $scope.dateFrom, $scope.dateTo);
        });

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;

        $scope.minDate = new Date();
    }
]);