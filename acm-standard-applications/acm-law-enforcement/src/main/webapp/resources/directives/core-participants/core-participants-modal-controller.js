'use strict';

angular.module('directives').controller('Directives.CoreParticipantsModalController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService', 'params',
    function ($scope, $modal, $modalInstance, $translate, Util, paramsOwn) {

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

            var params={};
            $scope.owningGroup=paramsOwn.owningGroup;

            if($scope.participant.participantType == "assignee") {
                params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
                params.filter = 'fq="object_type_s": USER &fq="groups_id_ss": '+$scope.owningGroup;
                params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
            } else
            if ($scope.participant.participantType != "owning group" && $scope.participant.participantType.lastIndexOf("group-", 0) != 0) {
                params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
                params.filter = '"Object Type": USER';
                params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
            }
            else{
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
                	$scope.participant.participantLdapId = ($scope.participant.participantType === "owning group" || $scope.participant.participantType.lastIndexOf("group-", 0) === 0) ? selected.name : selected.object_id_s;
                    $scope.selectedType = selected.object_type_s;
                }
            });
        };
    }
]);