'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', 'ConfigService', 'getUserInfo','userPicService','$http',
    function ($scope, ConfigService, getUserInfo,userPicService,$http) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.profilePicDefault=true;
        $scope.changePic = function () {
            $("#file").click();
        };
        $scope.selectImage = function () {
            $("#submit").click();
        };
        $scope.sub = function () {
            getUserInfo.async().then(function (data) {
                var formData = new FormData();
                var userID = data.userOrgId;
                formData.append("parentObjectId", userID);
                formData.append("parentObjectType", "USER_ORG");
                formData.append("fileType", "user_profile");
                formData.append("file", $("#file")[0].files[0]);
                userPicService.changePic(formData)
                        .then(function(data){
                            var ecmFileID=data[0].fileId;
                            $scope.profileEcmFieldID=ecmFileID;
                            getUserInfo.async().then(function (infoData) {
                                infoData.ecmFileId = $scope.profileEcmFieldID;
                                return ($http.post('proxy/arkcase/api/latest/plugin/profile/userOrgInfo/set', infoData));
                            });
                        });
            });
        };
        $scope.update = function () {
            var profileInfo;
            getUserInfo.async().then(function (infoData) {
                profileInfo = infoData;
                profileInfo.fullName = $scope.profilePicFullName;
                profileInfo.email = $scope.profilePicEmail;
                profileInfo.title = $scope.profilePicTitle;
                return ($http.post('proxy/arkcase/api/latest/plugin/profile/userOrgInfo/set', profileInfo));
            });
        };
        getUserInfo.async().then(function (data) {
            $scope.profilePicFullName = data.fullName;
            $scope.profilePicEmail = data.email;
            $scope.profilePicTitle = data.title;
            $scope.profileEcmFieldID=data.ecmFileId;
            if($scope.profileEcmFieldID !== null){
               $scope.profilePicDefault=false;
            }
        });
    }
]);
