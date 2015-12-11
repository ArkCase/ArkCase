'use strict';

angular.module('admin').controller('Admin.BrandingLogoController', ['$scope', 'Admin.BrandingLogoService', '$translate', 'MessageService',
    function ($scope, brandingLogoService, $translate, messageService) {
        $scope.uploadingInProgress = false;
        $scope.loadingProgress = 0;
        $scope.selectedHeaderFile = null;
        $scope.selectedLoginFile = null;

        $scope.saveFiles = function () {
            $scope.uploadingInProgress = true;
            //we need to have and form names for files upload
            var files = [];
            var formNames = [];
            if ($scope.selectedHeaderFile) {
                files.push($scope.selectedHeaderFile);
                formNames.push('headerLogo');
            }
            if ($scope.selectedLoginFile) {
                files.push($scope.selectedLoginFile);
                formNames.push('loginLogo');
            }

            brandingLogoService.uploadLogo(files, formNames)
                .success(function () {
                    $scope.uploadingInProgress = false;
                    $scope.selectedHeaderFile = null;
                    $scope.selectedLoginFile = null;
                    messageService.info($translate.instant('admin.branding.logo.messages.upload.success'));
                })
                .error(function () {
                    $scope.uploadingInProgress = false;
                    messageService.error($translate.instant('admin.branding.logo.messages.upload.error'));
                })
                .progress(function (e) {
                    if (e.total > 0) {
                        $scope.loadingProgress = parseInt(100.0 * e.loaded / e.total);
                    }
                });
        };
    }
]);