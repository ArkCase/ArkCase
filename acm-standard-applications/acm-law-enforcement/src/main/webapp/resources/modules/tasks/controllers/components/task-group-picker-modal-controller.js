'use strict';

angular.module('tasks').controller('Tasks.GroupPickerController', ['$scope', '$modal', '$modalInstance',
    '$translate', 'UtilService', 'ConfigService', '$q', 'owningGroup',
    function ($scope, $modal, $modalInstance, $translate, Util, ConfigService, $q, owningGroup) {

        var promiseConfig = ConfigService.getModuleConfig("tasks");

        $q.all([promiseConfig]).then(function (data) {
            var foundComponent = data[0].components.filter(function(component) { return component.title === 'Info'; });
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
        $scope.pickGroup = function () {

            var params = {};
            $scope.owningGroup = owningGroup;

            params.header = $translate.instant("tasks.comp.groupPickerModal.searchGroupHeader");
            params.filter = 'fq="object_type_s": GROUP';
            params.extraFilter = '&fq="name": ';
            params.config = Util.goodMapValue($scope.config, "dialogUserPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/tasks/views/components/task-group-picker-search-modal.client.view.html",
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
                    $scope.participant.object_type_s = selected.object_type_s;
                    $scope.participant.selectedAssigneeName = selected.name;
                }
            });
        };
    }
]);