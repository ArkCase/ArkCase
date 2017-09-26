angular.module('organizations').controller('Organizations.EmailsModalController', ['$scope', '$translate','$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.emailTypes = _.find(contactMethodTypes, {key: 'email'}).subLookup;
                    return contactMethodTypes;
                });

            $scope.email = params.email;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;
            $scope.hideNoField = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        email: $scope.email,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);