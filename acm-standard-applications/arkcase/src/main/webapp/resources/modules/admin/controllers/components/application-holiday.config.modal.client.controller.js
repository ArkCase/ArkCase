'use strict';

angular.module('admin').controller('Admin.HolidayModalController', ['$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', 'UtilService', function ($scope, $modalInstance, params, UtilDateService, $filter, UtilService) {


    $scope.holidays = params.holidays;
    $scope.holidayDate = moment(params.holidays.holidayDate);
    if (!UtilService.isEmpty($scope.holidays.holidayDate)) {
        $scope.holidays.holidayDate = new Date($scope.holidays.holidayDate);
    } else {
        $scope.holidays.holidayDate = new Date();
    }
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $scope.holidays.holidayDate = $filter('date')($scope.holidayDate, "yyyy-MM-dd");

        $modalInstance.close($scope.holidays);
    };

} ]);