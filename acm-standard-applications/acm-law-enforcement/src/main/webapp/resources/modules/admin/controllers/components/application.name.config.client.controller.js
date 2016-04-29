'use strict';

angular.module('admin').controller('Admin.NameConfigController', ['$scope', '$q', 'Admin.ApplicationSettingsService',
    function ($scope, $q, ApplicationSettingsService) {

        ApplicationSettingsService.getProperty(ApplicationSettingsService.PROP_NAME).then(function (response) {
            $scope.nameProperty = response.data.name;
        });

        $scope.applyChanges = function () {
            ApplicationSettingsService.setProperty(
                ApplicationSettingsService.PROP_NAME,
                $scope.nameProperty
            );
        }
    }
]);