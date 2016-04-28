'use strict';

angular.module('admin').controller('Admin.UserNameConfigController', ['$scope', '$q', 'Admin.ApplicationSettingsService',
    function ($scope, $q, ApplicationSettingsService) {

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROP_NAME).then(function (response) {
            $scope.nameProperty = response.data[ApplicationSettingsService.PROP_NAME];
        });

        $scope.applyChanges = function () {
            ApplicationSettingsService.setProperty(
                ApplicationSettingsService.PROP_NAME,
                $scope.nameProperty
            );
        }
    }
]);