'use strict';

angular.module('admin').controller('Admin.ApplicationVersionController', [ '$scope', 'Admin.ApplicationVersionService', function($scope, ApplicationVersionService) {

    $scope.applicationVersion = "";
    ApplicationVersionService.getApplicationVersion().then(function (result) {
        $scope.applicationVersion = result.data["Implementation-Version"];
        var warBuildTime = moment.utc(result.data["Build-Time"]).format("MMMM DD, YYYY");
        var jarModifiedTime = "";
        var isJarTimeNewer = false;
        if (result.data["JarModifiedTime"]) {
            jarModifiedTime = moment.utc(result.data["JarModifiedTime"]).format("MMMM DD, YYYY");
            isJarTimeNewer = moment(jarModifiedTime).isAfter(warBuildTime);
        }

        if (jarModifiedTime !== "" && isJarTimeNewer === true) {
            $scope.buildTime = jarModifiedTime;
        } else {
            $scope.buildTime = warBuildTime;
        }
        if (result.data["extensionVersion"]) {
            $scope.extensionVersion = result.data["extensionVersion"];
        }
    });
}]);