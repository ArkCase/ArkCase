angular.module('people').controller('People.EmailsModalController', ['$scope', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.emailTypes = _.find(contactMethodTypes, {type: 'email'}).subTypes;
                    return contactMethodTypes;
                });

            $scope.email = params.email;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;

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