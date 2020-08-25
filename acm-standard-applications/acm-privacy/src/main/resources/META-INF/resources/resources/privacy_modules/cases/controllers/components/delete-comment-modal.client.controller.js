'use strict';

angular.module('cases').controller('Cases.DeleteCommentModalController', [ '$scope', '$modalInstance', function($scope, $modalInstance) {

    $scope.deleteComment = "";

    $scope.onClickOk = function() {
        $modalInstance.close($scope.deleteComment);
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);