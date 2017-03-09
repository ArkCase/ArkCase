'use strict';

angular.module('profile').controller('Profile.LocaleController', ['$scope','$q', '$translate'
    , 'UtilService', 'Profile.UserInfoService', 'Admin.LabelsConfigService'
	, function($scope, $q, $translate
        , Util, UserInfoService, LabelsConfigService) {

        UserInfoService.getUserInfo().then(function(data) {
            $scope.profileInfo = data;

            var langPromise = LabelsConfigService.retrieveLanguages().$promise;
            var settingsPromise = LabelsConfigService.retrieveSettings().$promise;

            $q.all([langPromise, settingsPromise]).then(function (result) {
                var langs = result[0];
                var settings = result[1];
                $scope.languagesDropdownOptions = langs;
                $scope.profileInfo.prefLocale = Util.goodValue(data.prefLocale, settings.defaultLang);
            });
        });

        $scope.changeDefaultLng = function ($event, newLang) {
            $event.preventDefault();
            $scope.profileInfo.prefLocale = newLang;
            UserInfoService.updateUserInfo($scope.profileInfo);
            $translate.use(newLang);
        };
	}
]);
