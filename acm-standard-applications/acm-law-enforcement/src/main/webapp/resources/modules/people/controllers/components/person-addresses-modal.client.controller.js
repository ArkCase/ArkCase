angular.module('people').controller('People.AddressesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', function($scope, $translate, $modalInstance, ObjectLookupService, params) {

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
            $scope.state = 'canadaStates';
        } else if (country == 'JP') {
            $scope.state = 'japanStates';
        }
        $scope.updateStates();
    };
    
    $scope.updateStates = function(){
        ObjectLookupService.getLookupByLookupName($scope.state) .then(function(states) {
            $scope.states = states;
        });
    };

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