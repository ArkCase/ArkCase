'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', 'userInfoService', 'userPicService','$log',
    function ($scope, userInfoService, userPicService,$log) {
        $scope.$emit('req-component-config', 'picture');
        $scope.profilePicDefault = true;
        $scope.changePic = function () {
            $("#file").click();
        };
        $scope.submit = function () {
            if ($scope.userPicture != null) {
                userInfoService.getUserInfo().then(function (data) {
                    var userID = data.userOrgId;
                    userPicService.changePic($scope.userPicture, userID)
                            .success(function (fileInfo) {
                                var ecmFileID = fileInfo[0].fileId;
                                $scope.profileEcmFileID = ecmFileID;
                                userInfoService.getUserInfo().then(function (infoData) {
                                    infoData.ecmFileId = $scope.profileEcmFileID;
                                    userInfoService.updateUserInfo(infoData);
                                });
                            })
                            .error(function () {
                                $log.error('error during uploading user profile picture');
                            });
                });
            }
        };
        $scope.update = function () {
            var profileInfo;
            userInfoService.getUserInfo().then(function (infoData) {
                profileInfo = infoData;
                profileInfo.fullName = $scope.profilePicFullName;
                profileInfo.email = $scope.profilePicEmail;
                profileInfo.title = $scope.profilePicTitle;
                userInfoService.updateUserInfo(profileInfo);
            });
        };
        userInfoService.getUserInfo().then(function (data) {
            $scope.profilePicFullName = data.fullName;
            $scope.profilePicEmail = data.email;
            $scope.profilePicTitle = data.title;
            $scope.profileEcmFileID = data.ecmFileId;
            if ($scope.profileEcmFileID !== null) {
                $scope.profilePicDefault = false;
            }
        });
    }
]);
