'use strict';

angular.module('progress-bar').controller(
    'ProgressBarManagerController',
    ['$scope', '$rootScope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'MessageService', 'UtilService', 'ObjectService',
        function ($scope, $rootScope, $q, $state, $translate, $modal, $http, $timeout, $document, MessageService, Util, ObjectService) {

            $scope.hideSnackbar = true;
            $scope.hideSnackbarIcon = false;

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

            $scope.onClickCloseSnackbar = function () {
                $scope.hideSnackbar = true;
                $scope.$bus.publish('progress-snackbar-close');
            }

            $scope.onClickViewDetailsModal = function () {
                $scope.$bus.publish('progress-bar-modal-show');
            };

            function startProgressBarProcess(fileDetails) {
                if (!Util.isEmpty(fileDetails)) {
                    $scope.$bus.publish('start-progress-bar-process', fileDetails);
                }
            }

            $scope.$bus.subscribe('update-snackbar-current-progress', function (message) {
                $scope.currentProgress = message.currentProgress;
            })

            $scope.$bus.subscribe('notify-snackbar-progress-status', function (hideSnackbarIcon) {
                $scope.hideSnackbarIcon = hideSnackbarIcon.hide;
                ;
            })
        }]);