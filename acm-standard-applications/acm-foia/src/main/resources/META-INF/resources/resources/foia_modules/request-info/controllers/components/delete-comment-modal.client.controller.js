'use strict';

angular.module('request-info').controller('RequestInfo.DeleteCommentModalController', [ '$scope', '$modalInstance', function($scope, $modalInstance) {

    $scope.deleteComment = "";

    $scope.onClickOk = function() {
        $modalInstance.close($scope.deleteComment);
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);