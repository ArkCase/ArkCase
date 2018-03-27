'use strict';

angular.module('admin').controller(
        'Admin.HolidayModalController',
        [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter',
                function($scope, $modalInstance, params, UtilDateService, $filter) {

                    $scope.holidays = {
                        holidayName : '',
                        holidayDate : ''
                    };

                    $scope.holidays.holidayName = params.holidays.holidayName;
                    $scope.holidays.holidayDate = new Date(params.holidays.holidayDate);

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };

                    $scope.onClickOk = function() {
                        var holidayConfig = {
                            holidayName : $scope.holidays.holidayName,
                            holidayDate : $filter('date')($scope.holidays.holidayDate, "yyyy-MM-dd")
                        };
                        $modalInstance.close({
                            holidays : holidayConfig
                        });
                    };

                } ]);