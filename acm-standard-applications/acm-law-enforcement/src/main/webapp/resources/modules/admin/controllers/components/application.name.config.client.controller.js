'use strict';

angular.module('admin').controller('Admin.NameConfigController', ['$scope', '$q', 'Admin.ApplicationSettingsService',
    function ($scope, $q, ApplicationSettingsService) {

        $scope.nameProperty = ApplicationSettingsService.getProperty(ApplicationSettingsService.PROP_NAME);

        $scope.applyChanges = function () {
            ApplicationSettingsService.setProperty(
                ApplicationSettingsService.PROP_NAME,
                $scope.nameProperty
            );
        }
    }
]);