'use strict';
angular.module('admin').controller('Admin.FileUploaderController',
    [ '$scope', '$q', '$modal', '$translate', 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService', function($scope, $q, $modal, $translate, Util, ApplicationSettingsService, DialogService, messageService) {

        var saved = {};
        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT).then(function(response) {
            var defaultLimit = 52428800; //50mb
            var uploadFileSizeLimitInBytes = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT], defaultLimit);
            $scope.uploadFileSizeLimit = Util.bytes(uploadFileSizeLimitInBytes);
            saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;
        });

        $scope.applyChanges = function() {
            if (saved.uploadFileSizeLimit != $scope.uploadFileSizeLimit) {
                $scope.uploadFileSizeLimit = Util.bytes($scope.uploadFileSizeLimit);
                ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT, $scope.uploadFileSizeLimit);
                saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;
                //TODO: event publish from here to the doc-tree controller, so that it knows that the limit has been changed
                $scope.$bus.publish('upload-file-size-limit-changed', {
                    uploadFileSizeLimit: $scope.uploadFileSizeLimit
                });

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
