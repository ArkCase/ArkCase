angular.module('people').controller('People.PhonesModalController', ['$scope', '$translate','$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.phoneTypes = _.find(contactMethodTypes, {key: 'phone'}).subLookup;
                    return contactMethodTypes;
                });

            $scope.phone = params.phone;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;
            $scope.hideNoField = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        phone: $scope.phone,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);