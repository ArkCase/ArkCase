'use strict';

angular.module('admin').controller('Admin.AuditHistoryController', ['$scope', '$q', '$modal', '$translate'
    , 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService'
    , function ($scope, $q, $modal, $translate
        , Util, ApplicationSettingsService, DialogService
    ) {
        /*
        var saved = {};
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_LIMIT).then(function (response) {
            $scope.idleLimit = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT], 600000);
            saved.idleLimit = $scope.idleLimit;
        });

        $scope.applyChanges = function () {
            if (saved.idleLimit != $scope.idleLimit) {
                ApplicationSettingsService.setProperty(
                        ApplicationSettingsService.PROPERTIES.IDLE_LIMIT,
                        $scope.idleLimit
                );
                saved.idleLimit = $scope.idleLimit;

                DialogService.alert($translate.instant("admin.application.login.config.inform"));
            }
        }
        */
    }
]);
