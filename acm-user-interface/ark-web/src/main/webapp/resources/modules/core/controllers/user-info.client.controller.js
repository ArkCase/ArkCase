'use strict';

angular.module('core').controller('UserInfoController', ['$scope','Profile.UserInfoService', 'Menus',
    function($scope, UserInfoService, Menus) {
        $scope.menu = Menus.getMenu('usermenu');
        $scope.profilePicDefault = true;

        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.fullName = data.fullName;
            if ($scope.profileEcmFileID !== null) {
                $scope.profilePicDefault = false;
            }
        });
    }
]);