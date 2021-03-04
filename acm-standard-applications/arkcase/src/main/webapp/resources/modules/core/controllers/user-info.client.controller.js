'use strict';

angular.module('core').controller('UserInfoController', ['$scope', 'Profile.UserInfoService', 'Menus', 'Acm.LoginService', 'Admin.ApplicationSettingsService', 'MessageService', 'Admin.ZylabIntegrationService',
    function ($scope, UserInfoService, Menus, AcmLoginService, ApplicationSettingsService, MessageService, ZylabIntegrationService) {

        ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
            $scope.helpUrl = response.data["application.properties.helpUrl"];
        });

        ZylabIntegrationService.getConfiguration().then(function (response) {
            $scope.documentReviewEnabled = response.data["zylabIntegration.enabled"];
            $scope.documentReviewLink = response.data["zylabIntegration.url"] + response.data["zylabIntegration.documentReviewPath"];
        });

        $scope.menu = Menus.getMenu('usermenu');

        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.user = {
                userId: data.userId,
                userName: data.fullName
            };
            $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/arkcase_logo.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmFileID + '&parentObjectType=USER_ORG' + '&inline=true';
        });

        $scope.$on('uploadedPicture', function (event, arg) {
            $scope.imgSrc = !arg ? 'modules/profile/img/arkcase_logo.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + arg + '&parentObjectType=USER_ORG' + '&inline=true';
        });

        $scope.onClickLogout = function () {
            AcmLoginService.logout();
        };

        $scope.$bus.subscribe('sync-progress', function (data) {
            MessageService.info(data.message);
        });

        $scope.$bus.subscribe('sequence-error', function (data) {
            MessageService.error(data.message);
        });

    }]);
