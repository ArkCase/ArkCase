'use strict';

angular.module('admin').controller('Admin.FileUploaderController',
    [ '$scope', '$q', '$modal', '$translate', 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService', function($scope, $q, $modal, $translate, Util, ApplicationSettingsService, DialogService, messageService) {
        var saved = {};
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT).then(function(response) {
            var uploadFileSizeLimitInBytes = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT], 52428800);
            $scope.uploadFileSizeLimit = Util.convertBytesToSize(uploadFileSizeLimitInBytes, 'MB');
            saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;
        });

        $scope.applyChanges = function() {
            if (saved.uploadFileSizeLimit != $scope.uploadFileSizeLimit) {
                ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT, $scope.uploadFileSizeLimit);
                saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;

                //change for AFDP-6803
                bootbox.alert({
                    message: $translate.instant("admin.documentManagement.fileUploader.inform"),
                    buttons: {
                        ok:{
                            label: $translate.instant("admin.documentManagement.fileUploader.dialog.OKBtn")
                        },
                    },
                    callback: function(result){
                        messageService.succsessAction();
                    }
                });
            }
        }
    } ]);
