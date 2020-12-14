'use strict';

angular.module('admin').controller('Admin.TimesheetModalController', [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {

    $scope.chargeRoleItem = {
        chargeRole: '',
        rate: 0,
        active: ''
    };

    $scope.chargeRoleItem.chargeRole = params.chargeRoleItem.chargeRole;
    $scope.chargeRoleItem.rate = params.chargeRoleItem.rate;
    $scope.chargeRoleItem.active = params.chargeRoleItem.active;
    $scope.isEdit = params.isEdit;
    $scope.chargeRoleDropdownOptions = params.chargeRoleDropdownOptions;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            chargeRoleItem: $scope.chargeRoleItem
        });
    };

} ]);