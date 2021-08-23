'use strict';

angular.module('admin').controller('Admin.BusinessHoursController',
    ['$scope', 'Admin.BusinessHoursService','MessageService', function ($scope, BusinessHoursService,MessageService) {

        BusinessHoursService.getBusinessHoursConfig().then(function (response) {
            $scope.businessDayHoursEnabled = response.data[BusinessHoursService.PROPERTIES.BUSINESS_DAY_HOURS_FLAG];
            $scope.endOfBusinessDayTime = response.data[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_TIME];
            $scope.startOfBusinessDayTime = response.data[BusinessHoursService.PROPERTIES.START_OF_BUSINESS_DAY_TIME];
            $scope.configDataModel = response.data;
        });

        $scope.applyChanges = function () {
            $scope.configDataModel[BusinessHoursService.PROPERTIES.BUSINESS_DAY_HOURS_FLAG] = $scope.businessDayHoursEnabled;
            $scope.configDataModel[BusinessHoursService.PROPERTIES.END_OF_BUSINESS_DAY_TIME] = $scope.endOfBusinessDayTime;
            $scope.configDataModel[BusinessHoursService.PROPERTIES.START_OF_BUSINESS_DAY_TIME] = $scope.startOfBusinessDayTime;

            BusinessHoursService.saveBusinessHoursConfig($scope.configDataModel);
            MessageService.succsessAction();
        };

    }]);