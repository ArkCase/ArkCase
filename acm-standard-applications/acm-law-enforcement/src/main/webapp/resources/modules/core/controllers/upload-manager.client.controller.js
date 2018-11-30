'use strict';

angular.module('core').controller(
    'UploadManagerController',
    [ '$scope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'Upload', 'MessageService', 'UtilService', 'Admin.ApplicationSettingsService',
        function($scope, $q, $state, $translate, $modal, $http, $timeout, $document, Upload, MessageService, Util, ApplicationSettingsService) {

        $scope.hideUploadSnackbar = true;

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT).then(function(response) {
            var defaultLimit = 52428800; //50mb
            var uploadFileSizeLimitInBytes = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.UPLOAD_FILE_SIZE_LIMIT], defaultLimit);
            $scope.uploadFileSizeLimit = uploadFileSizeLimitInBytes;
        });

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT).then(function(response) {
            var defaultChunkLimit = 10485760; //10mb
            var singleChunkFileSizeLimitInBytes = Util.goodValue(response.data[ApplicationSettingsService.PROPERTIES.SINGLE_CHUNK_FILE_SIZE_LIMIT], defaultChunkLimit);
            $scope.singleChunkFileSizeLimit = singleChunkFileSizeLimitInBytes;
        });


        $scope.$bus.subscribe('upload-file-size-limit-changed', function(newUploadFileSizeLimit){
            $scope.uploadFileSizeLimit = newUploadFileSizeLimit;
        });

        $scope.$bus.subscribe('singe-chunk-file-size-limit-changed', function(newSingleChunkFileSizeLimit){
            $scope.singleChunkFileSizeLimit = newSingleChunkFileSizeLimit;
        });

        var modalInstance = null;

        $scope.$bus.subscribe('upload-chunk-file', function(fileDetails){

            if (modalInstance === null) {
                modalInstance = $modal.open({
                    templateUrl: "modules/core/views/upload-progress-bar-modal.html",
                    controller: 'UploadManagerModalController',
                    size: 'lg',
                    backdrop: 'static',
                    keyboard: false,
                    backdropClass: "uploadManagerComponent",
                    windowClass: "uploadManagerComponent",
                    resolve: {
                        params:{
                            uploadFileSizeLimit: $scope.uploadFileSizeLimit,
                            singleChunkFileSizeLimit: $scope.singleChunkFileSizeLimit
                        }
                    }
                });

                modalInstance.opened.then(function(){
                    startUploadChunkFile(fileDetails);
                });

                modalInstance.hide = function () {
                    $('.uploadManagerComponent').hide();
                    $scope.hideUploadSnackbar = false;
                };

                modalInstance.show = function () {
                    $('.uploadManagerComponent').show();
                    $scope.hideUploadSnackbar = true;
                };

                $scope.$bus.subscribe('upload-manager-show', modalInstance.show);
                $scope.$bus.subscribe('upload-manager-hide', modalInstance.hide);
            } else {
                modalInstance.show();
                startUploadChunkFile(fileDetails);
            }

        });

        $scope.onClickViewDetailsModal = function () {
            $scope.$bus.publish('upload-manager-show');
        };

        function startUploadChunkFile(fileDetails) {
            if (!Util.isEmpty(fileDetails)) {
                $scope.$bus.publish('start-upload-chunk-file', fileDetails);
            }
        }
} ]);