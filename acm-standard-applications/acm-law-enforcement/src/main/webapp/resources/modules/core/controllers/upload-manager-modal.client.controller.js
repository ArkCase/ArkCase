'use strict';

angular.module('core').controller(
    'UploadManagerModalController',
    ['$scope', '$modalInstance', '$http', '$translate', 'Upload', 'MessageService', function ($scope, $modalInstance, $http, $translate, Upload, MessageService) {
        $scope.hashMap = {};

        $scope.onClickHideModal = function () {
            $scope.$bus.publish('upload-manager-hide');
        };

        $scope.onClickHideTheUploadedFile = function () {
            $scope.hideProgressbar = true;
        };

        function getPartFile(uuid) {
            $scope.hashMap[uuid].part++;
            $scope.hashMap[uuid].startByte = $scope.hashMap[uuid].endByte;
            $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].endByte + $scope.hashMap[uuid].partBytes;

            if ($scope.hashMap[uuid].endByte >= $scope.hashMap[uuid].file.size) {
                $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].file.size;
            }
            var file = $scope.hashMap[uuid].file.slice($scope.hashMap[uuid].startByte, $scope.hashMap[uuid].endByte);
            file.name = $scope.hashMap[uuid].file.name + "_" + $scope.hashMap[uuid].part + "_" + Date.now();

            return file;
        }

        function startUpload(uuid) {
            uploadPart(uuid);
        };

        function uploadPart(uuid) {
            var file = getPartFile(uuid);
            uploadChunks(file, uuid);
        };

        function uploadChunks(file, uuid) {
            $scope.hashMap[uuid].status = "IN_PROGRESS";
            Upload.upload({
                url: 'api/latest/service/ecm/uploadChunks',
                file: file,
                params: {
                    uuid: uuid
                }
            }).then(function (result) {
                var _uuid = result.data.uuid;

                $scope.hashMap[_uuid].parts.push(result.data.fileName);
                if ($scope.hashMap[_uuid].endByte < $scope.hashMap[_uuid].file.size) {
                    $scope.hashMap[_uuid].progress = $scope.hashMap[_uuid].progress + $scope.hashMap[_uuid].partProgress;
                    uploadPart(_uuid);
                } else {
                    $scope.hashMap[_uuid].status = "FINISHED";
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
                $scope.onClickClose = function () {
                    $modalInstance.dismiss('close');
                };

            }, function (error) {
                $scope.hashMap[uuid].status = "FAILED";
                MessageService.error($translate.instant('common.directive.docTree.progressBar.failed') + ": " + error);
            }, function (progress) {
                $scope.hashMap[uuid].partProgress = progress.loaded;
                $scope.hashMap[uuid].currentProgress = parseInt(100.0 * (($scope.hashMap[uuid].progress + $scope.hashMap[uuid].partProgress) / $scope.hashMap[uuid].file.size));
            });

        }

        $scope.$bus.subscribe('start-upload-chunk-file', function (fileDetails) {
            for (var i = 0; i < fileDetails.files.length; i++) {
                var details = {};
                details.parts = [];
                details.part = 0;
                details.partBytes = 52428800; // 50MB
                details.progress = 0;
                details.partProgress = 0;
                details.startByte = 0;
                details.endByte = 0;
                details.file = fileDetails.files[i];
                details.status = "READY"; // "READY", "IN_PROGRESS", "FINISHED", "FAILED"
                details.objectId = fileDetails.originObjectId;
                details.objectType = fileDetails.originObjectType;
                details.folderId = fileDetails.folderId;
                details.fileType = fileDetails.fileType;
                details.currentProgress = 0;

                var uuid = Date.now();
                $scope.hashMap[uuid] = details;
                startUpload(uuid);
            }
        });
    }
    ]);