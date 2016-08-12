'use strict';

angular.module('admin').controller('Admin.AuditHistoryController', ['$scope', '$q', '$modal', '$translate'
    , 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService'
    , function ($scope, $q, $modal, $translate
        , Util, ApplicationSettingsService, DialogService
    ) {
        var saved = {};
        // Replace with loading setting from backend.
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_LIMIT).then(function (response) {
            $scope.historyDays = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT], 30);
            saved.historyDays = $scope.historyDays;
        });

        $scope.applyChanges = function () {
            if (saved.historyDays != $scope.historyDays) {
                // Save setting on backend.

                saved.historyDays = $scope.historyDays;

                // Replace with "successfully saved" message.
                DialogService.alert($translate.instant("admin.application.login.config.inform"));
            }
        }
    }
]);
