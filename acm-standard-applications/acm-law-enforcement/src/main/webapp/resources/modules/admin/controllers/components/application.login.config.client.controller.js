'use strict';

angular.module('admin').controller('Admin.LoginConfigController', ['$scope', '$q', '$modal', '$translate'
    , 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService'
    , function ($scope, $q, $modal, $translate
        , Util, ApplicationSettingsService, DialogService, messageService
    ) {
        var saved = {};
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_LIMIT).then(function (response) {
            $scope.idleLimit = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT], 600000);
            saved.idleLimit = $scope.idleLimit;
        });
        //ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_PULL).then(function (response) {
        //    $scope.idlePull = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_PULL], 5000);
        //    saved.idlePull = $scope.idlePull;
        //});
        //ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_CONFIRM).then(function (response) {
        //    $scope.idleConfirm = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_CONFIRM], 15000);
        //    saved.idleConfirm = $scope.idleConfirm;
        //});

        $scope.applyChanges = function () {
            if (saved.idleLimit != $scope.idleLimit) {
                ApplicationSettingsService.setProperty(
                        ApplicationSettingsService.PROPERTIES.IDLE_LIMIT,
                        $scope.idleLimit
                );
                saved.idleLimit = $scope.idleLimit;

                DialogService.alert($translate.instant("admin.application.login.config.inform")).then(function () {
                    //success
                    messageService.succsessAction();
                });
            }
        }
    }
]);
