angular.module('request-info').controller('RequestInfo.ErrorDialogController', [ '$scope', '$modalInstance', 'errorMessage', function($scope, $modalInstance, errorMessage) {
    $scope.errorMessage = errorMessage;
    $scope.onClickOk = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);