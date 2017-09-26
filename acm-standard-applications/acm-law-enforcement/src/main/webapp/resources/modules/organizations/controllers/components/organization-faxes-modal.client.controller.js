angular.module('organizations').controller('Organizations.FaxesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $translate ,$modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.faxTypes = _.find(contactMethodTypes, {key: 'fax'}).subLookup;
                    return contactMethodTypes;
                });

            $scope.fax = params.fax;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;
            $scope.hideNoField = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        fax: $scope.fax,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);