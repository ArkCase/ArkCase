'use strict';

angular.module('cases').controller('Dashboard.WebsiteModalController', [ '$scope', '$modalInstance', 'UtilService', 'websiteInfo', 'isEdit', function($scope, $modalInstance, Util, websiteInfo, isEdit) {
    $scope.modalInstance = $modalInstance;
    $scope.websiteInfo = websiteInfo;
    $scope.isEdit = isEdit;

    $scope.cancel = function() {
        $scope.modalInstance.dismiss('cancel');
    };

    $scope.confirm = function() {
        $scope.modalInstance.close($scope.websiteInfo);
    };

    $scope.disableConfirm = function() {
        return Util.isEmpty($scope.websiteInfo.url) || Util.isEmpty($scope.websiteInfo.name);
    };
} ]);
