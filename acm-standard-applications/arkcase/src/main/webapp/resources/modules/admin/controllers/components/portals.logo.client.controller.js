'use strict';

angular.module('admin').controller(
    'Admin.PortalsLogoController',
    [ '$scope', 'Admin.PortalConfigurationService', '$translate', 'MessageService', 'UtilService', function($scope, portalConfigurationService, $translate, messageService, Util) {

                $scope.uploadingInProgress = false;
                $scope.loadingProgress = 0;
                $scope.selectedHeaderFile = null;
                $scope.selectedLoginFile = null;
                $scope.selectedBannerFile = null;

                $scope.isFileTypeValid = function() {
                        var headerLogoType, loginLogoType, bannerLogoType;
                        if (!Util.isEmpty($scope.selectedLoginFile)) {
                                loginLogoType = $scope.selectedLoginFile.name.match(/\.png$/i);
                        }
                        if (!Util.isEmpty($scope.selectedHeaderFile)) {
                                headerLogoType = $scope.selectedHeaderFile.name.match(/\.png$/i);
                        }
                        if (!Util.isEmpty($scope.selectedBannerFile)) {
                                bannerLogoType = $scope.selectedBannerFile.name.match(/\.png$/i);
                        }
                        if (headerLogoType === null || loginLogoType === null || bannerLogoType === null || (typeof (headerLogoType) == 'undefined' && typeof (loginLogoType) == 'undefined' && typeof (bannerLogoType) == 'undefined')) {
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
                                formNames.push('headerLogoPortal');
                        }
                        if ($scope.selectedLoginFile) {
                                files.push($scope.selectedLoginFile);
                                formNames.push('loginLogoPortal');
                        }
                        if ($scope.selectedBannerFile) {
                                files.push($scope.selectedBannerFile);
                                formNames.push('bannerPortal');
                        }

                        portalConfigurationService.uploadLogo(files, formNames).success(function() {

                                if ($scope.selectedLoginFile === null && $scope.selectedHeaderFile === null && $scope.selectedBannerFile === null) {
                                        messageService.error($translate.instant('admin.portals.logo.messages.upload.error'));
                                } else {
                                        messageService.info($translate.instant('admin.portals.logo.messages.upload.success'));
                                }

                                $scope.uploadingInProgress = false;
                                $scope.selectedHeaderFile = null;
                                $scope.selectedLoginFile = null;
                                $scope.selectedBannerFile = null;
                        }).error(function() {
                                $scope.uploadingInProgress = false;
                                messageService.error($translate.instant('admin.portals.logo.messages.upload.error'));
                        }).progress(function(e) {
                                if (e.total > 0) {
                                        $scope.loadingProgress = parseInt(100.0 * e.loaded / e.total);
                                }
                        });
                };

        }]);
