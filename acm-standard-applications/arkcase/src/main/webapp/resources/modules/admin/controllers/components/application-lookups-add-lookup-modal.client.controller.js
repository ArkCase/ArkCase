'use strict';

angular.module('admin').controller('Admin.AddLookupModalController', [ '$scope', '$modal', '$modalInstance', 'params', 'Object.LookupService', function($scope, $modal, $modalInstance, params, ObjectLookupService) {

    $scope.entry = params.entry;

    $scope.lookupTypes = ObjectLookupService.getLookupTypes;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            entry: $scope.entry
        });
    };
} ]);