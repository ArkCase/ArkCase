'use strict';

angular.module('admin').controller('Admin.ApplicationVersionController', [ '$scope', 'Admin.ApplicationVersionService', function($scope, ApplicationVersionService) {

    $scope.applicationVersion = "";
    ApplicationVersionService.getApplicationVersion().then(function (result) {
        $scope.applicationVersion = result.data["Implementation-Version"];
        $scope.buildTime = result.data["Build-Time"];
        if (result.data["extensionVersion"]) {
            $scope.extensionVersion = result.data["extensionVersion"];
        }
    });
}]);