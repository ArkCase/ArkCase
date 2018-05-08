angular.module('organizations').controller('Organizations.AddressesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', function($scope, $translate, $modalInstance, ObjectLookupService, params) {

    ObjectLookupService.getAddressTypes().then(function(addressTypes) {
        $scope.addressTypes = addressTypes;
        return addressTypes;
    });

    ObjectLookupService.getCountries().then(function(countries) {
        $scope.countries = countries;
    });

    $scope.address = params.address;
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };
    $scope.onClickOk = function() {
        $modalInstance.close({
            address: $scope.address,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit
        });
    };
} ]);