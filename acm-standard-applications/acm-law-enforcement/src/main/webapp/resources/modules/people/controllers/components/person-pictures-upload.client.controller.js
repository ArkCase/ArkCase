angular.module('people').controller('Person.PictureUploadDialogController', ['$scope', '$modalInstance', 'params',
    function ($scope, $modalInstance, params) {
        $scope.userPicture = {};
        $scope.userPicture.title = false;

        if (params.userPicture != undefined) {
            $scope.userPicture.title = true;
            $scope.userPicture.name = params.userPicture;
        }
        $scope.isEdit = params.isEdit;
        $scope.isDefault = params.isDefault;
        $scope.hideNoField = params.isDefault;
        $scope.image = params.image;

        $scope.onClickCancel = function () {
            $modalInstance.dismiss('Cancel');
        };

        $scope.onClickOk = function () {

            $modalInstance.close(
                {
                    file: $scope.userPicture,
                    image: $scope.image,
                    isDefault: $scope.isDefault,
                    isEdit: $scope.isEdit
                }
            );
        };
    }
]);