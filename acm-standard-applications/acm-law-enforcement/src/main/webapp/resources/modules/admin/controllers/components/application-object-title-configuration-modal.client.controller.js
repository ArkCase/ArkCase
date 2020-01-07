'use strict';

angular.module('admin').controller('Admin.ObjectTitleConfigurationModalController', [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {

    $scope.objectTitleTypes = {};

    $scope.objectTitleTypes.objectType = params.objectTitleTypes.objectType;
    $scope.objectTitleTypes.enableTitleField = params.objectTitleTypes.enableTitleField;
    $scope.objectTitleTypes.title = params.objectTitleTypes.title;
    $scope.isEdit = params.isEdit;
    $scope.objectTitleTypesDropdownOptions = params.objectTitleTypesDropdownOptions;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {
        $modalInstance.close({
            objectTitleTypes: $scope.objectTitleTypes
        });
    };

} ]);