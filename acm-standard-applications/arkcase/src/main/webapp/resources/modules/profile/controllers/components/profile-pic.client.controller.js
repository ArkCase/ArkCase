'use strict';

angular.module('profile').controller('Profile.PicController',
        [ '$scope', '$rootScope', 'Profile.UserInfoService', 'Profile.ProfilePictureService', '$log', 'Dialog.BootboxService', '$translate', 'MessageService', function($scope, $rootScope, UserInfoService, ProfilePictureService, $log, DialogService, $translate, messageService) {
            $scope.changePic = function() {
                $("#file").click();
            };
            $scope.changeSignature = function() {
                $("#fileSignature").click();
            };
            $scope.submit = function() {
                if ($scope.userPicture != null) {
                    UserInfoService.getUserInfo().then(function(data) {
                        var userID = data.userOrgId;
                        if ($scope.userPicture.$error) {
                            DialogService.alert($translate.instant("profile.picture.uploadImgError"));
                        } else {
                            ProfilePictureService.changePic($scope.userPicture, userID).success(function(fileInfo) {
                                var ecmFileID = fileInfo[0].fileId;
                                $scope.profileEcmFileID = ecmFileID;
                                UserInfoService.getUserInfo().then(function(infoData) {
                                    infoData.ecmFileId = $scope.profileEcmFileID;
                                    UserInfoService.updateUserInfo(infoData);
                                    $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/arkcase_logo.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmFileID + '&parentObjectType=USER_ORG' + '&inline=true';
                                    $rootScope.$broadcast('uploadedPicture', $scope.profileEcmFileID);
                                });
                            }).error(function() {
                                $log.error('error during uploading user profile picture');
                                messageService.error($translate.instant('profile.picture.uploadError'));
                            });
                        }
                    });
                }
            };
            $scope.submitSignature = function() {
                if ($scope.userSignature != null) {
                    UserInfoService.getUserInfo().then(function(data) {
                        var userID = data.userOrgId;
                        if ($scope.userSignature.$error) {
                            DialogService.alert($translate.instant("profile.picture.uploadImgError"));
                        } else {
                            ProfilePictureService.changeSignature($scope.userSignature, userID).success(function(fileInfo) {
                                var ecmFileID = fileInfo[0].fileId;
                                $scope.profileEcmSignatureFileID = ecmFileID;
                                UserInfoService.getUserInfo().then(function(infoData) {
                                    infoData.ecmSignatureFileId = $scope.profileEcmSignatureFileID;
                                    UserInfoService.updateUserInfo(infoData);
                                    $scope.imgSignatureSrc = !$scope.profileEcmSignatureFileID ? 'modules/profile/img/nosignature.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmSignatureFileID + '&parentObjectType=USER_ORG' + '&inline=true';
                                });
                            }).error(function() {
                                $log.error('error during uploading user signature');
                                messageService.error($translate.instant('profile.signature.uploadError'));
                            });
                        }
                    });
                }
            };
            $scope.update = function() {
                var profileInfo;
                UserInfoService.getUserInfo().then(function(infoData) {
                    profileInfo = infoData;
                    profileInfo.fullName = $scope.profilePicFullName;
                    profileInfo.email = $scope.profilePicEmail;
                    profileInfo.title = $scope.profilePicTitle;
                    UserInfoService.updateUserInfo(profileInfo);
                });
            };
            UserInfoService.getUserInfo().then(function(data) {
                $scope.profilePicFullName = data.fullName;
                $scope.profilePicEmail = data.email;
                $scope.profilePicTitle = data.title;
                $scope.profileEcmFileID = data.ecmFileId;
                $scope.imgSrc = !$scope.profileEcmFileID ? 'modules/profile/img/arkcase_logo.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmFileID + '&parentObjectType=USER_ORG' + '&inline=true';
                // signature
                $scope.profileEcmSignatureFileID = data.ecmSignatureFileId;
                $scope.imgSignatureSrc = !$scope.profileEcmSignatureFileID ? 'modules/profile/img/nosignature.png' : 'api/latest/plugin/ecm/download?ecmFileId=' + $scope.profileEcmSignatureFileID + '&parentObjectType=USER_ORG' + '&inline=true';
            });
        } ]);
