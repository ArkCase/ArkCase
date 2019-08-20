'use strict';

angular.module('core').controller('UserInfoController', ['$scope', 'Profile.UserInfoService', 'Menus', 'Acm.LoginService', 'Admin.ApplicationSettingsService', 'MessageService', function ($scope, UserInfoService, Menus, AcmLoginService, ApplicationSettingsService, MessageService) {

    ApplicationSettingsService.getApplicationPropertiesConfig().then(function (response) {
        $scope.helpUrl = response.data["application.properties.helpUrl"];
    });

    $scope.menu = Menus.getMenu('usermenu');

    UserInfoService.getUserInfo().then(function(data) {
        $scope.profileEcmFileID = data.ecmFileId;
        $scope.user = {
            userId: data.userId,
            userName: data.fullName
        };
        $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmFileID + '&parentObjectType=USER_ORG' + '&inline=true';
    });

    $scope.$on('uploadedPicture', function(event, arg) {
        $scope.imgSrc = !arg ? 'modules/profile/img/nopic.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + arg + '&parentObjectType=USER_ORG' + '&inline=true';
    });

    $scope.onClickLogout = function() {
        AcmLoginService.logout();
    };

    $scope.$bus.subscribe('sync-progress', function(data) {
        MessageService.info(data.message);
    });
    
    $scope.$bus.subscribe('sequence-error', function(data) {
        MessageService.error(data.message);
    });

} ]);
