'use strict';
angular.module('admin').controller('Admin.FileUploaderController',
    ['$scope', '$q', '$modal', '$translate', 'UtilService', 'Admin.ApplicationSettingsService', 'Dialog.BootboxService', 'MessageService', function ($scope, $q, $modal, $translate, Util, ApplicationSettingsService, DialogService, messageService) {

        var saved = {};

        ApplicationSettingsService.getSettings().then(function (response) {
            var singleChunkFileSizeLimitInBytes = response.data[ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT];
            $scope.singleChunkFileSizeLimit = Util.bytes(singleChunkFileSizeLimitInBytes);
            saved.singleChunkFileSizeLimit = $scope.singleChunkFileSizeLimit;

            var uploadFileSizeLimitInBytes = response.data[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT];
            $scope.uploadFileSizeLimit = Util.bytes(uploadFileSizeLimitInBytes);
            saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;

            saved.enableFileChunkUpload = response.data[ApplicationSettingsService.PROPERTIES.ENABLE_FILE_CHUNK];
            $scope.enableFileChunkUpload = saved.enableFileChunkUpload;
        });

        $scope.applyChanges = function () {
            if (saved.uploadFileSizeLimit !== $scope.uploadFileSizeLimit || saved.singleChunkFileSizeLimit !== $scope.singleChunkFileSizeLimit || (!Util.isEmpty(saved.enableFileChunkUpload) && saved.enableFileChunkUpload !== $scope.enableFileChunkUpload)) {
                var uploadManagerData = {};
                uploadManagerData[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT] = Util.bytes($scope.uploadFileSizeLimit);
                uploadManagerData[ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT] = Util.bytes($scope.singleChunkFileSizeLimit);
                uploadManagerData[ApplicationSettingsService.PROPERTIES.ENABLE_FILE_CHUNK] = $scope.enableFileChunkUpload;

                ApplicationSettingsService.setSettings(uploadManagerData).then(
                    function(response){
                        saved.uploadFileSizeLimit = response.data.uploadFileSizeLimit;
                        saved.singleChunkFileSizeLimit = response.data.singleChunkFileSizeLimit;
                        saved.enableFileChunkUpload = response.data.enableFileChunkUpload;
                    }
                );

                bootbox.alert({
                    message: $translate.instant("admin.documentManagement.fileUploader.inform"),
                    buttons: {
                        ok: {
                            label: $translate.instant("admin.documentManagement.fileUploader.dialog.OKBtn")
                        },
                    },
                    callback: function (result) {
                        messageService.succsessAction();
                    }
                });
            }
        }
    }]);
