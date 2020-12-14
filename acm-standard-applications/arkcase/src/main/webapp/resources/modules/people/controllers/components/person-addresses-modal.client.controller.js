angular.module('people').controller('People.AddressesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'UtilService', function ($scope, $translate, $modalInstance, ObjectLookupService, params, Util) {

    ObjectLookupService.getAddressTypes().then(function(addressTypes) {
        $scope.addressTypes = addressTypes;
        return addressTypes;
    });

    ObjectLookupService.getCountries().then(function(countries) {
        $scope.countries = countries;
    });
    
    $scope.changeStates = function(country){
        $scope.state = "";
        if(country == 'US') {
            $scope.state = 'states';
        } else if (country == 'CA') {
            $scope.state = 'canadaProvinces';
        } else if (country == 'JP') {
            $scope.state = 'japanStates';
        }
        $scope.updateStates($scope.state);
    };

    $scope.updateStates = function (state) {
        if (!Util.isEmpty(state))
        ObjectLookupService.getLookupByLookupName($scope.state).then(function (states) {
            $scope.states = states;
        });
    };

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
