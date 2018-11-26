'use strict';

angular.module('admin').controller('Admin.FileUploaderController',
    [ '$scope', '$q', '$modal', '$translate', 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService', function($scope, $q, $modal, $translate, Util, ApplicationSettingsService, DialogService, messageService) {
        var saved = {};
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT).then(function(response) {
            $scope.uploadFileSizeLimit = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT], 52428800);
            saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;
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
            if (saved.uploadFileSizeLimit != $scope.uploadFileSizeLimit) {
                ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.IDLE_LIMIT, $scope.uploadFileSizeLimit);
                saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;

                //change for AFDP-6803
                bootbox.alert({
                    message: $translate.instant("admin.application.documentManagement.fileUploader.inform"),
                    buttons: {
                        ok:{
                            label: $translate.instant("admin.application.documentManagement.fileUploader.dialog.OKBtn")
                        },
                    },
                    callback: function(result){
                        messageService.succsessAction();
                    }
                });
            }
        }
    } ]);
