'use strict';

angular.module('admin').controller('Admin.BusinessHoursController',
    ['$scope', 'Admin.BusinessHoursService', function ($scope, BusinessHoursService) {

        BusinessHoursService.getBusinessHoursConfig().then(function (response) {
            $scope.endOfBusinessDayEnabled = response.data[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_FLAG];
            $scope.endOfBusinessDayTime = response.data[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_TIME];
            $scope.configDataModel = response.data;
        });

        $scope.applyChanges = function () {
            $scope.configDataModel[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_FLAG] = $scope.endOfBusinessDayEnabled;
            $scope.configDataModel[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_TIME] = $scope.endOfBusinessDayTime;

            BusinessHoursService.saveBusinessHoursConfig($scope.configDataModel);
        };

    }]);