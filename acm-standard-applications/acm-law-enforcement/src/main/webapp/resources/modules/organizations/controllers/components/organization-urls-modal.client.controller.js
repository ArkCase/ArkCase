angular.module('organizations').controller('Organizations.UrlsModalController', ['$scope', '$modalInstance', 'Object.LookupService',
        function ($scope, $modalInstance, ObjectLookupService) {

            ObjectLookupService.getContactMethodTypes().then(
                function (contactMethodTypes) {
                    $scope.urlTypes = _.find(contactMethodTypes, {type: 'url'}).subTypes;
                    return contactMethodTypes;
                });

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        url: $scope.url,
                        isDefailt: $scope.isDefault,
                        isEdit: $scope.isEdit
                    }
                );
            };
        }
    ]
);