'use strict';

angular.module('admin').controller('Admin.SequenceManagementModalController', ['$scope', '$modalInstance', 'params', 'Util.DateService', '$filter', 'Object.LookupService', function ($scope, $modalInstance, params, UtilDateService, $filter, ObjectLookupService) {


    $scope.sequence = params.sequence;
    $scope.sequenceName = $scope.sequence.sequenceName;
    $scope.sequenceDescription = $scope.sequence.sequenceDescription;
    $scope.sequenceEnabled = $scope.sequence.sequenceEnabled;
    $scope.sequenceParts = [];
    $scope.isEdit = params.isEdit;

    ObjectLookupService.getSequenceName().then(function (sequenceName) {
        $scope.sequences = sequenceName;
        var defaultSequenceName = ObjectLookupService.getPrimaryLookup($scope.sequences);

        if ($scope.sequence.sequenceName == null && defaultSequenceName != null) {
            $scope.sequence.sequenceName = defaultSequenceName.key;
        }

    });

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function () {
        $modalInstance.close($scope.sequence);
    };

}]);