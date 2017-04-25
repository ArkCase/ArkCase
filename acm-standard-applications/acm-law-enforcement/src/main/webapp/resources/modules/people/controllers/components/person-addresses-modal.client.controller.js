angular.module('people').controller('People.AddressesModalController', ['$scope', '$modalInstance', 'Object.LookupService',
        function ($scope, $modalInstance, ObjectLookupService) {

            ObjectLookupService.getAddressTypes().then(
                function (addressTypes) {
                    $scope.addressTypes = addressTypes;
                    return addressTypes;
                });

            ObjectLookupService.getCountries().then(function (countries) {
                $scope.countries = countries;
            });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        address: $scope.address,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);