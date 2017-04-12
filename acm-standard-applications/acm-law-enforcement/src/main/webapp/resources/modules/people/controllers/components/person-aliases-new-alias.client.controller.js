angular.module('people').controller('Person.AliasesNewAliasDialogController', ['$scope', '$modalInstance', 'Object.LookupService',
        function ($scope, $modalInstance, ObjectLookupService) {
            $scope.alias = null;
            ObjectLookupService.getAliasTypes().then(function (response) {
                $scope.aliasTypes = response;
            });
            $scope.isDefault = false;
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