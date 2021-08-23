'use strict';

angular.module('admin').controller('Admin.ApplicationConfigurationController',
    ['$scope', 'Admin.ApplicationSettingsService', 'Object.LookupService','MessageService', function ($scope, ApplicationSettingsService, ObjectLookupService,MessageService) {

        $scope.isTimezoneValid = true;

        ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
            $scope.defaultTimezone = response.data[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE];
            $scope.organizationName = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_NAME];
            $scope.organizationAddress1 = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS1];
            $scope.organizationAddress2 = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS2];
            $scope.organizationCity = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_CITY];
            $scope.organizationState = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_STATE];
            $scope.organizationZip = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ZIP];
            $scope.organizationPhone = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_PHONE];
            $scope.organizationFax = response.data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_FAX];
            $scope.dashboardBannerEnabled = response.data[ApplicationSettingsService.PROPERTIES.DASHBOARD_BANNER];
            $scope.configDataModel = response.data;
        });

        ObjectLookupService.getTimeZones().then(function (timeZones) {
            $scope.timeZones = timeZones;
        });

        $scope.validateTimezone = function () {
            try {
                Intl.DateTimeFormat(undefined, {timeZone: $scope.defaultTimezone});
                $scope.isTimezoneValid = true;
            } catch (ex) {
                $scope.isTimezoneValid = false;
            }
        };

        $scope.isSomethingChanged = function(data) {
            if (data[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE] !== $scope.defaultTimezone) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_NAME] !== $scope.organizationName) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS1] !== $scope.organizationAddress1) {
                $scope.mapFieldValuesToConfigDataModel();
                return true
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS2] !== $scope.organizationAddress2) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_CITY] !== $scope.organizationCity) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_STATE] !== $scope.organizationState) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ZIP] !== $scope.organizationZip) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_PHONE] !== $scope.organizationPhone) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.ORGANIZATION_FAX] !== $scope.organizationFax) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else if (data[ApplicationSettingsService.PROPERTIES.DASHBOARD_BANNER] !== $scope.dashboardBannerEnabled) {
                $scope.mapFieldValuesToConfigDataModel();
                return true;
            } else {
                return false;
            }
        };

        $scope.mapFieldValuesToConfigDataModel = function() {
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.DEFAULT_TIMEZONE] = $scope.defaultTimezone;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_NAME] = $scope.organizationName;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS1] = $scope.organizationAddress1;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ADDRESS2] = $scope.organizationAddress2;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_CITY] = $scope.organizationCity;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_STATE] = $scope.organizationState;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_ZIP] = $scope.organizationZip;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_PHONE] = $scope.organizationPhone;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.ORGANIZATION_FAX] = $scope.organizationFax;
            $scope.configDataModel[ApplicationSettingsService.PROPERTIES.DASHBOARD_BANNER] = $scope.dashboardBannerEnabled;
        };

        $scope.applyChanges = function () {
            if ($scope.isSomethingChanged($scope.configDataModel)) {
                ApplicationSettingsService.saveApplicationPropertyConfig($scope.configDataModel);
                MessageService.succsessAction();
            }
        };

    }]);