'use strict';

angular.module('core').controller('UserInfoController', ['$scope', '$state', 'Profile.UserInfoService', 'Menus',
    function ($scope, $state, UserInfoService, Menus) {
        $scope.menu = Menus.getMenu('usermenu');

        $scope.profilePicDefault = true;
        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.fullName = data.fullName;
            if ($scope.profileEcmFileID !== null) {
                $scope.profilePicDefault = false;
            }
        });

        $scope.onClickLogout = function () {
            $state.go("goodbye");
        }
    }
]);