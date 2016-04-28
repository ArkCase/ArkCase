'use strict';

angular.module('core').controller('UserInfoController', ['$rootScope', '$scope', '$state'
    , 'Profile.UserInfoService', 'Menus', 'Util.TimerService', 'Acm.LoginStatService'
    , function ($rootScope, $scope, $state
        , UserInfoService, Menus, UtilTimerService, AcmLoginStatService
    ) {

        $scope.menu = Menus.getMenu('usermenu');

        UserInfoService.getUserInfo().then(function (data) {
            $scope.profileEcmFileID = data.ecmFileId;
            $scope.fullName = data.fullName;
            $scope.userId = data.userId;
            $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId='+$scope.profileEcmFileID+'&inline=true';
        });

        $scope.$on('uploadedPicture', function (event, arg) {
            $scope.imgSrc = !arg ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId='+ arg +'&inline=true';
        });

        $scope.onClickLogout = function () {
            AcmLoginStatService.setLogin(false);
            $state.go("goodbye");
        };

        UtilTimerService.useTimer("LoginStat", 4000, function() {
            var isLogin = AcmLoginStatService.isLogin();
            if (!isLogin) {
                $state.go("goodbye");
                return false;
            }
            return true;
        });
    }
]);
