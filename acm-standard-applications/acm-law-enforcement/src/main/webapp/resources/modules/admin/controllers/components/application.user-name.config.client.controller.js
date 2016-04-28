'use strict';

angular.module('admin').controller('Admin.UserNameConfigController', ['$scope', '$q', 'Admin.ApplicationSettingsService',
    function ($scope, $q, ApplicationSettingsService) {

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROPERTIES.DISPLAY_USERNAME).then(function (response) {
            $scope.nameProperty = response.data[ApplicationSettingsService.PROPERTIES.DISPLAY_USERNAME];
        });

        $scope.applyChanges = function () {
            ApplicationSettingsService.setProperty(
                ApplicationSettingsService.PROPERTIES.DISPLAY_USERNAME,
                $scope.nameProperty
            );
        }
    }
]);