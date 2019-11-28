'use strict';

angular.module('profile').controller('Profile.CompanyController', [ '$scope', '$stateParams', 'Profile.UserInfoService', 'UtilService', function($scope, $stateParams, UserInfoService, Util) {

    $scope.editDisabled = false;
    var refresh = function() {
        UserInfoService.getUserInfo().then(function(data) {
            $scope.id = data.userOrgId;
            $scope.userId = data.userId;
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
    };

    refresh();

    $scope.update = function() {
        var profileInfo;
        UserInfoService.getUserInfo().then(function(infoData) {
            profileInfo = infoData;
            profileInfo.companyName = $scope.profileCompanyName;
            profileInfo.firstAddress = $scope.profileCompanyAddress1;
            profileInfo.secondAddress = $scope.profileCompanyAddress2;
            profileInfo.city = $scope.profileCompanyCity;
            profileInfo.state = $scope.profileCompanyState;
            profileInfo.zip = $scope.profileCompanyZip;
            profileInfo.mainOfficePhone = $scope.profileCompanyMainPhone;
            profileInfo.fax = $scope.profileCompanyFax;

            function res() {
                var res = $scope.profileCompanyWebsite;
                var httpRegex = "(http://|https://).+";
                var noHttpRegex = /[-a-zA-Z0-9@:%._\+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_\+.~#?&//=]*)?/gi;
                if (res.toLowerCase().match(httpRegex)) {
                    res = res.toLowerCase();
                } else if (res.toLowerCase().match(noHttpRegex)) {
                    res = ("http://" + res.toLowerCase());
                } else {
                    res = null;
                }
                return res;
            }

            profileInfo.website = res();

            UserInfoService.updateUserInfo(profileInfo).then(function() {
                refresh();
            });
        });
        $scope.disableEdit();
    };

    $scope.enableEdit = function() {
        $scope.editDisabled = true;
    };

    $scope.disableEdit = function() {
        $scope.editDisabled = false;
    };
} ]);
