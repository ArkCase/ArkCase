'use strict';

angular.module('admin').controller('Admin.ApplicationConfigurationController',
    ['$scope', 'Admin.ApplicationSettingsService', function ($scope, ApplicationSettingsService) {

        $scope.isTimezoneValid = true;

        ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
            $scope.defaultTimezone = response.data[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE];
            $scope.configDataModel = response.data;
        });

        $scope.validateTimezone = function () {
            try {
                Intl.DateTimeFormat(undefined, {timeZone: $scope.defaultTimezone});
                $scope.isTimezoneValid = true;
            } catch (ex) {
                $scope.isTimezoneValid = false;
            }
        };

        $scope.applyChanges = function () {
            if ($scope.configDataModel[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE] !== $scope.defaultTimezone) {

                $scope.configDataModel[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE] = $scope.defaultTimezone;
                ApplicationSettingsService.saveApplicationPropertyConfig($scope.configDataModel);
            }
        };

    }]);