'use strict';

angular.module('admin').controller('Admin.BrandingCustomCssController', ['$scope', 'Admin.BrandingCustomCssService', 'MessageService', '$translate',
    function ($scope, brandingCustomCssService, messageService, $translate) {
        $scope.initialCustomCssData = '';
        $scope.saveBtnDisabled = true;

        $scope.aceLoaded = function (_editor) {
            $scope.aceSession = _editor.getSession();
            brandingCustomCssService.getCustomCss().then(function (payload) {
                $scope.aceSession.setValue(payload.data);
                $scope.initialCustomCssData = payload.data;
            });
        };

        $scope.dataChanged = function () {
            var currentData = $scope.aceSession.getValue();
            if ($scope.initialCustomCssData != currentData) {
                $scope.saveBtnDisabled = false;
            } else {
                $scope.saveBtnDisabled = true;
            }
        };

        $scope.saveCssData = function () {
            brandingCustomCssService.saveCustomCss($scope.aceSession.getValue()).then(function () {
                //success save
                messageService.info($translate.instant('admin.branding.customCss.save.success'));
            }, function () {
                //error save
                messageService.info($translate.instant('admin.branding.customCss.save.error'));
            });
        };
    }
]);