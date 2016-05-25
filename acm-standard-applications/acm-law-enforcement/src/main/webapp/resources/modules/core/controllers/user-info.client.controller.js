'use strict';

angular.module('core').controller('UserInfoController', ['$scope'
    , 'Profile.UserInfoService', 'Menus', 'Acm.LoginService'
    , function ($scope, UserInfoService, Menus, AcmLoginService) {
        $scope.menu = Menus.getMenu('usermenu');

        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.user = {
                userId: data.userId,
                userName: data.fullName
            };
            $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmFileID + '&inline=true';
        });

        $scope.$on('uploadedPicture', function (event, arg) {
            $scope.imgSrc = !arg ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId=' + arg + '&inline=true';
        });

        $scope.onClickLogout = function () {
            AcmLoginService.logout();
        };

    }
]);
