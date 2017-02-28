'use strict';

angular.module('cases').controller('Cases.AssigneePickerController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService', 'ConfigService', '$q', 'owningGroup',
    function ($scope, $modal, $modalInstance, $translate, Util, ConfigService, $q, owningGroup) {

        var promiseConfig = ConfigService.getModuleConfig("cases");

        $q.all([promiseConfig]).then(function (data) {
            var foundComponent = data[0].components.filter(function(component) { return component.title === 'Participants'; });
            $scope.config = foundComponent[0];
        });

        $scope.onClickOk = function () {
            $modalInstance.close({
                participant: $scope.participant
            });
        };
        $scope.onClickCancel = function () {
            $modalInstance.dismiss('cancel');
        };
        $scope.pickAssignee = function () {

            var params = {};
            $scope.owningGroup = owningGroup;

            params.header = $translate.instant("cases.comp.assigneePickerModal.searchAssigneeHeader");
            params.filter = '"Object Type": USER' + '&fq="Group": ' + $scope.owningGroup;
            params.extraFilter = '&fq="name": ';
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/cases/views/components/case-assignee-picker-search-modal.client.view.html",
                controller: ['$scope', '$modalInstance', 'params', function ($scope, $modalInstance, params) {
                    $scope.modalInstance = $modalInstance;
                    $scope.header = params.header;
                    $scope.filter = params.filter;
                    $scope.extraFilter = params.extraFilter;
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
                    $scope.participant.id = selected.id;
                    $scope.participant.selectedAssigneeName = selected.name;
                }
            });
        };
    }
]);