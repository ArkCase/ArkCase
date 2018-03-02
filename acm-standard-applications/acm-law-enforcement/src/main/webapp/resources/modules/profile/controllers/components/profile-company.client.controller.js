'use strict';

angular.module('profile').controller(
        'Profile.CompanyController',
        [ '$scope', 'Profile.UserInfoService', 'MessageService', '$translate',
                function($scope, UserInfoService, MessageService, $translate) {
                    $scope.editDisabled = false;
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
                            profileInfo.website = $scope.profileCompanyWebsite;
                            UserInfoService.updateUserInfo(profileInfo);
                        });

                        $scope.disableEdit();
                    };

                    $scope.enableEdit = function() {
                        $scope.editDisabled = true;
                        MessageService.info($translate.instant('profile.company.editEnabled'));
                    };

                    $scope.disableEdit = function() {
                        $scope.editDisabled = false;
                    };

                    UserInfoService.getUserInfo().then(function(data) {
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
                } ]);
