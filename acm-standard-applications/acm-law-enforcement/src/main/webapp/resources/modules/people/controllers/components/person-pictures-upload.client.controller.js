angular.module('people').controller('Person.PictureUploadDialogController', [ '$scope', '$modalInstance', 'params', 'Person.PicturesService', 'MessageService', function($scope, $modalInstance, params, PicturesService, MessageService) {
    $scope.userPicture = {};
    $scope.userPicture.isEdit = false;

    if (params.userPicture != undefined) {
        $scope.userPicture.isEdit = true;
        $scope.userPicture.name = params.userPicture;
    }
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;
    $scope.image = params.image;

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };

    $scope.onClickOk = function() {

        $modalInstance.close({
            file: $scope.userPicture,
            image: $scope.image,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit
        });
    };
    $scope.changeImageDescription = function () {
        PicturesService.changeImageDescription($scope.objectInfo.id, $scope.image.fileId, $scope.isDefault, $scope.image.description).then(function (data) {
            $modalInstance.close({
                image: data
            });
            MessageService.succsessAction();
        }, function () {
            MessageService.errorAction();
        });
    };
} ]);