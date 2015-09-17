'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', 'ConfigService', 'userInfoService', 'userPicService',
    function ($scope, ConfigService, userInfoService, userPicService) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.profilePicDefault = true;
        $scope.changePic = function () {
            $("#file").click();
        };
        $scope.submit = function () {
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
                            console.log('error uploading');
                        });
            });
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
