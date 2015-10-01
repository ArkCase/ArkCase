'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', 'Profile.UserInfoService', 'Profile.ProfilePictureService','$log',
    function ($scope, UserInfoService, ProfilePictureService,$log) {
        $scope.$emit('req-component-config', 'picture');
        $scope.profilePicDefault = true;
        $scope.changePic = function () {
            $("#file").click();
        };
        $scope.submit = function () {
            if ($scope.userPicture != null) {
                UserInfoService.getUserInfo().then(function (data) {
                    var userID = data.userOrgId;
                    ProfilePictureService.changePic($scope.userPicture, userID)
                            .success(function (fileInfo) {
                                var ecmFileID = fileInfo[0].fileId;
                                $scope.profileEcmFileID = ecmFileID;
                                UserInfoService.getUserInfo().then(function (infoData) {
                                    infoData.ecmFileId = $scope.profileEcmFileID;
                                    UserInfoService.updateUserInfo(infoData);
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
            UserInfoService.getUserInfo().then(function (infoData) {
                profileInfo = infoData;
                profileInfo.fullName = $scope.profilePicFullName;
                profileInfo.email = $scope.profilePicEmail;
                profileInfo.title = $scope.profilePicTitle;
                UserInfoService.updateUserInfo(profileInfo);
            });
        };
        UserInfoService.getUserInfo().then(function (data) {
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
