angular.module('people').controller('Person.PictureUploadDialogController', ['$scope', '$modalInstance',
        function ($scope, $modalInstance) {
            $scope.userPicture = null;
            $scope.isDefault = false;
            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };
            $scope.onClickOk = function () {
                $modalInstance.close(
                    {
                        file: $scope.userPicture,
                        isDefault: $scope.isDefault
                    }
                );
            };
        }
    ]
);