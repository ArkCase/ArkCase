'use strict';

angular.module('admin').controller(
        'Admin.HolidayModalController',
        [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter',
                function($scope, $modalInstance, params, UtilDateService, $filter) {

                    $scope.holiday = params.holiday;
                    $scope.holiday.holidayDate = new Date($scope.holiday.holidayDate);
                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };

                    $scope.onClickOk = function() {
                        $scope.holiday.holidayDate = $filter('date')($scope.holiday.holidayDate, "yyyy-MM-dd");

                        $modalInstance.close($scope.holiday);
                    };

                } ]);