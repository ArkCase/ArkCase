angular.module('organizations').controller('Organization.AliasesModalController', ['$scope', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $modalInstance, ObjectLookupService, params) {
            ObjectLookupService.getAliasTypes().then(function (response) {
                $scope.aliasTypes = response;
            });

            $scope.alias = params.alias;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        alias: $scope.alias,
                        isDefault: $scope.isDefault
                    }
                );
            };
        }
    ]
);