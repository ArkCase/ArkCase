'use strict';

angular.module('tasks').controller('Tasks.AssigneePickerController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService', 'ConfigService', '$q', 'owningGroup',
    function ($scope, $modal, $modalInstance, $translate, Util, ConfigService, $q, owningGroup) {

        var promiseConfig = ConfigService.getModuleConfig("tasks");

        $q.all([promiseConfig]).then(function (data) {
            $scope.config = data[0].components[14];
        });

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
        $scope.pickAssignee = function () {

            var params = {};
            $scope.owningGroup = owningGroup;

            params.header = $translate.instant("tasks.comp.assigneePickerModal.searchAssigneeHeader");
            params.filter = '"Object Type": USER' + '&fq="Group": ' + $scope.owningGroup;
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-assignee-picker-search-modal.client.view.html",
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
                    $scope.participant.id = selected.id;
                    $scope.selectedType = selected.object_type_s;
                    $scope.participant.selectedAssigneeName = selected.name;
                }
            });
        };
    }
]);