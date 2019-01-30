'use strict';

angular.module('core').controller(
    'UploadManagerModalController',
    ['$scope', '$rootScope', '$modalInstance', '$http', '$translate', '$timeout', 'Upload', 'MessageService', 'ObjectService', 'UtilService', 'params', function ($scope, $rootScope, $modalInstance, $http, $translate, $timeout, Upload, MessageService, ObjectService, Util, params) {
        $scope.hashMapOfAllFiles = {};

        var uploadFileSizeLimit = params.uploadFileSizeLimit;
        $scope.$bus.subscribe('upload-file-size-limit-changed', function(newUploadFileSizeLimit){
            uploadFileSizeLimit = newUploadFileSizeLimit;
        });

        var singleChunkFileSizeLimit = params.singleChunkFileSizeLimit;
        $scope.$bus.subscribe('singe-chunk-file-size-limit-changed', function(newSingleChunkFileSizeLimit){
            singleChunkFileSizeLimit = newSingleChunkFileSizeLimit;
        });

        $scope.onClickHideModal = function () {
            $scope.$bus.publish('upload-manager-hide');
        };

        $scope.onClickHideTheUploadedFile = function (uuid) {
            delete $scope.hashMapOfAllFiles[uuid];
        };

        function notifySnackbarUploadFinished(){
            var uploadedSuccessfully = true;
            $timeout(function() {
                $scope.$bus.publish('notify-snackbar-all-files-were-uploaded', uploadedSuccessfully);
            }, 5000);
        }

        function getPartFile(uuid) {
            $scope.hashMapOfAllFiles[uuid].part++;
            $scope.hashMapOfAllFiles[uuid].startByte = $scope.hashMapOfAllFiles[uuid].endByte;
            $scope.hashMapOfAllFiles[uuid].endByte = $scope.hashMapOfAllFiles[uuid].endByte + $scope.hashMapOfAllFiles[uuid].partBytes;

            if ($scope.hashMapOfAllFiles[uuid].endByte >= $scope.hashMapOfAllFiles[uuid].file.size) {
                $scope.hashMapOfAllFiles[uuid].endByte = $scope.hashMapOfAllFiles[uuid].file.size;
            }

            var file = $scope.hashMapOfAllFiles[uuid].file;

            if ($scope.hashMapOfAllFiles[uuid].file.size > $scope.hashMapOfAllFiles[uuid].partBytes) {
                file = $scope.hashMapOfAllFiles[uuid].file.slice($scope.hashMapOfAllFiles[uuid].startByte, $scope.hashMapOfAllFiles[uuid].endByte);
                file.name = $scope.hashMapOfAllFiles[uuid].file.name + "_" + $scope.hashMapOfAllFiles[uuid].part + "_" + Date.now();
            }

            return file;
        }

        function startUpload() {
            if (!Util.isEmpty($scope.hashMapOfAllFiles))
            {
                var foundInProgress = false;
                var foundReady = null;

                for (var key in $scope.hashMapOfAllFiles) {
                    var fileDetails = $scope.hashMapOfAllFiles[key];
                    if (fileDetails.status == ObjectService.UploadFileStatus.IN_PROGRESS){
                        foundInProgress = true;
                        break;
                    }
                    if ((Util.isEmpty(foundReady) || foundReady.date > fileDetails.date) && fileDetails.status == ObjectService.UploadFileStatus.READY){
                        foundReady = fileDetails;
                    }
                }

                if (!foundInProgress && !Util.isEmpty(foundReady))
                {
                    uploadPart(foundReady.uuid);
                }
            }
        };

        function uploadPart(uuid) {
            var file = getPartFile(uuid);
            uploadChunks(file, uuid);
        };

        function getParams(uuid){
            var params = {};
            params.uuid = uuid;
            params.isFileChunk = true;

            var currentFileDetails = $scope.hashMapOfAllFiles[uuid];
            if (!Util.isEmpty(currentFileDetails)) {
                if (currentFileDetails.file.size <= currentFileDetails.partBytes) {
                    params.fileLang = currentFileDetails.fileLang;
                    params.parentObjectType = currentFileDetails.objectType;
                    params.parentObjectId =  currentFileDetails.objectId;
                    params.folderId =  currentFileDetails.folderId;
                    params.fileType =  currentFileDetails.fileType;
                    params.isFileChunk = false;
                }
            }
            return params;
        }

        function uploadChunks(file, uuid) {
            $scope.hashMapOfAllFiles[uuid].status = ObjectService.UploadFileStatus.IN_PROGRESS;
            Upload.upload({
                url: 'api/latest/service/ecm/uploadChunks',
                params: getParams(uuid),
                file: file
            }).then(function (result) {
                var _uuid = !Util.isEmpty(result) && !Util.isEmpty(result.data) && !Util.isEmpty(result.data.uuid) ? result.data.uuid : uuid;

                $scope.hashMapOfAllFiles[_uuid].parts.push(result.data.fileName);
                if ($scope.hashMapOfAllFiles[_uuid].endByte < $scope.hashMapOfAllFiles[_uuid].file.size) {
                    $scope.hashMapOfAllFiles[_uuid].progress = $scope.hashMapOfAllFiles[_uuid].progress + $scope.hashMapOfAllFiles[_uuid].partProgress;
                    uploadPart(_uuid);
                } else {
                    if ($scope.hashMapOfAllFiles[_uuid].file.size > $scope.hashMapOfAllFiles[_uuid].partBytes) {
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
                        $http({
                            method: "POST",
                            url: 'api/latest/service/ecm/mergeChunks',
                            data: data
                        }).then(function () {
                        });
                    }
                }
                //this stays out until we have requirement close/cancel modal button.
                /*$scope.onClickClose = function () {
                    $modalInstance.dismiss('close');
                };*/

            }, function (error) {
                $scope.hashMapOfAllFiles[uuid].status = ObjectService.UploadFileStatus.FAILED;
                MessageService.error($translate.instant('common.directive.docTree.progressBar.failed') + ": " + error);
            }, function (progress) {
                $scope.hashMapOfAllFiles[uuid].partProgress = progress.loaded;
                var currentProgress = $scope.hashMapOfAllFiles[uuid].progress + $scope.hashMapOfAllFiles[uuid].partProgress;
                // The calculation for total is introduced because ng-file-upload makes discrepancy in the multipart file size.
                // When parameters are send to the request for upload, total size gets bigger then the actual file size
                var total = 0;
                if($scope.hashMapOfAllFiles[uuid].file.size>uploadFileSizeLimit) {
                    total = currentProgress < $scope.hashMapOfAllFiles[uuid].file.size ? $scope.hashMapOfAllFiles[uuid].file.size : currentProgress;
                    $scope.hashMapOfAllFiles[uuid].currentProgress = parseInt(50.0 * (currentProgress / total));
                } else {
                    total = progress.total;
                    $scope.hashMapOfAllFiles[uuid].currentProgress = parseInt(100.0 * (currentProgress / total));
                    if($scope.hashMapOfAllFiles[uuid].currentProgress == 100){
                        var status = ObjectService.UploadFileStatus.FINISHED;
                        $scope.hashMapOfAllFiles[uuid].status = status;
                        startUpload();
                        notifySnackbarUploadFinished();
                    }
                }
            });

        }

        $rootScope.$bus.subscribe('notify-modal-progressbar-current-progress-updated', function(message) {
            $scope.$apply(function(){
                var status = ObjectService.UploadFileStatus.IN_PROGRESS;
                message.status = status;

                updateCurrentProgress(message);
            });
        });

        function updateCurrentProgress(message)
        {
            $scope.hashMapOfAllFiles[message.uuid].status = message.status;
            $scope.hashMapOfAllFiles[message.uuid].currentProgress = message.currentProgress;
        }

        $scope.$bus.subscribe('start-upload-chunk-file', function (fileDetails) {
            for (var i = 0; i < fileDetails.files.length; i++) {
                var currentFileDetails = {};
                currentFileDetails.parts = [];
                currentFileDetails.part = 0;
                currentFileDetails.partBytes = uploadFileSizeLimit;
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

                var uuid = Util.isEmpty(fileDetails.uuid) ? Date.now().toString() + i.toString() : fileDetails.uuid;
                currentFileDetails.uuid = uuid;

                $scope.hashMapOfAllFiles[uuid] = currentFileDetails;
            }
            startUpload();
        });

        $rootScope.$bus.subscribe('notify-modal-progressbar-current-progress-finished', function(message) {
                var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
                message.status = status;

                updateCurrentProgress(message);
                startUpload();
                notifySnackbarUploadFinished();
        });

    }
    ]);