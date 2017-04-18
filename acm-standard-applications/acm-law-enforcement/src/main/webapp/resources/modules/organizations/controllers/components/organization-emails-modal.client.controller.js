angular.module('organizations').controller('Organizations.EmailsModalController', ['$scope', '$modalInstance', 'Object.LookupService',
        function ($scope, $modalInstance, ObjectLookupService) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.emailTypes = _.find(contactMethodTypes, {type: 'email'}).subTypes;
                    return contactMethodTypes;
                });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        email: $scope.email,
                        isDefailt: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);