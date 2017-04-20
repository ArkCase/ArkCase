angular.module('people').controller('People.IDsModalController', ['$scope', '$modalInstance', 'Object.LookupService',
        function ($scope, $modalInstance, ObjectLookupService) {

            ObjectLookupService.getIdentificationTypes().then(
                function (identificationTypes) {
                    $scope.identificationTypes = identificationTypes;
                    return identificationTypes;
                });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        identification: $scope.identification,
                        isDefailt: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);