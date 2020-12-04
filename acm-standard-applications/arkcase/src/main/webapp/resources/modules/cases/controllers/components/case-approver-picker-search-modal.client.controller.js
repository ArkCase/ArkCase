'use strict';

angular.module('cases').controller('Cases.ApproverPickerController', [ '$scope', '$modalInstance', 'modalParams', function($scope, $modalInstance, modalParams) {
    $scope.header = modalParams.header;
    $scope.filter = modalParams.filter;
    $scope.extraFilter = modalParams.extraFilter;
    $scope.config = modalParams.config;
    $scope.modalInstance = $modalInstance;
} ]);
