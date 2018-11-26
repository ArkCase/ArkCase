'use strict';

angular.module('core').controller(
    'UploadManagerController',
    [ '$scope', '$q', '$state', '$translate', '$modal', '$http', '$timeout', '$document', 'Upload', 'MessageService', 'UtilService',
        function($scope, $q, $state, $translate, $modal, $http, $timeout, $document, Upload, MessageService, Util) {

        $scope.hideUploadSnackbar = true;

        var modalInstance = null;

        $scope.$bus.subscribe('upload-chunk-file', function(fileDetails){

            if (modalInstance === null) {
                modalInstance = $modal.open({
                    templateUrl: "modules/core/views/upload-progress-bar-modal.html",
                    controller: 'UploadManagerModalController',
                    size: 'lg',
                    backdrop: 'static',
                    keyboard: false,
                    backdropClass: "uploadManagerComponent",
                    windowClass: "uploadManagerComponent"
                });

                modalInstance.opened.then(function(){
                    startUploadChunkFile(fileDetails);
                })

                modalInstance.hide = function () {
                    $('.uploadManagerComponent').hide();
                    $scope.hideUploadSnackbar = false;
                };

                modalInstance.show = function () {
                    $('.uploadManagerComponent').show();
                    $scope.hideUploadSnackbar = true;
                };

                $scope.$bus.subscribe('upload-manager-show', modalInstance.show);
                $scope.$bus.subscribe('upload-manager-hide', modalInstance.hide);
            } else {
                modalInstance.show();
                startUploadChunkFile(fileDetails);
            }

        });

        $scope.onClickViewDetailsModal = function () {
            $scope.$bus.publish('upload-manager-show');
        };

        function startUploadChunkFile(fileDetails) {
            if (!Util.isEmpty(fileDetails)) {
                $scope.$bus.publish('start-upload-chunk-file', fileDetails);
            }
        }
} ]);