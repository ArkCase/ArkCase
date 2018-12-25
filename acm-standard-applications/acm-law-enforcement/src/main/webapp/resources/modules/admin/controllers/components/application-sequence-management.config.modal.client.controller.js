'use strict';

angular.module('admin').controller('Admin.SequenceManagementModalController', [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', function($scope, $modalInstance, params, UtilDateService, $filter) {


    $scope.sequence = params.sequence;
    $scope.sequenceName = $scope.sequence.sequenceName;
    $scope.sequenceDescription = $scope.sequence.sequenceDescription;
    $scope.sequenceEnabled = $scope.sequence.sequenceEnabled;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close($scope.sequence);
    };

} ]);