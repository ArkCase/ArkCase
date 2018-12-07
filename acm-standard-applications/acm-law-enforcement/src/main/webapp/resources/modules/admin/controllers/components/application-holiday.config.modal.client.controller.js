'use strict';

angular.module('admin').controller('Admin.HolidayModalController', [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', function($scope, $modalInstance, params, UtilDateService, $filter) {

        
    $scope.holidays = params.holidays;
    $scope.holidays.holidayDate = new Date($scope.holidays.holidayDate);
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $scope.holidays.holidayDate = $filter('date')($scope.holidays.holidayDate, "yyyy-MM-dd");

        $modalInstance.close($scope.holidays);
    };

} ]);