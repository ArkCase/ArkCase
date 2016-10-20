'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', '$rootScope', 'Profile.UserInfoService', 'Profile.ProfilePictureService','$log',
                                                               'Dialog.BootboxService','$translate',
    function ($scope, $rootScope, UserInfoService, ProfilePictureService,$log,DialogService,$translate) {
        $scope.$emit('req-component-config', 'picture');
        $scope.changePic = function () {
            $("#file").click();
        };
        $scope.submit = function () {
            if ($scope.userPicture != null) {
                UserInfoService.getUserInfo().then(function (data) {
                    var userID = data.userOrgId;
                    if ($scope.userPicture.$error){
                        DialogService.alert($translate.instant("profile.picture.uploadImgError"));
                    }
                    else {
                    ProfilePictureService.changePic($scope.userPicture, userID)
                        .success(function (fileInfo) {
                            var ecmFileID = fileInfo[0].fileId;
                            $scope.profileEcmFileID = ecmFileID;
                            UserInfoService.getUserInfo().then(function (infoData) {
                                infoData.ecmFileId = $scope.profileEcmFileID;
                                UserInfoService.updateUserInfo(infoData);
                                $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' :
                                'api/latest/plugin/ecm/download?ecmFileId='+$scope.profileEcmFileID+'&inline=true';
                                $rootScope.$broadcast('uploadedPicture', $scope.profileEcmFileID);
                            });
                        })
                        .error(function () {
                            $log.error('error during uploading user profile picture');
                        });
                    }
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
            $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/nopic.png' :
            'api/latest/plugin/ecm/download?ecmFileId='+$scope.profileEcmFileID+'&inline=true';
        });
    }
]);
