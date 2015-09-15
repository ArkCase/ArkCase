'use strict';

angular.module('profile').controller('Profile.CompanyController', ['$scope', 'ConfigService','userInfoService',
    function ($scope, ConfigService, userInfoService) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        ;
        $scope.update = function () {
            var profileInfo;
            userInfoService.getUserInfo().then(function(infoData) {
             profileInfo= infoData;
             profileInfo.companyName=$scope.profileCompanyName;
             profileInfo.firstAddress=$scope.profileCompanyAddress1;
             profileInfo.secondAddress=$scope.profileCompanyAddress2;
             profileInfo.city=$scope.profileCompanyCity;
             profileInfo.state=$scope.profileCompanyState;
             profileInfo.zip=$scope.profileCompanyZip;
             profileInfo.mainOfficePhone=$scope.profileCompanyMainPhone;
             profileInfo.fax=$scope.profileCompanyFax;
             profileInfo.website=$scope.profileCompanyWebsite;
             userInfoService.updateUserInfo(profileInfo);
            });
        };
        userInfoService.getUserInfo().then(function(data) {
            $scope.profileCompanyName = data.companyName;
            $scope.profileCompanyAddress1 = data.firstAddress;
            $scope.profileCompanyAddress2 = data.secondAddress;
            $scope.profileCompanyCity = data.city;
            $scope.profileCompanyState = data.state;
            $scope.profileCompanyZip = data.zip;
            $scope.profileCompanyMainPhone = data.mainOfficePhone;
            $scope.profileCompanyFax = data.fax;
            $scope.profileCompanyWebsite = data.website;
        });
    }
]);

