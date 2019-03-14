'use strict';

angular.module('core').controller('UploadManagerModalController',
    [ '$scope', '$rootScope', '$modalInstance', '$http', '$translate', '$timeout', '$log', 'Upload', 'MessageService', 'ObjectService', 'UtilService', 'params', 'Core.UploadManagerModalService', function($scope, $rootScope, $modalInstance, $http, $translate, $timeout, $log, Upload, MessageService, ObjectService, Util, params, UploadManagerModalService) {
        $scope.hashMapOfAllFiles = {};

        $scope.uploadFileSizeLimit = params.uploadFileSizeLimit;
        $scope.singleChunkFileSizeLimit = params.singleChunkFileSizeLimit;
        $scope.enableFileChunkUpload = params.enableFileChunkUpload;

        $scope.onClickHideModal = function() {
            $scope.$bus.publish('upload-manager-hide');
        };

        $scope.onClickHideTheUploadedFile = function(uuid) {
            delete $scope.hashMapOfAllFiles[uuid];
        };

        $scope.onClickRetryUploadFile = function (uuid) {
            var fileReadyToUploadAgain = $scope.hashMapOfAllFiles[uuid];
            uploadPart(fileReadyToUploadAgain.uuid);
        };

        $scope.onClickCloseModal = function() {
            $modalInstance.close();
        };

        function notifySnackbarUploadFinished() {
            var uploadIcon = {
                hide: true
            };
            $scope.activeUploadFileProcess = false;
            $timeout(function() {
                $scope.$bus.publish('notify-snackbar-upload-status', uploadIcon);
            }, 5000);
        }

        function getPartFile(uuid) {
            $scope.hashMapOfAllFiles[uuid].part++;
            $scope.hashMapOfAllFiles[uuid].startByte = $scope.hashMapOfAllFiles[uuid].endByte;
            $scope.hashMapOfAllFiles[uuid].endByte = $scope.hashMapOfAllFiles[uuid].endByte + $scope.hashMapOfAllFiles[uuid].partBytes;

            if ($scope.enableFileChunkUpload && $scope.hashMapOfAllFiles[uuid].endByte >= $scope.hashMapOfAllFiles[uuid].file.size) {
                $scope.hashMapOfAllFiles[uuid].endByte = $scope.hashMapOfAllFiles[uuid].file.size;
            }

            var file = $scope.hashMapOfAllFiles[uuid].file;

            if ($scope.enableFileChunkUpload && $scope.hashMapOfAllFiles[uuid].file.size > $scope.hashMapOfAllFiles[uuid].partBytes) {
                file = $scope.hashMapOfAllFiles[uuid].file.slice($scope.hashMapOfAllFiles[uuid].startByte, $scope.hashMapOfAllFiles[uuid].endByte);
                file.name = $scope.hashMapOfAllFiles[uuid].file.name + "_" + $scope.hashMapOfAllFiles[uuid].part + "_" + Date.now();
            }

            return file;
        }

        function startUpload() {
            if (!Util.isEmpty($scope.hashMapOfAllFiles)) {
                var foundInProgress = false;
                var fileReadyToUpload = {};

                for ( var key in $scope.hashMapOfAllFiles) {
                    var fileDetails = $scope.hashMapOfAllFiles[key];
                    if (fileDetails.status == ObjectService.UploadFileStatus.IN_PROGRESS) {
                        foundInProgress = true;
                        break;
                    }
                    if ((Util.isObjectEmpty(fileReadyToUpload) || fileReadyToUpload.date > fileDetails.date) && fileDetails.status == ObjectService.UploadFileStatus.READY || fileDetails.status == ObjectService.UploadFileStatus.FAILED) {
                        fileReadyToUpload = fileDetails;
                    }
                }

                if (!foundInProgress && !Util.isObjectEmpty(fileReadyToUpload)) {
                    uploadPart(fileReadyToUpload.uuid);
                } else {
                    notifySnackbarUploadFinished();
                }
            }
        }


        function uploadPart(uuid) {
            var file = getPartFile(uuid);
            uploadChunks(file, uuid);
        }


        function getParams(uuid) {
            var params = {};
            params.uuid = uuid;
            params.isFileChunk = true;
            params.folderId = $scope.hashMapOfAllFiles[uuid].folderId;

            var currentFileDetails = $scope.hashMapOfAllFiles[uuid];
            if (!Util.isEmpty(currentFileDetails.file) && (!$scope.enableFileChunkUpload || currentFileDetails.file.size <= currentFileDetails.partBytes)) {
                params.fileLang = currentFileDetails.fileLang;
                params.parentObjectType = currentFileDetails.objectType;
                params.parentObjectId = currentFileDetails.objectId;
                params.folderId = currentFileDetails.folderId;
                params.fileType = currentFileDetails.fileType;
                params.isFileChunk = false;
            }
            return params;
        }


        function uploadChunks(file, uuid) {
            $scope.hashMapOfAllFiles[uuid].status = ObjectService.UploadFileStatus.IN_PROGRESS;
            $scope.activeUploadFileProcess = true;
            var uploadIcon = {
                hide: false
            };
            $scope.$bus.publish('notify-snackbar-upload-status', uploadIcon);

            Upload.upload({
                url: 'api/latest/service/ecm/uploadChunks',
                params: getParams(uuid),
                file: file
            }).then(function(result) {
                var _uuid = !Util.isEmpty(result) && !Util.isEmpty(result.data) && !Util.isEmpty(result.data.uuid) ? result.data.uuid : uuid;

                $scope.hashMapOfAllFiles[_uuid].parts.push(result.data.fileName);
                if ($scope.enableFileChunkUpload && $scope.hashMapOfAllFiles[_uuid].endByte < $scope.hashMapOfAllFiles[_uuid].file.size) {
                    $scope.hashMapOfAllFiles[_uuid].progress = $scope.hashMapOfAllFiles[_uuid].progress + $scope.hashMapOfAllFiles[_uuid].partProgress;
                    uploadPart(_uuid);
                } else if ($scope.enableFileChunkUpload && $scope.hashMapOfAllFiles[_uuid].file.size > $scope.hashMapOfAllFiles[_uuid].partBytes) {
                    var data = {};
                    data.name = $scope.hashMapOfAllFiles[_uuid].file.name;
                    data.mimeType = $scope.hashMapOfAllFiles[_uuid].file.type;
                    data.objectId = $scope.hashMapOfAllFiles[_uuid].objectId;
                    data.objectType = $scope.hashMapOfAllFiles[_uuid].objectType;
                    data.folderId = $scope.hashMapOfAllFiles[_uuid].folderId;
                    data.parts = $scope.hashMapOfAllFiles[_uuid].parts;
                    data.fileType = $scope.hashMapOfAllFiles[_uuid].fileType;
                    data.fileLang = $scope.hashMapOfAllFiles[_uuid].fileLang;
                    data.uuid = _uuid;
                    UploadManagerModalService.mergeChunks(data).then(function() {
                        $log.info('Merge chunk files successful');
                    });
                }

            }, function(error) {
                $scope.hashMapOfAllFiles[uuid].status = ObjectService.UploadFileStatus.FAILED;
                $scope.hashMapOfAllFiles[uuid].success = false;
                $scope.hashMapOfAllFiles[uuid].currentProgress = 0;
                MessageService.error($translate.instant('core.progressBar.failed') + ": " + error.status);
            }, function(progress) {
                $scope.hashMapOfAllFiles[uuid].partProgress = progress.loaded;
                var currentProgress = $scope.hashMapOfAllFiles[uuid].progress + $scope.hashMapOfAllFiles[uuid].partProgress;
                // The calculation for total is introduced because ng-file-upload makes discrepancy in the multipart file size.
                // When parameters are send to the request for upload, total size gets bigger then the actual file size
                var total = 0;
                if ($scope.enableFileChunkUpload && $scope.hashMapOfAllFiles[uuid].file.size > $scope.uploadFileSizeLimit) {
                    total = currentProgress < $scope.hashMapOfAllFiles[uuid].file.size ? $scope.hashMapOfAllFiles[uuid].file.size : currentProgress;
                    $scope.hashMapOfAllFiles[uuid].currentProgress = parseInt(50.0 * (currentProgress / total));
                } else {
                    total = progress.total;
                    $scope.hashMapOfAllFiles[uuid].currentProgress = parseInt(50.0 * (currentProgress / total));
                    if ($scope.hashMapOfAllFiles[uuid].currentProgress == 100 && $scope.hashMapOfAllFiles[uuid].status != ObjectService.UploadFileStatus.FINISHED) {
                        $scope.hashMapOfAllFiles[uuid].status = ObjectService.UploadFileStatus.FINISHED;
                        var message = {};
                        message.objectId = $scope.hashMapOfAllFiles[uuid].objectId;
                        message.objectType = $scope.hashMapOfAllFiles[uuid].objectType;
                        message.success = true;
                        message.currentProgress = $scope.hashMapOfAllFiles[uuid].currentProgress;
                        message.status = ObjectService.UploadFileStatus.FINISHED;
                        message.uuid = uuid;

                        $scope.$bus.publish('notify-modal-progressbar-current-progress-finished', message);
                    }
                }
            });
        }

        $scope.$bus.subscribe('notify-modal-progressbar-current-progress-updated', function(message) {
            $scope.$apply(function() {
                var status = ObjectService.UploadFileStatus.IN_PROGRESS;
                message.status = status;

                updateCurrentProgress(message);
            });
        });

        function updateCurrentProgress(message) {
            $scope.hashMapOfAllFiles[message.uuid].status = message.status;
            $scope.hashMapOfAllFiles[message.uuid].currentProgress = message.currentProgress;
            $scope.hashMapOfAllFiles[message.uuid].success = message.success;
        }

        $scope.$bus.subscribe('start-upload-chunk-file', function(fileDetails) {
            for (var i = 0; i < fileDetails.files.length; i++) {
                var currentFileDetails = {};
                currentFileDetails.parts = [];
                currentFileDetails.part = 0;
                currentFileDetails.partBytes = $scope.uploadFileSizeLimit;
                currentFileDetails.progress = 0;
                currentFileDetails.partProgress = 0;
                currentFileDetails.startByte = 0;
                currentFileDetails.endByte = 0;
                currentFileDetails.fileName = fileDetails.files[i].name;
                currentFileDetails.file = fileDetails.files[i];
                currentFileDetails.status = Util.isEmpty(fileDetails.status) ? ObjectService.UploadFileStatus.READY : fileDetails.status;
                currentFileDetails.objectId = fileDetails.originObjectId;
                currentFileDetails.objectType = fileDetails.originObjectType;
                currentFileDetails.folderId = fileDetails.folderId;
                currentFileDetails.fileType = fileDetails.fileType;
                currentFileDetails.currentProgress = Util.isEmpty(fileDetails.files[i].currentProgress) ? 0 : fileDetails.files[i].currentProgress;
                currentFileDetails.parentObjectNumber = fileDetails.parentObjectNumber;
                currentFileDetails.fileLang = Util.isEmpty(fileDetails.lang) ? "en" : fileDetails.lang;
                currentFileDetails.date = Date.now();
                currentFileDetails.success = true;

                var uuid = Util.isEmpty(fileDetails.uuid) ? Date.now().toString() + i.toString() : fileDetails.uuid;
                currentFileDetails.uuid = uuid;

                $scope.hashMapOfAllFiles[uuid] = currentFileDetails;

            }
            startUpload();
        });

        $scope.$bus.subscribe('notify-modal-progressbar-current-progress-finished', function(message) {
            var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
            message.status = status;

            updateCurrentProgress(message);
            startUpload();
        });

    } ]);