'use strict';

angular.module('core').controller(
        'UploadManagerController',
        [ '$scope', '$rootScope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'Upload', 'MessageService', 'UtilService', 'ObjectService', 'Admin.FileUploaderConfigurationService',
                function($scope, $rootScope, $q, $state, $translate, $modal, $http, $timeout, $document, Upload, MessageService, Util, ObjectService, FileUploaderConfigurationService) {

                    $scope.hideUploadSnackbar = true;
                    $scope.hideUploadSnackbarIcon = true;

                    FileUploaderConfigurationService.getFileUploaderConfiguration().then(function (response) {
                        $scope.ecmFileProperties = response.data;
                        var singleChunkFileSizeLimitInBytes = $scope.ecmFileProperties['fileUploader.singleChunkFileSizeLimit'];
                        $scope.singleChunkFileSizeLimit = singleChunkFileSizeLimitInBytes;

                        var uploadFileSizeLimitInBytes = $scope.ecmFileProperties['fileUploader.uploadFileSizeLimit'];
                        $scope.uploadFileSizeLimit = uploadFileSizeLimitInBytes;

                        var enableFileChunkUpload = $scope.ecmFileProperties['fileUploader.enableFileChunkUpload'];
                        $scope.enableFileChunkUpload = enableFileChunkUpload;
                    });

                    var modalInstance = null;

                    $scope.$bus.subscribe('upload-chunk-file', function(fileDetails) {

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
                                    params: {
                                        uploadFileSizeLimit: $scope.uploadFileSizeLimit,
                                        singleChunkFileSizeLimit: $scope.singleChunkFileSizeLimit,
                                        enableFileChunkUpload: $scope.enableFileChunkUpload
                                    }
                                }
                            });

                            modalInstance.opened.then(function() {
                                startUploadChunkFile(fileDetails);
                            });

                            modalInstance.hide = function() {
                                $('.uploadManagerComponent').hide();
                                $scope.hideUploadSnackbar = false;
                            };

                            modalInstance.show = function() {
                                $('.uploadManagerComponent').show();
                                $scope.hideUploadSnackbar = true;
                            };

                            $scope.$bus.subscribe('upload-manager-show', modalInstance.show);
                            $scope.$bus.subscribe('upload-manager-hide', function() {
                                modalInstance.hide();
                            });
                        } else {
                            modalInstance.show();
                            startUploadChunkFile(fileDetails);
                        }

                    });

                    $scope.onClickViewDetailsModal = function() {
                        $scope.$bus.publish('upload-manager-show');
                    };

                    function startUploadChunkFile(fileDetails) {
                        if (!Util.isEmpty(fileDetails)) {
                            $scope.$bus.publish('start-upload-chunk-file', fileDetails);
                        }
                    }

                    $scope.$bus.subscribe('progressbar-current-progress-updated', function(message) {
                        var status = ObjectService.UploadFileStatus.IN_PROGRESS;
                        message.status = status;

                        updateCurrentProgress(message);
                    });

                    $scope.$bus.subscribe('progressbar-current-progress-finished', function(message) {
                        var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
                        message.status = status;

                        updateCurrentProgress(message);
                    });

                    function updateCurrentProgress(message) {
                        if (modalInstance === null) {
                            var fileDetails = {};
                            fileDetails.files = [];
                            fileDetails.files.push({
                                name: message.fileName,
                                currentProgress: message.currentProgress
                            });
                            fileDetails.originObjectId = message.objectId;
                            fileDetails.originObjectType = message.objectType;
                            fileDetails.parentObjectNumber = message.objectNumber;
                            fileDetails.uuid = message.uuid;
                            fileDetails.status = message.status;
                            fileDetails.success = message.success;
                            $scope.$bus.publish('upload-chunk-file', fileDetails);
                        } else {
                            if (message.status === ObjectService.UploadFileStatus.IN_PROGRESS) {
                                $scope.$bus.publish('notify-modal-progressbar-current-progress-updated', message);
                            } else {
                                $scope.$bus.publish('notify-modal-progressbar-current-progress-finished', message);
                            }
                        }
                    }

                    $scope.$bus.subscribe('notify-snackbar-upload-status', function(iconDisplayStatus) {
                        $scope.hideUploadSnackbarIcon = iconDisplayStatus.hide;
                    })
                } ]);