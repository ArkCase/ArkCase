'use strict';

angular.module('admin').controller('Admin.LocaleConfigController', ['$scope', '$q'
    , 'UtilService', 'Admin.LabelsConfigService', 'Config.LocaleService'
    , function ($scope, $q
        , Util, LabelsConfigService, LocaleService
    ) {

        var settingsPromise = LabelsConfigService.retrieveSettings().$promise;
        var localSettingsPromise = LocaleService.getSettings();

        $q.all([settingsPromise, localSettingsPromise]).then(function (result) {
            $scope.settings = result[0];
            var localeCode = Util.goodMapValue($scope.settings, "localeCode", LocaleService.DEFAULT_CODE);

            $scope.localSettings = result[1];
            var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);

            $scope.languagesDropdownOptions = locales;
            $scope.defaultLocale = _.find(locales, {code: localeCode});
        });


        $scope.changeDefaultLocale = function ($event, newLocale) {
            $event.preventDefault();
            var locales = Util.goodMapValue($scope.localSettings, "locales", LocaleService.DEFAULT_LOCALES);
            $scope.defaultLocale = _.find(locales, {code: newLocale});
            $scope.settings.localeCode = newLocale;
            LabelsConfigService.updateSettings(
                $scope.settings
            )
        };

    }
]);
