'use strict';

angular.module('directives').controller('Directives.CoreParticipantsModalController', [ '$scope', '$modal', '$modalInstance', '$translate', 'UtilService', 'params', function($scope, $modal, $modalInstance, $translate, Util, paramsOwn) {
    $scope.participantEdit = {
        participantType: $scope.participant.participantType,
        participantLdapId: $scope.participant.participantLdapId,
        replaceChildrenParticipant: $scope.participant.showReplaceChildrenParticipants
    };
    $scope.onClickOk = function() {
        $modalInstance.close({
            participant: $scope.participantEdit,
            isEdit: $scope.isEdit,
            selectedType: $scope.selectedType
        });
    };
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.pickParticipant = function() {

        var params = {};
        $scope.owningGroup = paramsOwn.owningGroup;

        if ($scope.participant.participantType == "assignee" || $scope.participant.participantType == "owner") {
            params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
            params.filter = 'fq="object_type_s": USER &fq="status_lcs": VALID &fq="groups_id_ss": ' + $scope.owningGroup;
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
        } else if ($scope.participant.participantType != "owning group" && $scope.participant.participantType.lastIndexOf("group-", 0) != 0) {
            params.header = $translate.instant("common.directive.coreParticipants.modal.dialogUserPicker.header");
            params.filter = '"Object Type": USER &fq="status_lcs": VALID';
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");
        } else {
            params.header = $translate.instant("common.directive.coreParticipants.modal.dialogGroupPicker.header");
            params.filter = '"Object Type": GROUP &fq="status_lcs": ACTIVE';
            params.config = Util.goodMapValue($scope.config, "dialogGroupPicker");
        }

        var modalInstance = $modal.open({
            templateUrl: "directives/core-participants/core-participants-picker-modal.client.view.html",
            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                $scope.modalInstance = $modalInstance;
                $scope.header = params.header;
                $scope.filter = params.filter;
                $scope.config = params.config;
            } ],
            animation: true,
            size: 'lg',
            backdrop: 'static',
            resolve: {
                params: function() {
                    return params;
                }
            }
        });
        modalInstance.result.then(function(selected) {
            if (!Util.isEmpty(selected)) {
                $scope.participant.participantLdapId = selected.object_id_s;
                $scope.selectedType = selected.object_type_s;

                $scope.participantEdit.participantLdapId = selected.object_id_s;
                $scope.participantEdit.participantType = selected.object_type_s;
            }
        });
    };
} ]);
