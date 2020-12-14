'use strict';

angular.module('admin').controller('Admin.SequenceEditSequenceNumberController', ['$scope', '$modalInstance', 'Admin.SequenceManagementService', 'MessageService', 'params', 'UtilService', function ($scope, $modalInstance, AdminSequenceManagementService, MessageService, params, Util) {

    $scope.sequenceEntity = {};

    AdminSequenceManagementService.getSequenceEntity(params.sequenceName, params.sequencePartName).then(function (data) {
        if (!Util.isEmpty(data.data)) {
            $scope.sequenceEntity.sequenceName = data.data.sequenceName;
            $scope.sequenceEntity.sequencePartName = data.data.sequencePartName;
            $scope.sequenceEntity.version = data.data.version;
            $scope.sequenceEntity.sequencePartValue = data.data.sequencePartValue;

        } else {
            document.getElementById("sequencePartValue").disabled = true;
        }
    });

    $scope.onClickOk = function () {
        $modalInstance.close(
            AdminSequenceManagementService.updateSequenceNumber($scope.sequenceEntity).then(function () {
                MessageService.succsessAction();
            }, function (error) {
                if (error.data && error.data.message) {
                    MessageService.error(error.data.message);
                } else {
                    MessageService.errorAction();
                }
            })
        )
    };


    $scope.onClickCancel = function () {
        $modalInstance.dismiss('Cancel');
    };


}]);