'use strict';

angular.module('directives').controller('Directives.CoreParticipantsModalController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService',
    function ($scope, $modal, $modalInstance, $translate, Util) {

        $scope.onClickOk = function () {
            $modalInstance.close({
                participant: $scope.participant,
                isEdit: $scope.isEdit,
                selectedType: $scope.selectedType
            });
        };
        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.pickParticipant = function () {

            var params = {};

            if ($scope.participant.participantType != "owning group") {
                params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
                params.filter = '"Object Type": USER';
                params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
            } else {
                params.header = $translate.instant("common.directive.coreParticipants.modal.dialogGroupPicker.header");
                params.filter = '"Object Type": GROUP';
                params.config = Util.goodMapValue($scope.config, "dialogGroupPicker");
            }

            var modalInstance = $modal.open({
                templateUrl: "directives/core-participants/core-participants-picker-modal.client.view.html",
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                    $scope.modalInstance = $modalInstance;
                    $scope.header = params.header;
                    $scope.filter = params.filter;
                    $scope.config = params.config;
                }],
                animation: true,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function () {
                        return params;
                    }
                }
            });
            modalInstance.result.then(function (selected) {
                if (!Util.isEmpty(selected)) {
                    $scope.participant.participantLdapId = selected.object_id_s;
                    $scope.selectedType = selected.object_type_s;
                }
            });
        };
    }
]);