angular.module('organizations').controller('Organizations.IDsModalController', ['$scope', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getIdentificationTypes().then(
                function (identificationTypes) {
                    $scope.identificationTypes = identificationTypes;
                    return identificationTypes;
                });

            $scope.identification = params.identification;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        identification: $scope.identification,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);