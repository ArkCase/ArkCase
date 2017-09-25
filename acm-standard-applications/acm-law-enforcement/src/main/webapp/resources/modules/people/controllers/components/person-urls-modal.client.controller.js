angular.module('people').controller('People.UrlsModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.urlTypes = _.find(contactMethodTypes, {key: 'url'}).subLookup;
                    return contactMethodTypes;
                });

            $scope.url = params.url;
            $scope.isEdit = params.isEdit;
            $scope.isDefault = params.isDefault;
            $scope.hideNoField = params.isDefault;

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        url: $scope.url,
                        isDefault: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);