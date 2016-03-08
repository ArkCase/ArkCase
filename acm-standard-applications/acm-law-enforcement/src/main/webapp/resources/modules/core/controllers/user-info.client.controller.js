'use strict';

angular.module('core').controller('UserInfoController', ['$scope', '$state', 'Profile.UserInfoService', 'Menus',
    function ($scope, $state, UserInfoService, Menus) {
        $scope.menu = Menus.getMenu('usermenu');

        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.fullName = data.fullName;
            $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId='+$scope.profileEcmFileID+'&inline=true';
        });

        $scope.$on('uploadedPicture', function (event, arg) {
            $scope.imgSrc = !arg ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId='+ arg +'&inline=true';
        });

        $scope.onClickLogout = function () {
            $state.go("goodbye");
        }
    }
]);
