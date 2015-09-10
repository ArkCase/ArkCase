'use strict';

angular.module('profile').controller('Profile.PicController', ['$scope', 'ConfigService', 'getUserInfo',
    function ($scope, ConfigService, getUserInfo) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.changePic = function () {
            $("#file").click();
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
        });
    }
]);
