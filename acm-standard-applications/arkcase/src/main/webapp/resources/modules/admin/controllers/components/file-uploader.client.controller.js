'use strict';
angular.module('admin').controller('Admin.FileUploaderController',
    ['$scope', '$q', '$modal', '$translate', 'UtilService', 'Dialog.BootboxService', 'MessageService', 'Admin.FileUploaderConfigurationService', function ($scope, $q, $modal, $translate, Util, DialogService, messageService, FileUploaderConfigurationService) {

        var fileUploaderConfigDataModel = {};

        //issues were spotted on html level when changing value of a property, child $scope was being created if we have not initiated the $scope.model object
        $scope.model = {};

        FileUploaderConfigurationService.getFileUploaderConfiguration().then(function (response) {
            $scope.ecmFileProperties = response.data;
            var singleChunkFileSizeLimitInBytes = $scope.ecmFileProperties['fileUploader.singleChunkFileSizeLimit'];
            $scope.model.singleChunkFileSizeLimit = Util.bytes(singleChunkFileSizeLimitInBytes);
            fileUploaderConfigDataModel.singleChunkFileSizeLimit = $scope.model.singleChunkFileSizeLimit;


            var uploadFileSizeLimitInBytes = $scope.ecmFileProperties['fileUploader.uploadFileSizeLimit'];
            $scope.model.uploadFileSizeLimit = Util.bytes(uploadFileSizeLimitInBytes);
            fileUploaderConfigDataModel.uploadFileSizeLimit = $scope.model.uploadFileSizeLimit;

            fileUploaderConfigDataModel.enableFileChunkUpload = $scope.ecmFileProperties['fileUploader.enableFileChunkUpload'];
            $scope.enableFileChunkUpload = fileUploaderConfigDataModel.enableFileChunkUpload;
        });

        $scope.applyChanges = function () {
            if (fileUploaderConfigDataModel.uploadFileSizeLimit !== $scope.model.uploadFileSizeLimit || fileUploaderConfigDataModel.singleChunkFileSizeLimit !== $scope.model.singleChunkFileSizeLimit || (!Util.isEmpty(fileUploaderConfigDataModel.enableFileChunkUpload) && fileUploaderConfigDataModel.enableFileChunkUpload !== $scope.enableFileChunkUpload)) {

                var updatedConfigurationData = {
                    "fileUploader.singleChunkFileSizeLimit": Util.bytes($scope.model.singleChunkFileSizeLimit),
                    "fileUploader.uploadFileSizeLimit": Util.bytes($scope.model.uploadFileSizeLimit),
                    "fileUploader.enableFileChunkUpload": $scope.enableFileChunkUpload
                };

                FileUploaderConfigurationService.saveFileUploaderConfiguration(updatedConfigurationData);

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
