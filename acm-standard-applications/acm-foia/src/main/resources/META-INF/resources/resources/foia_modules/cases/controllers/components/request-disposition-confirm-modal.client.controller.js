'use strict';

angular.module('cases').controller('Cases.RequestDispositionConfirmModalController', ['$scope', '$modalInstance', function ($scope, $modalInstance) {

    $scope.onClickYes = function () {
        $modalInstance.close();
    };

    $scope.onClickNo = function () {
        $modalInstance.dismiss('cancel');
    };
}]);