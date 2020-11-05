'use strict';

angular.module('admin').controller('Admin.LoginConfigController',
        [ '$scope', '$q', '$modal', '$translate', 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService', function($scope, $q, $modal, $translate, Util, ApplicationSettingsService, DialogService, messageService) {
            var saved = {};

            $scope.configDataModel = {};

            ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
                $scope.idleLimit = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT], 600000);
                saved.idleLimit = $scope.idleLimit;
                $scope.configDataModel = response.data;
            });
            //ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_PULL).then(function (response) {
            //    $scope.idlePull = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_PULL], 5000);
            //    saved.idlePull = $scope.idlePull;
            //});
            //ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.IDLE_CONFIRM).then(function (response) {
            //    $scope.idleConfirm = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.IDLE_CONFIRM], 15000);
            //    saved.idleConfirm = $scope.idleConfirm;
            //});

            $scope.applyChanges = function() {
                if (saved.idleLimit != $scope.idleLimit) {
                    $scope.configDataModel[ApplicationSettingsService.PROPERTIES.IDLE_LIMIT] = $scope.idleLimit;
                    ApplicationSettingsService.saveApplicationPropertyConfig($scope.configDataModel);
                    saved.idleLimit = $scope.idleLimit;

                    //change for AFDP-6803
                    bootbox.alert({
                        message: $translate.instant("admin.application.login.config.inform"),
                        buttons: {
                            ok:{
                                label: $translate.instant("admin.application.login.config.dialog.OKBtn")
                            },
                        },
                        callback: function(result){
                            messageService.succsessAction();
                        }
                    });
                }
            }
        } ]);
