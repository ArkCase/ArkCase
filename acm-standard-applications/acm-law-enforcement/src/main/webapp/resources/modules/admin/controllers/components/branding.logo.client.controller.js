'use strict';

angular.module('admin').controller('Admin.BrandingLogoController', [ '$scope', 'Admin.BrandingLogoService', '$translate', 'MessageService', 'UtilService', function($scope, brandingLogoService, $translate, messageService, Util) {
    $scope.uploadingInProgress = false;
    $scope.loadingProgress = 0;
    $scope.selectedHeaderFile = null;
    $scope.selectedLoginFile = null;
    $scope.selectedEmailFile = null;

    $scope.isFileTypeValid = function() {
        var headerLogoType, loginLogoType, emailLogoType;
        if (!Util.isEmpty($scope.selectedLoginFile)) {
            loginLogoType = $scope.selectedLoginFile.name.match(/\.png$/i);
        }
        if (!Util.isEmpty($scope.selectedHeaderFile)) {
            headerLogoType = $scope.selectedHeaderFile.name.match(/\.png$/i);
        }
        if (!Util.isEmpty($scope.selectedEmailFile)) {
            emailLogoType = $scope.selectedEmailFile.name.match(/\.png$/i);
        }
        if (headerLogoType === null || loginLogoType === null || emailLogoType === null || (typeof (headerLogoType) == 'undefined' && typeof (loginLogoType) == 'undefined' && typeof (emailLogoType) == 'undefined')) {
            return null;
        } else {
            return true;
        }
    }
    $scope.saveFiles = function() {
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
        if ($scope.selectedEmailFile) {
            files.push($scope.selectedEmailFile);
            formNames.push('emailLogo');
        }

        brandingLogoService.uploadLogo(files, formNames).success(function() {

            if ($scope.selectedLoginFile === null && $scope.selectedHeaderFile === null && $scope.selectedEmailFile === null) {
                messageService.error($translate.instant('admin.branding.logo.messages.upload.error'));
            } else {
                messageService.info($translate.instant('admin.branding.logo.messages.upload.success'));
            }

            $scope.uploadingInProgress = false;
            $scope.selectedHeaderFile = null;
            $scope.selectedLoginFile = null;
            $scope.selectedEmailFile = null;
        }).error(function() {
            $scope.uploadingInProgress = false;
            messageService.error($translate.instant('admin.branding.logo.messages.upload.error'));
        }).progress(function(e) {
            if (e.total > 0) {
                $scope.loadingProgress = parseInt(100.0 * e.loaded / e.total);
            }
        });
    };
} ]);