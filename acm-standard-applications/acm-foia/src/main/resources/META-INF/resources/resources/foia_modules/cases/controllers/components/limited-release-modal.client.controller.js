'use strict';

angular.module('cases').controller('Cases.LimitedReleaseModalController', ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {

    $scope.limitedDeliveryFlag = false;
    $scope.pageCount = params.pageCount;

    $scope.onClickOk = function () {
        $modalInstance.close($scope.limitedDeliveryFlag);
    };

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);