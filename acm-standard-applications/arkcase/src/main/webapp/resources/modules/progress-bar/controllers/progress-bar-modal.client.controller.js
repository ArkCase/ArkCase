'use strict';

angular.module('progress-bar').controller('ProgressBarModalController',
    ['$scope', '$rootScope', '$modalInstance', '$http', '$translate', '$timeout', '$interval', '$log', 'Upload', 'MessageService', 'ObjectService', 'UtilService', function ($scope, $rootScope, $modalInstance, $http, $translate, $timeout, $interval, $log, Upload, MessageService, ObjectService, Util) {
        $scope.versionedFile = {};

        $scope.onClickHideModal = function () {
            $scope.$bus.publish('progress-bar-modal-hide');
        };

        $scope.$bus.subscribe('progress-snackbar-close', function () {
            $scope.openNewVersionOfFile();
        });

        $scope.openNewVersionOfFile = function () {
            $modalInstance.close();
            $scope.clearVersionedFileDetails();
            $scope.$bus.publish('open-new-version-of-file');
            $scope.$emit('document-updated-refresh-needed', '');
        };

        $scope.clearVersionedFileDetails = function () {
            $scope.versionedFile = {};
            $scope.versionedFile.success = true
        }

        function startVersioning() {
            var stop;
            var snackBarIcon = {
                hide: false
            };
            $scope.$bus.publish('notify-snackbar-progress-status', snackBarIcon);
            stop = $interval(updateCurrentProcess, 500);
            $scope.stopCount = function () {
                $interval.cancel(stop);
                stop = undefined;
            }
        }

        $scope.$bus.subscribe('update-modal-progressbar-current-progress', function (message) {
            $scope.$apply(function () {
                var status = ObjectService.UploadFileStatus.IN_PROGRESS;
                message.status = status;
                updateSnackbarProgress(message);
            });
        });

        function updateCurrentProcess() {
            if ($scope.versionedFile.currentProgress >= 99) {
                $scope.stopCount();
                $timeout(function () {
                    if ($scope.versionedFile.currentProgress === 99) {
                        var message = {};
                        message.objectId = $scope.versionedFile.id;
                        message.objectType = $scope.versionedFile.requestType;
                        message.success = false;
                        message.currentProgress = 100;
                        message.status = ObjectService.UploadFileStatus.FAILED;
                        $scope.$bus.publish('finish-modal-progressbar-current-progress', message);
                    }
                }, 10000);
            } else {
                var message = {};
                message.status = ObjectService.UploadFileStatus.IN_PROGRESS;
                message.success = true;
                message.currentProgress = $scope.versionedFile.currentProgress + 1;
                updateSnackbarProgress(message);
            }
        }

        function updateSnackbarProgress(message) {
            $scope.versionedFile.status = message.status;
            $scope.versionedFile.currentProgress = message.currentProgress;
            $scope.versionedFile.success = message.success;
            $scope.$bus.publish('update-snackbar-current-progress', message);
        }

        $scope.$bus.subscribe('start-progress-bar-process', function (fileDetails) {
            var currentFileDetails = {};
            currentFileDetails.file = fileDetails.file;
            currentFileDetails.fileName = fileDetails.fileName;
            currentFileDetails.status = Util.isEmpty(fileDetails.status) ? ObjectService.UploadFileStatus.READY : fileDetails.status;
            currentFileDetails.objectId = fileDetails.originObjectId;
            currentFileDetails.objectType = fileDetails.originObjectType;
            currentFileDetails.folderId = fileDetails.folderId;
            currentFileDetails.fileType = fileDetails.fileType;
            currentFileDetails.currentProgress = Util.isEmpty(fileDetails.file.currentProgress) ? 0 : fileDetails.file.currentProgress;
            currentFileDetails.parentObjectNumber = fileDetails.parentObjectNumber;
            currentFileDetails.fileLang = Util.isEmpty(fileDetails.lang) ? "en" : fileDetails.lang;
            currentFileDetails.date = Date.now();
            currentFileDetails.success = true;

            $scope.versionedFile = currentFileDetails;

            startVersioning();
        });

        $scope.$bus.subscribe('finish-modal-progressbar-current-progress', function (message) {
            var status = message.success ? ObjectService.UploadFileStatus.FINISHED : ObjectService.UploadFileStatus.FAILED;
            message.status = status;
            updateSnackbarProgress(message);
            var snackBarIcon = {
                hide: true
            };
            $timeout(function () {
                $scope.$bus.publish('notify-snackbar-progress-status', snackBarIcon);
            }, 2000);

        });

        $scope.$on('$destroy', function () {
            $scope.stopCount();
        });
    }

    ]);