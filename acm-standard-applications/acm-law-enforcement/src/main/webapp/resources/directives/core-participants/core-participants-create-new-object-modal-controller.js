'use strict';

angular.module('directives').controller('Directives.CoreParticipantsCreateNewObjectFormModalController', [ '$scope', '$modal', '$modalInstance', '$translate', 'UtilService', 'params', function($scope, $modal, $modalInstance, $translate, Util, paramsOwn) {

    $scope.onClickOk = function() {
        $modalInstance.close({
            participant: $scope.participant,
            isEdit: $scope.isEdit,
            selectedType: $scope.selectedType,
            owningGroup: $scope.owningGroup
        });
    };
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.pickParticipant = function() {

        var params = {};

        params.header = $translate.instant("common.directive.coreParticipantsCreateNewObjectForm.modal.dialogUserGroupPicker.header");
        params.filter = "fq=\"object_type_s\":(GROUP OR USER)&fq=\"status_lcs\":(ACTIVE OR VALID)";
        params.extraFilter = "&fq=\"name\": ";
        params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

        if ($scope.participant.participantType == "assignee" || $scope.participant.participantType == "owner") {
            params.secondGrid = 'true';
        }

        var modalInstance = $modal.open({
            templateUrl: "directives/core-participants/participants-user-group-search.client.view.html",
            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                $scope.modalInstance = $modalInstance;
                $scope.header = params.header;
                $scope.filter = params.filter;
                $scope.config = params.config;
                $scope.secondGrid = params.secondGrid;
                $scope.extraFilter = params.extraFilter;
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
        modalInstance.result.then(function(selection) {

            if (selection) {
                if (selection.object_id_s) {
                    $scope.participant.participantLdapId = selection.object_id_s;
                    $scope.participant.participantFullName = selection.name;
                    $scope.selectedType = selection.object_type_s;
                    $scope.owningGroup = null;
                } else {
                    var selectedObjectType = selection.masterSelectedItem.object_type_s;

                    if (selectedObjectType === 'USER') { // Selected user
                        var selectedUser = selection.masterSelectedItem;
                        var selectedGroup = selection.detailSelectedItems;

                        $scope.participant.participantLdapId = selectedUser.object_id_s;
                        $scope.participant.participantFullName = selectedUser.name;
                        $scope.selectedType = selectedUser.object_type_s;

                        if (selectedGroup) {
                            $scope.owningGroup = {};
                            $scope.owningGroup.participantLdapId = selectedGroup.object_id_s;
                            $scope.owningGroup.participantFullName = selectedGroup.name;
                            $scope.owningGroup.selectedType = selectedGroup.object_type_s;
                        }
                    } else if (selectedObjectType === 'GROUP') { // Selected group
                        var selectedUser = selection.detailSelectedItems;
                        var selectedGroup = selection.masterSelectedItem;
                        if (selectedUser) {
                            $scope.participant.participantLdapId = selectedUser.object_id_s;
                            $scope.participant.participantFullName = selectedUser.name;
                            $scope.selectedType = selectedUser.object_type_s;
                        }
                        $scope.owningGroup = {};
                        $scope.owningGroup.participantLdapId = selectedGroup.object_id_s;
                        $scope.owningGroup.participantFullName = selectedGroup.name;
                        $scope.owningGroup.selectedType = selectedGroup.object_type_s;
                    }
                }
            }
        });
    };
} ]);