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

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT).then(function(response) {
            var defaultChunkLimit = 10485760; //10mb
            var singleChunkFileSizeLimitInBytes = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT], defaultChunkLimit);
            $scope.singleChunkFileSizeLimit = Util.bytes(singleChunkFileSizeLimitInBytes);
            saved.singleChunkFileSizeLimit = $scope.singleChunkFileSizeLimit;
        });



        $scope.applyChanges = function() {
            if (saved.uploadFileSizeLimit != $scope.uploadFileSizeLimit || saved.singleChunkFileSizeLimit != $scope.singleChunkFileSizeLimit) {
                $scope.uploadFileSizeLimit = Util.bytes($scope.uploadFileSizeLimit);
                ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT, $scope.uploadFileSizeLimit);
                saved.uploadFileSizeLimit = $scope.uploadFileSizeLimit;

                $scope.$bus.publish('upload-file-size-limit-changed', {
                    newUploadFileSizeLimit: Util.bytes($scope.uploadFileSizeLimit)
                });
                $scope.uploadFileSizeLimit = Util.bytes($scope.uploadFileSizeLimit);


                $scope.singleChunkFileSizeLimit = Util.bytes($scope.singleChunkFileSizeLimit);
                ApplicationSettingsService.setProperty(ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT, $scope.singleChunkFileSizeLimit);
                saved.singleChunkFileSizeLimit = $scope.singleChunkFileSizeLimit;

                //publish updated single chunk size limit in bytes
                $scope.$bus.publish('singe-chunk-file-size-limit-changed', {
                    newSingleChunkFileSizeLimit: Util.bytes($scope.singleChunkFileSizeLimit)
                });
                $scope.singleChunkFileSizeLimit = Util.bytes($scope.singleChunkFileSizeLimit);



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
