'use strict';

angular.module('admin').controller('Admin.ApplicationVersionController', [ '$scope', 'Admin.ApplicationVersionService', function($scope, ApplicationVersionService) {

    $scope.applicationVersion = "";
    ApplicationVersionService.getApplicationVersion().then(function (result) {
        var applicationVersion = result.data.applicationVersion;
        $scope.applicationVersion = "v" + applicationVersion.substring(0, applicationVersion.indexOf("-"));
    });
}]);