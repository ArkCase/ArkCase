'use strict';

angular.module('cases').controller('Cases.ReturnReasonModalController', [ '$scope', '$modalInstance', function($scope, $modalInstance) {

    $scope.returnReason = "";

    $scope.onClickOk = function() {
        $modalInstance.close($scope.returnReason);
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);