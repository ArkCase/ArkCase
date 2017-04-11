angular.module('tasks').controller('Person.PictureUploadDialogController', ['$scope', '$modalInstance',
        function ($scope, $modalInstance) {
            $scope.userPicture = null;
            $scope.isDefault = false;
            $scope.upload = function (data) {
                console.log(data);
                console.log($scope.userPicture);
            };
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