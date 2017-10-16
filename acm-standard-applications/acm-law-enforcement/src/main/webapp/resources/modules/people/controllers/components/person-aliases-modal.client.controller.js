angular.module('people').controller('Person.AliasesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params) {
            ObjectLookupService.getAliasTypes().then(function (response) {
                $scope.aliasTypes = response;
            });

            $scope.alias = params.alias;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;
            $scope.hideNoField = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        alias: $scope.alias,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);