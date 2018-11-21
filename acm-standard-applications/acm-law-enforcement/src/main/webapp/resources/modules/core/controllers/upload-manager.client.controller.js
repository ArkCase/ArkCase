'use strict';

angular.module('core').controller(
    'UploadManagerController',
    [ '$scope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'Upload', 'MessageService',
        function($scope, $q, $state, $translate, $modal, $http, $timeout, $document, Upload, MessageService) {

        $scope.hideUploadSnackbar = true;

        $scope.onClickViewDetailsModal = function(){
            $scope.hideUploadSnackbar = true;
            $scope.modalInstance.show();

        };

        $scope.getPartFile = function (uuid) {
            $scope.hashMap[uuid].part++;
            $scope.hashMap[uuid].startByte = $scope.hashMap[uuid].endByte;
            $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].endByte + $scope.hashMap[uuid].partBytes;

            if ($scope.hashMap[uuid].endByte >= $scope.hashMap[uuid].file.size) {
                $scope.hashMap[uuid].endByte = $scope.hashMap[uuid].file.size;
            }
            var file = $scope.hashMap[uuid].file.slice($scope.hashMap[uuid].startByte, $scope.hashMap[uuid].endByte);
            file.name = $scope.hashMap[uuid].file.name + "_" + $scope.hashMap[uuid].part + "_" + Date.now();

            return file;
        };

        $scope.startUpload = function (uuid) {
            $scope.uploadPart(uuid);
        };

        $scope.uploadPart = function (uuid) {
            var file = $scope.getPartFile(uuid);
            $scope.uploadPartFile(file, uuid);
        };

        $scope.modalInstance = null;

        $scope.$bus.subscribe('upload-chunk-file', function(fileDetails){

            $scope.uploadPartFile = function (file, uuid) {
                //$scope.currentChunkUploadProgress="";
                $scope.currentTotalUploadProgress="";
                //$scope.chunkUploadPercentage="";
                $scope.totalUploadPercentage="";

                if ($scope.modalInstance === null) {
                    $scope.modalInstance = $modal.open({
                        templateUrl: "modules/core/views/upload-progress-bar-modal.html",
                        controller: ['$scope', '$modalInstance', 'result', function ($scope, $modalInstance, result) {
                            $scope.hashMap = result.hashMap;

                            //$scope.currentChunkUploadProgress = $scope.currentChunkUploadProgress;
                            $scope.currentTotalUploadProgress = $scope.currentTotalUploadProgress;
                            //$scope.chunkUploadPercentage = $scope.chunkUploadPercentage;
                            $scope.totalUploadPercentage = $scope.totalUploadPercentage;

                            $scope.getPartFile = result.getPartFile;

                            $scope.onClickHideModal = function () {
                                //$scope.hideUploadModal = true;
                                //$scope.hideUploadSnackbar = false;
                                //TODO once again maybe wrap in a function maybe with document.getElementsById set class back to hide modal show CSS
                                //var hideUploadSnackbar = false;
                                //$modalInstance.close({hide: hideUploadSnackbar});
                                $scope.$bus.publish('upload-manager-hide');

                            };

                            $scope.onClickHideTheUploadedFile = function () {
                                $scope.hideProgressbar = true;
                            };

                            /*$scope.onClickAddMoreFiles = function(){
                            //TODO: IMPLEMENT SO THAT, WHEN THIS IS EXECUTED A NEW FILE UPLOAD FORM(windows os component) WILL BE OPENED AND THE FILE CAN BE SELECTED. THIS WILL TRIGGER FILEUPLOADCHUNKS YET AGAIN
                            };*/

                            $scope.uploadChunks = function (file, uuid) {
                                Upload.upload({
                                    url: 'api/latest/service/ecm/uploadChunks',
                                    file: file,
                                    params: {
                                        uuid: uuid
                                    }
                                }).then(function (result) {
                                    var _uuid = result.data.uuid;
                                    //$scope.currentChunkUploadProgress = $scope.currentChunkUploadProgress;
                                    $scope.currentTotalUploadProgress = $scope.currentTotalUploadProgress;

                                    //$scope.chunkUploadPercentage = $scope.chunkUploadPercentage;
                                    $scope.totalUploadPercentage = $scope.totalUploadPercentage;


                                    $scope.hashMap[_uuid].parts.push(result.data.fileName);
                                    if ($scope.hashMap[_uuid].endByte < $scope.hashMap[_uuid].file.size) {
                                        $scope.hashMap[_uuid].progress = $scope.hashMap[_uuid].progress + $scope.hashMap[_uuid].partProgress;
                                        var file = $scope.getPartFile(uuid);
                                        $scope.uploadChunks(file, _uuid);
                                    } else {
                                        var data = {};
                                        data.name = $scope.hashMap[_uuid].file.name;
                                        data.mimeType = $scope.hashMap[_uuid].file.type;
                                        data.objectId = fileDetails.originObjectId;
                                        data.objectType = fileDetails.originObjectType;
                                        data.folderId = fileDetails.folderId;
                                        data.parts = $scope.hashMap[_uuid].parts;
                                        data.fileType = fileDetails.fileType;
                                        data.uuid = _uuid;
                                        $http({
                                            method: "POST",
                                            url: 'api/latest/service/ecm/mergeChunks',
                                            data: data
                                        }).then(function () {
                                            if ($scope.currentTotalUploadProgress >= 100) {
                                                /*$timeout(function() {
                                                    $modalInstance.close();
                                                }, 2000);*/
                                            }
                                        });
                                    }
                                    $scope.onClickCancel = function () {
                                        $modalInstance.close();
                                    };

                                }, function (error) {
                                    MessageService.error($translate.instant('common.directive.docTree.progressBar.failed') + ": " + error);
                                }, function (progress) {
                                    $scope.hashMap[uuid].partProgress = progress.loaded;
                                    //$scope.currentChunkUploadProgress = parseInt(100.0 * ($scope.hashMap[uuid].partProgress / progress.total));
                                    $scope.currentTotalUploadProgress = parseInt(100.0 * (($scope.hashMap[uuid].progress + $scope.hashMap[uuid].partProgress) / $scope.hashMap[uuid].file.size));

                                    /*$scope.chunkUploadPercentage = {
                                        width: $scope.currentChunkUploadProgress + '%'
                                    };*/
                                    $scope.totalUploadPercentage = {
                                        width: $scope.currentTotalUploadProgress + '%'
                                    };
                                });

                            };

                            $scope.uploadChunks(result.file, result.uuid);
                        }],
                        size: 'lg',
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                            result: {
                                file: file,
                                uuid: uuid,
                                hashMap: $scope.hashMap,
                                getPartFile: $scope.getPartFile
                            }
                        },
                        backdropClass: "uploadManagerComponent",
                        windowClass: "uploadManagerComponent"
                    });

                    $scope.modalInstance.hide = function () {
                        var elements = $('.uploadManagerComponent');
                        $('.uploadManagerComponent').hide();
                        $scope.hideUploadSnackbar = false;
                    };

                    $scope.modalInstance.show = function () {
                        var elements = $('.uploadManagerComponent');
                        $('.uploadManagerComponent').show();
                    };

                    $scope.$bus.subscribe('upload-manager-hide', $scope.modalInstance.hide);
                } else {
                    $scope.modalInstance.show();
                }
            };

            $scope.hashMap = {};

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

                var uuid = Date.now();
                $scope.hashMap[uuid] = details;

                $scope.startUpload(uuid);
            }
        });
        } ]);