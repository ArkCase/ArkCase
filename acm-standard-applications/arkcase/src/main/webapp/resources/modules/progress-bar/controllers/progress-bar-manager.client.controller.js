'use strict';

angular.module('progress-bar').controller(
    'ProgressBarManagerController',
    ['$scope', '$rootScope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'MessageService', 'UtilService', 'ObjectService',
        function ($scope, $rootScope, $q, $state, $translate, $modal, $http, $timeout, $document, MessageService, Util, ObjectService) {

            $scope.hideSnackbar = true;
            $scope.hideSnackbarIcon = false;
            $scope.versionedFiles = [];

            var modalInstance = null;

            $scope.$bus.subscribe('open-progress-bar-modal', function (fileDetails) {

                if (modalInstance === null) {
                    modalInstance = $modal.open({
                        templateUrl: "modules/progress-bar/views/progress-bar-modal.html",
                        controller: 'ProgressBarModalController',
                        size: 'lg',
                        backdrop: 'static',
                        keyboard: false,
                        backdropClass: "progressBarManagerController",
                        windowClass: "progressBarManagerController",
                    });

                    modalInstance.opened.then(function () {
                        startProgressBarProcess(fileDetails);
                    });

                    modalInstance.hide = function () {
                        $('.progressBarManagerController').hide();
                        $scope.hideSnackbar = false;
                    };

                    modalInstance.show = function () {
                        $('.progressBarManagerController').show();
                        $scope.hideSnackbar = true;
                    };

                    modalInstance.close = function () {
                        $('.progressBarManagerController').hide();
                        $scope.hideSnackbar = true;
                    };

                    $scope.$bus.subscribe('progress-bar-modal-show', modalInstance.show);

                    $scope.$bus.subscribe('progress-bar-modal-hide', function () {
                        modalInstance.hide();
                    });
                } else {
                    modalInstance.show();
                    startProgressBarProcess(fileDetails);
                }

            });

            $scope.onClickCloseModal = function () {
                $scope.$bus.publish('progress-bar-modal-hide');
            };

            $scope.onClickViewDetailsModal = function () {
                $scope.$bus.publish('progress-bar-modal-show');
            };

            function startProgressBarProcess(fileDetails) {
                if (!Util.isEmpty(fileDetails)) {
                    $scope.$bus.publish('start-progress-bar-process', fileDetails);
                }
            }

            $scope.$bus.subscribe('update-snackbar-current-progress', function (message) {
                var file = _.find($scope.versionedFiles, {
                    fileId: message.id
                });
                if (file) file.currentProgress = message.currentProgress;
            });

            $scope.$bus.subscribe('start-snackbar-progress', function (file) {
                $scope.versionedFiles.push(file);
            });

            $scope.$bus.subscribe('clear-versioned-files', function () {
                $scope.versionedFiles = [];
            });

            $scope.$bus.subscribe('notify-snackbar-progress-status', function (hideSnackbarIcon) {
                if (hideSnackbarIcon.hide) {
                    for (var i = 0; i < $scope.versionedFiles.length; i++) {
                        if ($scope.versionedFiles[i].status !== ObjectService.UploadFileStatus.FINISHED) {
                            hideSnackbarIcon.hide = false;
                            break;
                        }
                    }
                }
                $scope.hideSnackbarIcon = hideSnackbarIcon.hide;
            });
        }]);