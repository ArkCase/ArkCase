'use strict';

angular.module('admin').controller('Admin.SequenceManagementPartsModalConfigController', [ '$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', function($scope, $modalInstance, params, UtilDateService, $filter) {


    // $scope.sequence = params.sequence;
    // $scope.sequenceName = $scope.sequence.sequenceName;
    // $scope.sequenceDescription = $scope.sequence.sequenceDescription;
    //$scope.sequenceStatus = $scope.sequence.sequenceStatus;
    //TODO: Fix sequenceDescription


    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    //TODO: Fix saving
    $scope.onClickOk = function() {
        //$scope.holidays.holidayDate = $filter('date')($scope.holidays.holidayDate, "yyyy-MM-dd");

        $modalInstance.close($scope.sequence);
    };

} ]);