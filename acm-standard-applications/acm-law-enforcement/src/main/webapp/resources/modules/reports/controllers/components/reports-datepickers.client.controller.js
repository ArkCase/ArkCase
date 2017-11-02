'use strict';

angular.module('reports').controller('Reports.DatepickersController', ['$scope', 'Helper.LocaleService',
    function ($scope, LocaleHelper) {

        new LocaleHelper.Locale({scope: $scope});

        $scope.opened = {};
        $scope.opened.openedStart = false;
        $scope.opened.openedEnd = false;
    }
]);