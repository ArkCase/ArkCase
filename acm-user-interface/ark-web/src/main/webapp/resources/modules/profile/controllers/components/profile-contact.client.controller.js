'use strict';
angular.module('profile').controller('Profile.ContactController', ['$scope', 'ConfigService','userInfoService',
    function ($scope, ConfigService,userInfoService) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);


        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        };
        $scope.update=function(){
            var profileInfo;
            userInfoService.getUserInfo().then(function(infoData) {
             profileInfo= infoData;
             profileInfo.location=$scope.profileContactLocation;
             profileInfo.imAccount=$scope.profileContactIM;
             profileInfo.imSystem=$scope.profileContactIMSystem;
             profileInfo.officePhoneNumber=$scope.profileContactOfficephone;
             profileInfo.mobilePhoneNumber=$scope.profileContactMobilephone;
             userInfoService.updateUserInfo(profileInfo);
            }); 
        };
        userInfoService.getUserInfo().then(function(data) {
            $scope.profileContactLocation = data.location;
            $scope.profileContactIM = data.imAccount;
            $scope.profileContactIMSystem = data.imSystem;
            $scope.profileContactOfficephone = data.officePhoneNumber;
            $scope.profileContactMobilephone = data.mobilePhoneNumber;
        });


    }
]);