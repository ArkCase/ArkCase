'use strict';

angular.module('admin').controller(
        'Admin.HolidayScheduleModalController',
        [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter',
                function($scope, $modalInstance, params, UtilDateService, $filter) {

                    $scope.schedule = {
                        holidayName : '',
                        holidayDate : ''
                    };

                    $scope.schedule.holidayName = params.schedule.holidayName;
                    $scope.schedule.holidayDate = new Date(params.schedule.holidayDate);

                    $scope.onClickCancel = function() {
                        $modalInstance.dismiss('Cancel');
                    };

                    $scope.onClickOk = function() {
                        var holidayConfig = {
                            holidayName : $scope.schedule.holidayName,
                            holidayDate : $filter('date')($scope.schedule.holidayDate, "yyyy-MM-dd")
                        };
                        $modalInstance.close({
                            schedule : holidayConfig
                        });
                    };

                } ]);