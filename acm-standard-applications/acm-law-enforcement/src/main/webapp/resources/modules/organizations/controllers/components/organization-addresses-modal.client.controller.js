angular.module('organizations').controller('Organizations.AddressesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', function($scope, $translate, $modalInstance, ObjectLookupService, params) {

    ObjectLookupService.getAddressTypes().then(function(addressTypes) {
        $scope.addressTypes = addressTypes;
        return addressTypes;
    });

    $scope.changeStates = function (country) {
        $scope.state = "";
        if (country == 'US') {
            $scope.state = 'states';
        } else if (country == 'CA') {
            $scope.state = 'canadaProvinces';
        } else if (country == 'JP') {
            $scope.state = 'japanStates';
        }
        $scope.updateStates();
    };

    $scope.updateStates = function () {
        ObjectLookupService.getLookupByLookupName($scope.state).then(function (states) {
            $scope.states = states;
        });
    };

    ObjectLookupService.getCountries().then(function(countries) {
        $scope.countries = countries;
    });

    $scope.address = params.address;
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;


    $scope.changeStates($scope.address.country);
    
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
