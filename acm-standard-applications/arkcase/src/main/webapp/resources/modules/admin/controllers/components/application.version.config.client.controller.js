'use strict';

angular.module('admin').controller('Admin.ApplicationVersionController', [ '$scope', 'Admin.ApplicationVersionService', function($scope, ApplicationVersionService) {

    $scope.applicationVersion = "";
    ApplicationVersionService.getApplicationVersion().then(function (result) {
        $scope.applicationVersion = result.data["Implementation-Version"];
        $scope.buildTime = moment.utc(result.data["Build-Time"]).format("MMMM DD, YYYY");
        if (result.data["JarModifiedTime"]) {
            $scope.jarModifiedTime = moment.utc(result.data["JarModifiedTime"]).format("MMMM DD, YYYY");
        }
        if (result.data["extensionVersion"]) {
            $scope.extensionVersion = result.data["extensionVersion"];
        }
        if (!$scope.jarModifiedTime && $scope.applicationVersion === $scope.extensionVersion) {
            $scope.jarModifiedTime = moment.utc(result.data["Build-Time"]).format("MMMM DD, YYYY");
        }
    });
}]);