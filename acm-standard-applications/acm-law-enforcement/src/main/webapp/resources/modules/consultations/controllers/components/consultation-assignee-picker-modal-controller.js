'use strict';

angular.module('consultations').controller('Consultations.AssigneePickerController', [ '$scope', '$modal', '$modalInstance', '$translate', 'UtilService', 'ConfigService', '$q', 'owningGroup', function($scope, $modal, $modalInstance, $translate, Util, ConfigService, $q, owningGroup) {

    ConfigService.getComponentConfig("consultations", "participants").then(function(componentConfig) {
        $scope.config = componentConfig;
    });

    $scope.onClickOk = function() {
        $modalInstance.close({
            participant: $scope.participant
        });
    };
    $scope.onClickCancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.pickAssignee = function() {

        var params = {};
        $scope.owningGroup = owningGroup;

        params.header = $translate.instant("consultations.comp.assigneePickerModal.searchAssigneeHeader");
        params.filter = '"Object Type": USER' + '&fq="status_lcs": "VALID"' + '&fq="Group": ' + $scope.owningGroup;
        params.extraFilter = '&fq="name": ';
        params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

        var modalInstance = $modal.open({
            templateUrl: "modules/consultations/views/components/consultation-assignee-picker-search-modal.client.view.html",
            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                $scope.modalInstance = $modalInstance;
                $scope.header = params.header;
                $scope.filter = params.filter;
                $scope.extraFilter = params.extraFilter;
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
                $scope.participant.id = selected.id;
                $scope.participant.selectedAssigneeName = selected.name;
            }
        });
    };
} ]);