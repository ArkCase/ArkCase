'use strict';

angular.module('core').controller(
    'UploadManagerModalController',
    ['$scope', '$modalInstance', '$http', '$translate', 'Upload', 'MessageService', 'ObjectService', 'UtilService', 'params', function ($scope, $modalInstance, $http, $translate, Upload, MessageService, ObjectService, Util, params) {
        $scope.hashMap = {};

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
            delete $scope.hashMap[uuid];
        };

        function getPartFile(uuid) {
            $scope.hashMap[uuid].part++;
            $scope.hashMap[uuid].startByte = $scope.hashMap[uuid].endByte;
            $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].endByte + $scope.hashMap[uuid].partBytes;

            if ($scope.hashMap[uuid].endByte >= $scope.hashMap[uuid].file.size) {
                $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].file.size;
            }

            var file = $scope.hashMap[uuid].file;

            if ($scope.hashMap[uuid].file.size > $scope.hashMap[uuid].partBytes) {
                file = $scope.hashMap[uuid].file.slice($scope.hashMap[uuid].startByte, $scope.hashMap[uuid].endByte);
                file.name = $scope.hashMap[uuid].file.name + "_" + $scope.hashMap[uuid].part + "_" + Date.now();
            }

            return file;
        }

        function startUpload(uuid) {
            uploadPart(uuid);
        };

        function uploadPart(uuid) {
            var file = getPartFile(uuid);
            uploadChunks(file, uuid);
        };

        function getParams(uuid){
            var params = {};
            params.uuid = uuid;
            params.isFileChunk = true;

            var details = $scope.hashMap[uuid];
            if (!Util.isEmpty(details)) {
                if (details.file.size <= details.partBytes) {
                    params.fileLang = details.lang;
                    params.parentObjectType = details.objectType;
                    params.parentObjectId =  details.objectId;
                    params.folderId =  details.folderId;
                    params.fileType =  details.fileType;
                    params.isFileChunk = false;
                }
            }
            return params;
        }

        function uploadChunks(file, uuid) {
            $scope.hashMap[uuid].status = ObjectService.UploadFileStatus.IN_PROGRESS;
            Upload.upload({
                url: 'api/latest/service/ecm/uploadChunks',
                params: getParams(uuid),
                file: file
            }).then(function (result) {
                var _uuid = !Util.isEmpty(result) && !Util.isEmpty(result.data) && !Util.isEmpty(result.data.uuid) ? result.data.uuid : uuid;

                $scope.hashMap[_uuid].parts.push(result.data.fileName);
                if ($scope.hashMap[_uuid].endByte < $scope.hashMap[_uuid].file.size) {
                    $scope.hashMap[_uuid].progress = $scope.hashMap[_uuid].progress + $scope.hashMap[_uuid].partProgress;
                    uploadPart(_uuid);
                } else {
                    $scope.hashMap[_uuid].status = ObjectService.UploadFileStatus.FINISHED;
                    if ($scope.hashMap[_uuid].file.size > $scope.hashMap[_uuid].partBytes) {
                        var data = {};
                        data.name = $scope.hashMap[_uuid].file.name;
                        data.mimeType = $scope.hashMap[_uuid].file.type;
                        data.objectId = $scope.hashMap[_uuid].objectId;
                        data.objectType = $scope.hashMap[_uuid].objectType;
                        data.folderId = $scope.hashMap[_uuid].folderId;
                        data.parts = $scope.hashMap[_uuid].parts;
                        data.fileType = $scope.hashMap[_uuid].fileType;
                        data.uuid = _uuid;
                        $http({
                            method: "POST",
                            url: 'api/latest/service/ecm/mergeChunks',
                            data: data
                        }).then(function () {

                        });
                    }
                }
                $scope.onClickClose = function () {
                    $modalInstance.dismiss('close');
                };

            }, function (error) {
                $scope.hashMap[uuid].status = ObjectService.UploadFileStatus.FAILED;
                MessageService.error($translate.instant('common.directive.docTree.progressBar.failed') + ": " + error);
            }, function (progress) {
                $scope.hashMap[uuid].partProgress = progress.loaded;
                //here goes the upload progress to 100% without the websockets implementation,
                //instead improvement is needed to get it to 50% and then increment until 100% from the progressIndicator java implementation
                $scope.hashMap[uuid].currentProgress = parseInt(100.0 * (($scope.hashMap[uuid].progress + $scope.hashMap[uuid].partProgress) / $scope.hashMap[uuid].file.size));
            });

        }

        $scope.$bus.subscribe('start-upload-chunk-file', function (fileDetails) {
            for (var i = 0; i < fileDetails.files.length; i++) {
                var details = {};
                details.parts = [];
                details.part = 0;
                details.partBytes = uploadFileSizeLimit;
                details.progress = 0;
                details.partProgress = 0;
                details.startByte = 0;
                details.endByte = 0;
                details.file = fileDetails.files[i];
                details.status = ObjectService.UploadFileStatus.READY;
                details.objectId = fileDetails.originObjectId;
                details.objectType = fileDetails.originObjectType;
                details.folderId = fileDetails.folderId;
                details.fileType = fileDetails.fileType;
                details.currentProgress = 0;
                details.parentObjectNumber = fileDetails.parentObjectNumber;
                details.lang = fileDetails.lang;

                var uuid = Date.now();
                details.uuid = uuid;

                $scope.hashMap[uuid] = details;

                startUpload(uuid);
            }
        });
    }
    ]);