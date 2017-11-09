'use strict';

angular.module('reports').controller('Reports.DatepickersController', ['$scope', function ($scope) {
        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
    }
]);