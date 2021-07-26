'use strict';

angular.module('progress-bar').controller('ProgressBarModalController',
    ['$scope', '$rootScope', '$modalInstance', '$http', '$translate', '$timeout', '$interval', '$log', 'Upload', 'MessageService', 'ObjectService', 'UtilService', function ($scope, $rootScope, $modalInstance, $http, $translate, $timeout, $interval, $log, Upload, MessageService, ObjectService, Util) {
        $scope.versionedFiles = [];
        $scope.timeout = false;
        $scope.allFilesCompleted = 0;
        $scope.counterMap = [];

        $scope.onClickHideModal = function () {
            $scope.$bus.publish('progress-bar-modal-hide');
        };

        $scope.openNewVersionOfFile = function () {
            $modalInstance.close();
            $scope.$bus.publish('clear-versioned-files');
            $scope.$bus.publish('open-new-version-of-file');
            $scope.$emit('document-updated-refresh-needed', '');
        };

        function startVersioning() {
            if (!Util.isEmpty($scope.versionedFiles)) {
                for (var i = 0; i < $scope.versionedFiles.length; i++) {
                    var file = $scope.versionedFiles[i];
                    if (file.status === ObjectService.UploadFileStatus.READY) {
                        var snackBarIcon = {
                            hide: false
                        };
                        $scope.$bus.publish('notify-snackbar-progress-status', snackBarIcon);
                        if (file.pageCount > 200) {
                            startUpdateProgress(file, 3000);
                        } else {
                            startUpdateProgress(file, 500);
                        }
                    }
                }
            }
        }

        function startUpdateProgress(file, timer) {
            var stop = $interval(function () {
                updateCurrentProgress(file)
            }, timer);
            var counter = {
                fileId: file.fileId,
                stop: stop
            }
            $scope.counterMap.push(counter);
        }

        $scope.$bus.subscribe('clear-versioned-files', function () {
            $scope.versionedFiles = [];
            $scope.counterMap = [];
            $scope.allFilesCompleted = 0;
        });

        $scope.$bus.subscribe('update-modal-progressbar-current-progress', function (message) {
            $scope.$apply(function () {
                var file = _.find($scope.versionedFiles, {
                    fileId: message.id
                });
                var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
                message.status = status;
                file.status = status;
                file.currentProgress = message.currentProgress;
                file.success = message.success;
                updateSnackbarProgress(message);
            });
        });

        function updateCurrentProgress(file) {
            if (file.currentProgress > 99) {
                var counter = _.find($scope.counterMap, {
                    fileId: file.fileId
                });
                if (counter) {
                    $interval.cancel(counter.stop);
                    $scope.allFilesCompleted += 1;
                }
            } else if (file.currentProgress === 99) {
                $timeout(function () {
                    if (file.currentProgress === 99) {
                        $scope.timeout = true;
                    }
                }, 10000);
            } else {
                file.status = ObjectService.UploadFileStatus.IN_PROGRESS;
                file.success = true;
                file.currentProgress = file.currentProgress + 1;
                var message = {};
                message.currentProgress = file.currentProgress
                updateSnackbarProgress(message);
            }
        }

        function updateSnackbarProgress(message) {
            $scope.$bus.publish('update-snackbar-current-progress', message);
        }

        $scope.$bus.subscribe('start-progress-bar-process', function (fileDetails) {
            var currentFileDetails = {};
            currentFileDetails.fileId = fileDetails.fileId;
            currentFileDetails.file = fileDetails.file;
            currentFileDetails.fileName = fileDetails.fileName;
            currentFileDetails.status = Util.isEmpty(fileDetails.status) ? ObjectService.UploadFileStatus.READY : fileDetails.status;
            currentFileDetails.objectId = fileDetails.originObjectId;
            currentFileDetails.objectType = fileDetails.originObjectType;
            currentFileDetails.folderId = fileDetails.folderId;
            currentFileDetails.fileType = fileDetails.fileType;
            currentFileDetails.pageCount = fileDetails.pageCount;
            currentFileDetails.currentProgress = Util.isEmpty(fileDetails.file.currentProgress) ? 0 : fileDetails.file.currentProgress;
            currentFileDetails.parentObjectNumber = fileDetails.parentObjectNumber;
            currentFileDetails.fileLang = Util.isEmpty(fileDetails.lang) ? "en" : fileDetails.lang;
            currentFileDetails.date = Date.now();
            currentFileDetails.success = true;

            var alreadyBurning = _.find($scope.versionedFiles, {
                fileId: currentFileDetails.fileId
            });
            if (!alreadyBurning) {
                $scope.versionedFiles.push(currentFileDetails);
            } else {
                var index = $scope.versionedFiles.indexOf(alreadyBurning);
                $scope.versionedFiles[index] = currentFileDetails;
            }
            $scope.$bus.publish('start-snackbar-progress', currentFileDetails);

            startVersioning();
        });

        $scope.$bus.subscribe('finish-modal-progressbar-current-progress', function (message) {
            var file = _.find($scope.versionedFiles, {
                fileId: message.id
            });
            var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
            message.status = status;
            file.status = status;
            file.currentProgress = message.currentProgress;
            file.success = message.success;
            updateSnackbarProgress(message);
            var snackBarIcon = {
                hide: true
            };
            $timeout(function () {
                $scope.$bus.publish('notify-snackbar-progress-status', snackBarIcon);
            }, 2000);

        });

        $scope.$on('$destroy', function () {
            if (!Util.isEmpty($scope.counterMap)) {
                for (var counter in $scope.counterMap) {
                    $interval.cancel(counter.stop);
                }
            }
        });
    }

    ]);